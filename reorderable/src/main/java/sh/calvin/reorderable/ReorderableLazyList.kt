/*
 * Copyright 2023 Calvin Liang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sh.calvin.reorderable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ReorderableLazyListDefaults {
    val ScrollThreshold = 48.dp
    const val ScrollSpeed = 0.05f
    const val IgnoreContentPaddingForScroll = false
}

/**
 * Creates a [ReorderableLazyListState] that is remembered across compositions.
 *
 * Changes to [lazyListState], [scrollThreshold], [scrollSpeed], and [ignoreContentPaddingForScroll] will result in [ReorderableLazyListState] being updated.
 *
 * @param lazyListState The return value of [rememberLazyListState](androidx.compose.foundation.lazy.LazyListStateKt.rememberLazyListState)
 * @param scrollThreshold The distance in dp from the top or bottom of the list that will trigger scrolling
 * @param scrollSpeed The fraction of the Column's size that will be scrolled when dragging an item within the scrollThreshold
 * @param ignoreContentPaddingForScroll Whether to ignore content padding for scrollThreshold
 * @param onMove The function that is called when an item is moved
 */
@Composable
fun rememberReorderableLazyColumnState(
    lazyListState: LazyListState,
    scrollThreshold: Dp = ReorderableLazyListDefaults.ScrollThreshold,
    scrollSpeed: Float = ReorderableLazyListDefaults.ScrollSpeed,
    ignoreContentPaddingForScroll: Boolean = ReorderableLazyListDefaults.IgnoreContentPaddingForScroll,
    onMove: (from: LazyListItemInfo, to: LazyListItemInfo) -> Unit
) = rememberReorderableLazyListState(
    lazyListState,
    scrollThreshold,
    scrollSpeed,
    Orientation.Vertical,
    ignoreContentPaddingForScroll,
    onMove,
)

/**
 * Creates a [ReorderableLazyListState] that is remembered across compositions.
 *
 * Changes to [lazyListState], [scrollThreshold], [scrollSpeed], and [ignoreContentPaddingForScroll] will result in [ReorderableLazyListState] being updated.
 *
 * @param lazyListState The return value of [rememberLazyListState](androidx.compose.foundation.lazy.LazyListStateKt.rememberLazyListState)
 * @param scrollThreshold The distance in dp from the left or right of the list that will trigger scrolling
 * @param scrollSpeed The fraction of the Row's size that will be scrolled when dragging an item within the scrollThreshold
 * @param ignoreContentPaddingForScroll Whether to ignore content padding for scrollThreshold
 * @param onMove The function that is called when an item is moved
 */
@Composable
fun rememberReorderableLazyRowState(
    lazyListState: LazyListState,
    scrollThreshold: Dp = ReorderableLazyListDefaults.ScrollThreshold,
    scrollSpeed: Float = ReorderableLazyListDefaults.ScrollSpeed,
    ignoreContentPaddingForScroll: Boolean = ReorderableLazyListDefaults.IgnoreContentPaddingForScroll,
    onMove: (from: LazyListItemInfo, to: LazyListItemInfo) -> Unit
) = rememberReorderableLazyListState(
    lazyListState,
    scrollThreshold,
    scrollSpeed,
    Orientation.Horizontal,
    ignoreContentPaddingForScroll,
    onMove,
)

@Composable
internal fun rememberReorderableLazyListState(
    lazyListState: LazyListState,
    scrollThreshold: Dp,
    scrollSpeed: Float,
    orientation: Orientation,
    ignoreContentPaddingForScroll: Boolean,
    onMove: (from: LazyListItemInfo, to: LazyListItemInfo) -> Unit
): ReorderableLazyListState {
    val density = LocalDensity.current
    val scrollThresholdPx = with(density) { scrollThreshold.toPx() }

    val scope = rememberCoroutineScope()
    val onMoveState = rememberUpdatedState(onMove)
    val state = remember(
        scope, lazyListState, scrollThreshold, scrollSpeed, ignoreContentPaddingForScroll
    ) {
        ReorderableLazyListState(
            state = lazyListState,
            scope = scope,
            onMoveState = onMoveState,
            ignoreContentPaddingForScroll = ignoreContentPaddingForScroll,
            scrollThreshold = scrollThresholdPx,
            scrollSpeed = scrollSpeed,
            orientation = orientation,
        )
    }
    return state
}

private fun LazyListLayoutInfo.getContentOffset(
    orientation: Orientation, ignoreContentPadding: Boolean
): Pair<Int, Int> {
    val contentStartOffset = 0
    val contentPadding = if (!ignoreContentPadding) {
        beforeContentPadding - afterContentPadding
    } else {
        0
    }
    val contentEndOffset = when (orientation) {
        Orientation.Vertical -> viewportSize.height
        Orientation.Horizontal -> viewportSize.width
    } - contentPadding

    return contentStartOffset to contentEndOffset
}

private class ProgrammaticScroller(
    private val state: LazyListState,
    private val scope: CoroutineScope,
    private val orientation: Orientation,
    private val ignoreContentPaddingForScroll: Boolean,
    private val scrollSpeed: Float,
    private val reorderableKeys: HashSet<Any?>,
    private val swapItems: (
        draggingItem: LazyListItemInfo, targetItem: LazyListItemInfo
    ) -> Unit,
) {
    enum class ProgrammaticScrollDirection {
        BACKWARD, FORWARD
    }

    private data class ScrollJobInfo(
        val direction: ProgrammaticScrollDirection,
        val speedMultiplier: Float,
    )

    private var programmaticScrollJobInfo: ScrollJobInfo? = null
    private var programmaticScrollJob: Job? = null
    val isScrolling: Boolean
        get() = programmaticScrollJobInfo != null

    fun start(
        draggingItemProvider: () -> LazyListItemInfo?,
        direction: ProgrammaticScrollDirection,
        speedMultiplier: Float = 1f,
    ) {
        val scrollJobInfo = ScrollJobInfo(direction, speedMultiplier)

        if (programmaticScrollJobInfo == scrollJobInfo) return

        val viewportSize = when (orientation) {
            Orientation.Vertical -> state.layoutInfo.viewportSize.height
            Orientation.Horizontal -> state.layoutInfo.viewportSize.width
        }
        val multipliedScrollOffset = viewportSize * scrollSpeed * speedMultiplier

        programmaticScrollJob?.cancel()
        programmaticScrollJobInfo = null

        if (!canScroll(direction)) return

        programmaticScrollJobInfo = scrollJobInfo
        programmaticScrollJob = scope.launch {
            while (true) {
                try {
                    if (!canScroll(direction)) break

                    val duration = 100L
                    val diff = when (direction) {
                        ProgrammaticScrollDirection.BACKWARD -> -multipliedScrollOffset
                        ProgrammaticScrollDirection.FORWARD -> multipliedScrollOffset
                    }
                    launch {
                        state.animateScrollBy(
                            diff, tween(durationMillis = duration.toInt(), easing = LinearEasing)
                        )
                    }

                    launch {
                        // keep dragging item in visible area to prevent it from disappearing
                        swapDraggingItemToEndIfNecessary(draggingItemProvider, direction)
                    }

                    delay(duration)
                } catch (e: Exception) {
                    break
                }
            }
        }
    }

    private fun canScroll(direction: ProgrammaticScrollDirection): Boolean {
        return when (direction) {
            ProgrammaticScrollDirection.BACKWARD -> state.canScrollBackward
            ProgrammaticScrollDirection.FORWARD -> state.canScrollForward
        }
    }

    /**
     * Swap the dragging item with first item in the visible area in the direction of scrolling.
     */
    private fun swapDraggingItemToEndIfNecessary(
        draggingItemProvider: () -> LazyListItemInfo?,
        direction: ProgrammaticScrollDirection,
    ) {
        val draggingItem = draggingItemProvider() ?: return
        val itemsInContentArea = state.layoutInfo.getItemsInContentArea(
            orientation, ignoreContentPaddingForScroll
        )
        val draggingItemIsAtTheEnd = when (direction) {
            ProgrammaticScrollDirection.BACKWARD -> itemsInContentArea.firstOrNull()?.index?.let { draggingItem.index < it }
            ProgrammaticScrollDirection.FORWARD -> itemsInContentArea.lastOrNull()?.index?.let { draggingItem.index > it }
        } ?: false

        if (draggingItemIsAtTheEnd) return

        val isReorderable = { item: LazyListItemInfo -> item.key in reorderableKeys }

        val targetItem = itemsInContentArea.let {
            when (direction) {
                ProgrammaticScrollDirection.BACKWARD -> it.find(isReorderable)
                ProgrammaticScrollDirection.FORWARD -> it.findLast(isReorderable)
            }
        }
        if (targetItem != null && targetItem.index != draggingItem.index) {
            swapItems(draggingItem, targetItem)
        }
    }

    fun stop() {
        programmaticScrollJob?.cancel()
        programmaticScrollJobInfo = null
    }

    private fun LazyListLayoutInfo.getItemsInContentArea(
        orientation: Orientation, ignoreContentPadding: Boolean
    ): List<LazyListItemInfo> {
        val (contentStartOffset, contentEndOffset) = getContentOffset(
            orientation, ignoreContentPadding
        )
        return visibleItemsInfo.filter { item ->
            item.offset >= contentStartOffset && item.offset + item.size <= contentEndOffset
        }
    }
}

// base on https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/foundation/foundation/integration-tests/foundation-demos/src/main/java/androidx/compose/foundation/demos/LazyColumnDragAndDropDemo.kt;drc=edde6e8b9d304264598f962a3b0e5c267e1373bb
class ReorderableLazyListState internal constructor(
    private val state: LazyListState,
    private val scope: CoroutineScope,
    private val onMoveState: State<(from: LazyListItemInfo, to: LazyListItemInfo) -> Unit>,
    internal val orientation: Orientation,

    /**
     * Whether to ignore content padding for scrollThreshold.
     */
    private val ignoreContentPaddingForScroll: Boolean,

    /**
     * The threshold in pixels for scrolling the list when dragging an item.
     * If the dragged item is within this threshold of the top or bottom of the list, the list will scroll.
     * Must be greater than 0.
     */
    private val scrollThreshold: Float,
    scrollSpeed: Float,
) {
    private var draggingItemKey by mutableStateOf<Any?>(null)
    private val draggingItemIndex: Int?
        get() = draggingItemLayoutInfo?.index

    private var draggingItemDraggedDelta by mutableFloatStateOf(0f)
    private var draggingItemInitialOffset by mutableIntStateOf(0)

    // visibleItemsInfo doesn't update immediately after onMove, draggingItemLayoutInfo.item may be outdated for a short time.
    // not a clean solution, but it works.
    private var draggingItemTargetIndex: Int? = null
    private var predictedDraggingItemOffset: Int? = null
    private val draggingItemLayoutInfo: LazyListItemInfo?
        get() = state.layoutInfo.visibleItemsInfo.firstOrNull { it.key == draggingItemKey }
    internal val draggingItemOffset: Float
        get() = draggingItemLayoutInfo?.let { item ->
            val offset = if (item.index == draggingItemTargetIndex) {
                predictedDraggingItemOffset = null
                item.offset
            } else {
                predictedDraggingItemOffset ?: item.offset
            }
            draggingItemInitialOffset + draggingItemDraggedDelta - offset
        } ?: 0f

    // the offset of the handle center from the top or left of the dragging item when dragging starts
    private var draggingItemHandleOffset = 0f

    internal val reorderableKeys = HashSet<Any?>()

    private val programmaticScroller = ProgrammaticScroller(
        state,
        scope,
        orientation,
        ignoreContentPaddingForScroll,
        scrollSpeed,
        reorderableKeys,
        this::swapItems
    )

    internal var previousDraggingItemKey by mutableStateOf<Any?>(null)
        private set
    internal var previousDraggingItemOffset = Animatable(0f)
        private set

    internal fun onDragStart(key: Any, handleOffset: Float) {
        state.layoutInfo.visibleItemsInfo.firstOrNull { item ->
            item.key == key
        }?.also {
            draggingItemKey = key
            draggingItemInitialOffset = it.offset
            draggingItemHandleOffset = handleOffset
        }

        // TODO: if item isn't fully in view, scroll to it
    }

    internal fun onDragStop() {
        if (draggingItemIndex != null) {
            previousDraggingItemKey = draggingItemKey
            val startOffset = draggingItemOffset
            scope.launch {
                previousDraggingItemOffset.snapTo(startOffset)
                previousDraggingItemOffset.animateTo(
                    0f, spring(
                        stiffness = Spring.StiffnessMediumLow, visibilityThreshold = 1f
                    )
                )
                previousDraggingItemKey = null
            }
        }
        draggingItemDraggedDelta = 0f
        draggingItemKey = null
        draggingItemInitialOffset = 0
        programmaticScroller.stop()
        draggingItemTargetIndex = null
        predictedDraggingItemOffset = null
    }

    internal fun onDrag(offset: Float) {
        draggingItemDraggedDelta += offset

        val draggingItem = draggingItemLayoutInfo ?: return
        val startOffset = draggingItem.offset + draggingItemOffset
        val endOffset = startOffset + draggingItem.size
        val (contentStartOffset, contentEndOffset) = state.layoutInfo.getContentOffset(
            orientation, ignoreContentPaddingForScroll
        )

        if (!programmaticScroller.isScrolling) {
            // find a target item to swap with
            val targetItem = state.layoutInfo.visibleItemsInfo.find { item ->
                item.offsetMiddle in startOffset..endOffset && draggingItem.index != item.index && item.key in reorderableKeys
            }
            if (targetItem != null) {
                swapItems(draggingItem, targetItem)
            }
        }

        // check if the handle center is in the scroll threshold
        val distanceFromStart = (startOffset + draggingItemHandleOffset) - contentStartOffset
        val distanceFromEnd = contentEndOffset - (startOffset + draggingItemHandleOffset)

        if (distanceFromStart < scrollThreshold) {
            programmaticScroller.start(
                { draggingItemLayoutInfo },
                ProgrammaticScroller.ProgrammaticScrollDirection.BACKWARD,
                getScrollSpeedMultiplier(distanceFromStart)
            )
        } else if (distanceFromEnd < scrollThreshold) {
            programmaticScroller.start(
                { draggingItemLayoutInfo },
                ProgrammaticScroller.ProgrammaticScrollDirection.FORWARD,
                getScrollSpeedMultiplier(distanceFromEnd)
            )
        } else {
            programmaticScroller.stop()
        }
    }

    private fun swapItems(
        draggingItem: LazyListItemInfo, targetItem: LazyListItemInfo
    ) {
        if (draggingItem.index == targetItem.index) return

        predictedDraggingItemOffset = if (targetItem.index > draggingItem.index) {
            targetItem.size + targetItem.offset - draggingItem.size
        } else {
            targetItem.offset
        }
        draggingItemTargetIndex = targetItem.index

        val scrollToIndex = if (targetItem.index == state.firstVisibleItemIndex) {
            draggingItem.index
        } else if (draggingItem.index == state.firstVisibleItemIndex) {
            targetItem.index
        } else {
            null
        }
        if (scrollToIndex != null) {
            scope.launch {
                // this is needed to neutralize automatic keeping the first item first.
                state.scrollToItem(scrollToIndex, state.firstVisibleItemScrollOffset)
                onMoveState.value(draggingItem, targetItem)
            }
        } else {
            onMoveState.value(draggingItem, targetItem)
        }
    }

    internal fun isAnItemDragging(): State<Boolean> {
        return derivedStateOf {
            draggingItemKey != null
        }
    }

    internal fun isItemDragging(key: Any): State<Boolean> {
        return derivedStateOf {
            key == draggingItemKey
        }
    }

    private fun getScrollSpeedMultiplier(distance: Float): Float {
        // map distance in scrollThreshold..-scrollThreshold to 1..10
        return (1 - ((distance + scrollThreshold) / (scrollThreshold * 2)).coerceIn(0f, 1f)) * 10
    }

    private val LazyListItemInfo.offsetMiddle: Float
        get() = offset + size / 2f
}

interface ReorderableItemScope {
    /**
     * Make the UI element the draggable handle for the reorderable item.
     *
     * This modifier can only be used on the UI element that is a child of [LazyItemScope.ReorderableItem].
     *
     * @param enabled Whether or not drag is enabled
     * @param interactionSource [MutableInteractionSource] that will be used to emit [DragInteraction.Start] when this draggable is being dragged.
     * @param onDragStarted The function that is called when the item starts being dragged
     * @param onDragStopped The function that is called when the item stops being dragged
     */
    fun Modifier.draggableHandle(
        enabled: Boolean = true,
        onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit = {},
        onDragStopped: suspend CoroutineScope.(velocity: Float) -> Unit = {},
        interactionSource: MutableInteractionSource? = null,
    ): Modifier
}

internal class ReorderableItemScopeImpl(
    private val reorderableLazyListState: ReorderableLazyListState,
    private val key: Any,
    private val orientation: Orientation,
    private val itemPositionProvider: () -> Float
) : ReorderableItemScope {
    override fun Modifier.draggableHandle(
        enabled: Boolean,
        onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit,
        onDragStopped: suspend CoroutineScope.(velocity: Float) -> Unit,
        interactionSource: MutableInteractionSource?,
    ) = composed {
        var handleOffset = remember { 0f }
        var handleSize = remember { 0 }
        onGloballyPositioned {
            handleOffset = when (orientation) {
                Orientation.Vertical -> it.positionInRoot().y
                Orientation.Horizontal -> it.positionInRoot().x
            }
            handleSize = when (orientation) {
                Orientation.Vertical -> it.size.height
                Orientation.Horizontal -> it.size.width
            }
        }.draggable(
            state = rememberDraggableState { reorderableLazyListState.onDrag(offset = it) },
            orientation = orientation,
            enabled = enabled && (reorderableLazyListState.isItemDragging(key).value || !reorderableLazyListState.isAnItemDragging().value),
            interactionSource = interactionSource,
            onDragStarted = {
                val handleOffsetRelativeToItem = handleOffset - itemPositionProvider()
                val handleCenter = handleOffsetRelativeToItem + handleSize / 2f

                reorderableLazyListState.onDragStart(key, handleCenter)
                onDragStarted(it)
            },
            onDragStopped = {
                reorderableLazyListState.onDragStop()
                onDragStopped(it)
            },
        )
    }
}

/**
 * A composable that allows items in a LazyColumn to be reordered by dragging.
 *
 * @param state The return value of [rememberReorderableLazyColumnState] or [rememberReorderableLazyRowState]
 * @param key The key of the item, must be the same as the key passed to [LazyColumn.item](androidx.compose.foundation.lazy.LazyDsl.item), [LazyRow.item](androidx.compose.foundation.lazy.LazyDsl.item) or similar functions in [LazyListScope](androidx.compose.foundation.lazy.LazyListScope)
 * @param enabled Whether or this item is reorderable
 */
@ExperimentalFoundationApi
@Composable
fun LazyItemScope.ReorderableItem(
    reorderableLazyListState: ReorderableLazyListState,
    key: Any,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable ReorderableItemScope.(isDragging: Boolean) -> Unit
) {
    val orientation = reorderableLazyListState.orientation
    val dragging by reorderableLazyListState.isItemDragging(key)
    var itemPosition = remember { 0f }
    val draggingModifier = if (dragging) {
        Modifier
            .zIndex(1f)
            .then(when (orientation) {
                Orientation.Vertical -> Modifier.graphicsLayer {
                    translationY = reorderableLazyListState.draggingItemOffset
                }

                Orientation.Horizontal -> Modifier.graphicsLayer {
                    translationX = reorderableLazyListState.draggingItemOffset
                }
            })
    } else if (key == reorderableLazyListState.previousDraggingItemKey) {
        Modifier
            .zIndex(1f)
            .then(when (orientation) {
                Orientation.Vertical -> Modifier.graphicsLayer {
                    translationY = reorderableLazyListState.previousDraggingItemOffset.value
                }

                Orientation.Horizontal -> Modifier.graphicsLayer {
                    translationX = reorderableLazyListState.previousDraggingItemOffset.value
                }
            })
    } else {
        Modifier.animateItemPlacement()
    }.onGloballyPositioned {
        itemPosition = when (orientation) {
            Orientation.Vertical -> it.positionInRoot().y
            Orientation.Horizontal -> it.positionInRoot().x
        }
    }

    Column(modifier = modifier.then(draggingModifier)) {
        ReorderableItemScopeImpl(
            reorderableLazyListState,
            key,
            orientation,
            { itemPosition }).content(dragging)
    }

    LaunchedEffect(enabled) {
        if (enabled) {
            reorderableLazyListState.reorderableKeys.add(key)
        } else {
            reorderableLazyListState.reorderableKeys.remove(key)
        }
    }
}