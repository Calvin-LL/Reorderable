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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object ReorderableLazyListDefaults {
    val ScrollThreshold = 48.dp
}

private const val ScrollAmountMultiplier = 0.05f

/**
 * Creates a [ReorderableLazyListState] that is remembered across compositions.
 *
 * Changes to [lazyListState], [scrollThreshold], [scrollThresholdPadding], and [scroller] will result in [ReorderableLazyListState] being updated.
 *
 * @param lazyListState The return value of [rememberLazyListState](androidx.compose.foundation.lazy.LazyListStateKt.rememberLazyListState)
 * @param scrollThresholdPadding The padding that will be added to the top and bottom of the list to determine the scrollThreshold
 * @param scrollThreshold The distance in dp from the top or bottom of the list that will trigger scrolling
 * @param scroller The [Scroller] that will be used to scroll the list. Use [rememberScroller](sh.calvin.reorderable.ScrollerKt.rememberScroller) to create a [Scroller].
 * @param onMove The function that is called when an item is moved
 */
@Composable
fun rememberReorderableLazyColumnState(
    lazyListState: LazyListState,
    scrollThresholdPadding: PaddingValues = PaddingValues(0.dp),
    scrollThreshold: Dp = ReorderableLazyListDefaults.ScrollThreshold,
    scroller: Scroller = rememberScroller(
        scrollableState = lazyListState,
        pixelAmount = lazyListState.layoutInfo.viewportSize.height * ScrollAmountMultiplier,
    ),
    onMove: (from: LazyListItemInfo, to: LazyListItemInfo) -> Unit,
) = rememberReorderableLazyListState(
    lazyListState,
    scrollThresholdPadding,
    scrollThreshold,
    scroller,
    Orientation.Vertical,
    onMove,
)

/**
 * Creates a [ReorderableLazyListState] that is remembered across compositions.
 *
 * Changes to [lazyListState], [scrollThreshold], [scrollSpeed], and [scrollThresholdPadding] will result in [scroller] being updated.
 *
 * @param lazyListState The return value of [rememberLazyListState](androidx.compose.foundation.lazy.LazyListStateKt.rememberLazyListState)
 * @param scrollThresholdPadding The padding that will be added to the left and right of the list to determine the scrollThreshold
 * @param scrollThreshold The distance in dp from the left or right of the list that will trigger scrolling
 * @param scroller The [Scroller] that will be used to scroll the list. Use [rememberScroller](sh.calvin.reorderable.ScrollerKt.rememberScroller) to create a [Scroller].
 * @param onMove The function that is called when an item is moved
 */
@Composable
fun rememberReorderableLazyRowState(
    lazyListState: LazyListState,
    scrollThresholdPadding: PaddingValues = PaddingValues(0.dp),
    scrollThreshold: Dp = ReorderableLazyListDefaults.ScrollThreshold,
    scroller: Scroller = rememberScroller(
        scrollableState = lazyListState,
        pixelAmount = lazyListState.layoutInfo.viewportSize.width * ScrollAmountMultiplier,
    ),
    onMove: (from: LazyListItemInfo, to: LazyListItemInfo) -> Unit,
) = rememberReorderableLazyListState(
    lazyListState,
    scrollThresholdPadding,
    scrollThreshold,
    scroller,
    Orientation.Horizontal,
    onMove,
)

@Composable
internal fun rememberReorderableLazyListState(
    lazyListState: LazyListState,
    scrollThresholdPadding: PaddingValues,
    scrollThreshold: Dp,
    scroller: Scroller,
    orientation: Orientation,
    onMove: (from: LazyListItemInfo, to: LazyListItemInfo) -> Unit,
): ReorderableLazyListState {
    val density = LocalDensity.current
    val scrollThresholdPx = with(density) { scrollThreshold.toPx() }

    val scope = rememberCoroutineScope()
    val onMoveState = rememberUpdatedState(onMove)
    val layoutDirection = LocalLayoutDirection.current
    val absoluteScrollThresholdPadding = AbsolutePixelPadding(
        start = with(density) {
            scrollThresholdPadding.calculateStartPadding(layoutDirection).toPx()
        },
        end = with(density) {
            scrollThresholdPadding.calculateEndPadding(layoutDirection).toPx()
        },
        top = with(density) { scrollThresholdPadding.calculateTopPadding().toPx() },
        bottom = with(density) { scrollThresholdPadding.calculateBottomPadding().toPx() },
    )
    val state = remember(
        scope, lazyListState, scrollThreshold, scrollThresholdPadding, scroller,
    ) {
        ReorderableLazyListState(
            state = lazyListState,
            scope = scope,
            onMoveState = onMoveState,
            orientation = orientation,
            scrollThreshold = scrollThresholdPx,
            scrollThresholdPadding = absoluteScrollThresholdPadding,
            scroller = scroller,
        )
    }
    return state
}

internal data class AbsolutePixelPadding(
    val start: Float,
    val end: Float,
    val top: Float,
    val bottom: Float,
)

// base on https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/foundation/foundation/integration-tests/foundation-demos/src/main/java/androidx/compose/foundation/demos/LazyColumnDragAndDropDemo.kt;drc=edde6e8b9d304264598f962a3b0e5c267e1373bb
class ReorderableLazyListState internal constructor(
    private val state: LazyListState,
    private val scope: CoroutineScope,
    private val onMoveState: State<(from: LazyListItemInfo, to: LazyListItemInfo) -> Unit>,
    internal val orientation: Orientation,

    /**
     * The threshold in pixels for scrolling the list when dragging an item.
     * If the dragged item is within this threshold of the top or bottom of the list, the list will scroll.
     * Must be greater than 0.
     */
    private val scrollThreshold: Float,
    scrollThresholdPadding: AbsolutePixelPadding,
    private val scroller: Scroller,
) {
    private var draggingItemKey by mutableStateOf<Any?>(null)
    private val draggingItemIndex: Int?
        get() = draggingItemLayoutInfo?.index

    /**
     * Whether any item is being dragged. This property is observable.
     */
    val isAnyItemDragging by derivedStateOf {
        draggingItemKey != null
    }

    private var draggingItemDraggedDelta by mutableFloatStateOf(0f)
    private var draggingItemInitialOffset by mutableIntStateOf(0)

    private var draggingItemTargetIndex: Int? = null
    private val draggingItemLayoutInfo: LazyListItemInfo?
        get() = state.layoutInfo.visibleItemsInfo.firstOrNull { it.key == draggingItemKey }
    internal val draggingItemOffset: Float
        get() = draggingItemLayoutInfo?.let { item ->
            draggingItemInitialOffset + draggingItemDraggedDelta - item.offset
        } ?: 0f

    // the offset of the handle center from the top or left of the dragging item when dragging starts
    private var draggingItemHandleOffset = 0f

    internal val reorderableKeys = HashSet<Any?>()

    internal var previousDraggingItemKey by mutableStateOf<Any?>(null)
        private set
    internal var previousDraggingItemOffset = Animatable(0f)
        private set

    private val scrollThresholdPadding = when (orientation) {
        Orientation.Vertical -> scrollThresholdPadding.top to scrollThresholdPadding.bottom
        Orientation.Horizontal -> scrollThresholdPadding.start to scrollThresholdPadding.end
    }

    internal suspend fun onDragStart(key: Any, handleOffset: Float) {
        state.layoutInfo.visibleItemsInfo.firstOrNull { item ->
            item.key == key
        }?.also {
            if (it.offset < 0) {
                // if item is not fully in view, scroll to it
                state.animateScrollBy(it.offset.toFloat(), spring())
            }

            draggingItemKey = key
            draggingItemInitialOffset = it.offset
            draggingItemHandleOffset = handleOffset
        }
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
        scroller.stop()
        draggingItemTargetIndex = null
    }

    internal fun onDrag(offset: Float) {
        draggingItemDraggedDelta += offset

        val draggingItem = draggingItemLayoutInfo ?: return
        // draggingItem.offset is the distance of the start of the dragging item from the top or left of the list
        // state.layoutInfo.mainAxisItemSpacing is included in the offset
        // (e.g. the first item's offset is 0, despite being state.layoutInfo.mainAxisItemSpacing pixels from the top or left of the list)
        val startOffset = draggingItem.offset + draggingItemOffset
        val (contentStartOffset, contentEndOffset) = state.layoutInfo.getContentOffset(
            orientation, scrollThresholdPadding
        )

        if (!scroller.isScrolling) {
            val endOffset = startOffset + draggingItem.size
            // find a target item to swap with
            val targetItem = state.layoutInfo.visibleItemsInfo.find { item ->
                item.offsetMiddle in startOffset..endOffset && draggingItem.index != item.index && item.key in reorderableKeys && item.offset >= contentStartOffset && item.offset + item.size <= contentEndOffset
            }
            if (targetItem != null) {
                swapItems(draggingItem, targetItem)
            }
        }

        // the actual distance from the top or left of the list to the top or left of the dragging item
        val startOffsetWithSpacing = startOffset + state.layoutInfo.mainAxisItemSpacing
        // the distance from the top or left of the list to the center of the dragging item handle
        val handleOffset = startOffsetWithSpacing + draggingItemHandleOffset

        // check if the handle center is in the scroll threshold
        val distanceFromStart = handleOffset - contentStartOffset
        val distanceFromEnd = contentEndOffset - handleOffset

        if (distanceFromStart < scrollThreshold) {
            scroller.start(
                Scroller.Direction.BACKWARD,
                getScrollSpeedMultiplier(distanceFromStart),
                onScroll = {
                    swapDraggingItemToEndIfNecessary(Scroller.Direction.BACKWARD)
                }
            )
        } else if (distanceFromEnd < scrollThreshold) {
            scroller.start(
                Scroller.Direction.FORWARD,
                getScrollSpeedMultiplier(distanceFromEnd),
                onScroll = {
                    swapDraggingItemToEndIfNecessary(Scroller.Direction.FORWARD)
                }
            )
        } else {
            scroller.stop()
        }
    }

    // keep dragging item in visible area to prevent it from disappearing
    private fun swapDraggingItemToEndIfNecessary(
        direction: Scroller.Direction,
    ) {
        val draggingItem = draggingItemLayoutInfo ?: return
        val itemsInContentArea = state.layoutInfo.getItemsInContentArea(
            orientation, scrollThresholdPadding
        )
        val draggingItemIsAtTheEnd = when (direction) {
            Scroller.Direction.BACKWARD -> itemsInContentArea.firstOrNull()?.index?.let { draggingItem.index < it }
            Scroller.Direction.FORWARD -> itemsInContentArea.lastOrNull()?.index?.let { draggingItem.index > it }
        } ?: false

        if (draggingItemIsAtTheEnd) return

        val isReorderable = { item: LazyListItemInfo -> item.key in reorderableKeys }

        val targetItem = itemsInContentArea.let {
            when (direction) {
                Scroller.Direction.BACKWARD -> it.find(isReorderable)
                Scroller.Direction.FORWARD -> it.findLast(isReorderable)
            }
        }
        if (targetItem != null && targetItem.index != draggingItem.index) {
            swapItems(draggingItem, targetItem)
        }
    }

    private fun LazyListLayoutInfo.getItemsInContentArea(
        orientation: Orientation,
        padding: Pair<Float, Float>,
    ): List<LazyListItemInfo> {
        val (contentStartOffset, contentEndOffset) = getContentOffset(
            orientation, padding
        )
        return visibleItemsInfo.filter { item ->
            item.offset >= contentStartOffset && item.offset + item.size <= contentEndOffset
        }
    }

    private fun LazyListLayoutInfo.getContentOffset(
        orientation: Orientation,
        padding: Pair<Float, Float>,
    ): Pair<Float, Float> {
        val contentStartOffset = padding.first
        val contentEndOffset = when (orientation) {
            Orientation.Vertical -> viewportSize.height
            Orientation.Horizontal -> viewportSize.width
        } - padding.second

        return contentStartOffset to contentEndOffset
    }

    private fun swapItems(
        draggingItem: LazyListItemInfo, targetItem: LazyListItemInfo,
    ) {
        if (draggingItem.index == targetItem.index) return

        draggingItemTargetIndex = targetItem.index

        // TODO: when `requestScrollToItem` is released uncomment this and remove `scrollToIndex` and the following `if` block
        // see https://android-review.googlesource.com/c/platform/frameworks/support/+/2987293
        // see https://github.com/Calvin-LL/Reorderable/issues/4
//        if (
//            draggingItem.index == state.firstVisibleItemIndex ||
//            targetItem.index == state.firstVisibleItemIndex
//        ) {
//            state.requestScrollToItem(
//                state.firstVisibleItemIndex,
//                state.firstVisibleItemScrollOffset
//            )
//        }
//
//        onMoveState.value(draggingItem, targetItem)

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
                // see https://github.com/Calvin-LL/Reorderable/issues/4
                state.scrollToItem(scrollToIndex, state.firstVisibleItemScrollOffset)
                onMoveState.value(draggingItem, targetItem)
            }
        } else {
            onMoveState.value(draggingItem, targetItem)
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


    /**
     * Make the UI element the draggable handle for the reorderable item. Drag will start only after a long press.
     *
     * This modifier can only be used on the UI element that is a child of [LazyItemScope.ReorderableItem].
     *
     * @param enabled Whether or not drag is enabled
     * @param onDragStarted The function that is called when the item starts being dragged
     * @param onDragStopped The function that is called when the item stops being dragged
     */
    fun Modifier.longPressDraggableHandle(
        enabled: Boolean = true,
        onDragStarted: (startedPosition: Offset) -> Unit = {},
        onDragStopped: () -> Unit = {},
        interactionSource: MutableInteractionSource? = null,
    ): Modifier
}

internal class ReorderableItemScopeImpl(
    private val reorderableLazyListState: ReorderableLazyListState,
    private val key: Any,
    private val orientation: Orientation,
    private val itemPositionProvider: () -> Float,
) : ReorderableItemScope {

    override fun Modifier.draggableHandle(
        enabled: Boolean,
        onDragStarted: suspend CoroutineScope.(startedPosition: Offset) -> Unit,
        onDragStopped: suspend CoroutineScope.(velocity: Float) -> Unit,
        interactionSource: MutableInteractionSource?,
    ) = composed {
        var handleOffset by remember { mutableStateOf(0f) }
        var handleSize by remember { mutableStateOf(0) }

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
            enabled = enabled && (reorderableLazyListState.isItemDragging(key).value || !reorderableLazyListState.isAnyItemDragging),
            interactionSource = interactionSource,
            onDragStarted = {
                launch {
                    val handleOffsetRelativeToItem = handleOffset - itemPositionProvider()
                    val handleCenter = handleOffsetRelativeToItem + handleSize / 2f

                    reorderableLazyListState.onDragStart(key, handleCenter)
                }
                onDragStarted(it)
            },
            onDragStopped = {
                reorderableLazyListState.onDragStop()
                onDragStopped(it)
            },
        )
    }

    override fun Modifier.longPressDraggableHandle(
        enabled: Boolean,
        onDragStarted: (startedPosition: Offset) -> Unit,
        onDragStopped: () -> Unit,
        interactionSource: MutableInteractionSource?,
    ) = composed {
        var handleOffset by remember { mutableStateOf(0f) }
        var handleSize by remember { mutableStateOf(0) }

        val coroutineScope = rememberCoroutineScope()

        onGloballyPositioned {
            handleOffset = when (orientation) {
                Orientation.Vertical -> it.positionInRoot().y
                Orientation.Horizontal -> it.positionInRoot().x
            }
            handleSize = when (orientation) {
                Orientation.Vertical -> it.size.height
                Orientation.Horizontal -> it.size.width
            }
        }.longPressDraggable(
            enabled = enabled && (reorderableLazyListState.isItemDragging(key).value || !reorderableLazyListState.isAnyItemDragging),
            interactionSource = interactionSource,
            onDragStarted = {
                coroutineScope.launch {
                    val handleOffsetRelativeToItem = handleOffset - itemPositionProvider()
                    val handleCenter = handleOffsetRelativeToItem + handleSize / 2f

                    reorderableLazyListState.onDragStart(key, handleCenter)
                }
                onDragStarted(it)
            },
            onDragStopped = {
                reorderableLazyListState.onDragStop()
                onDragStopped()
            },
            onDrag = { _, dragAmount ->
                reorderableLazyListState.onDrag(
                    offset = when (orientation) {
                        Orientation.Vertical -> dragAmount.y
                        Orientation.Horizontal -> dragAmount.x
                    }
                )
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
    content: @Composable ReorderableItemScope.(isDragging: Boolean) -> Unit,
) {
    val orientation = reorderableLazyListState.orientation
    val dragging by reorderableLazyListState.isItemDragging(key)
    var itemPosition by remember { mutableStateOf(0f) }
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

    Box(modifier = modifier.then(draggingModifier)) {
        ReorderableItemScopeImpl(
            reorderableLazyListState,
            key,
            orientation,
            { itemPosition },
        ).content(dragging)
    }

    LaunchedEffect(enabled) {
        if (enabled) {
            reorderableLazyListState.reorderableKeys.add(key)
        } else {
            reorderableLazyListState.reorderableKeys.remove(key)
        }
    }
}
