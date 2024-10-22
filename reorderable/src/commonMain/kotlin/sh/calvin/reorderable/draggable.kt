package sh.calvin.reorderable

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal fun Modifier.draggable(
    key1: Any?,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    onDragStarted: (Offset) -> Unit = { },
    onDragStopped: () -> Unit = { },
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit,
) = composed {
    val coroutineScope = rememberCoroutineScope()
    var dragInteractionStart by remember { mutableStateOf<DragInteraction.Start?>(null) }
    var dragStarted by remember { mutableStateOf(false) }

    DisposableEffect(key1) {
        onDispose {
            if (dragStarted) {
                dragInteractionStart?.also {
                    coroutineScope.launch {
                        interactionSource?.emit(DragInteraction.Cancel(it))
                    }
                }

                if (dragStarted) {
                    onDragStopped()
                }

                dragStarted = false
            }
        }
    }

    pointerInput(key1, enabled) {
        if (enabled) {
            detectDragGestures(
                onDragStart = {
                    dragStarted = true
                    dragInteractionStart = DragInteraction.Start().also {
                        coroutineScope.launch {
                            interactionSource?.emit(it)
                        }
                    }

                    onDragStarted(it)
                },
                onDragEnd = {
                    dragInteractionStart?.also {
                        coroutineScope.launch {
                            interactionSource?.emit(DragInteraction.Stop(it))
                        }
                    }

                    if (dragStarted) {
                        onDragStopped()
                    }

                    dragStarted = false
                },
                onDragCancel = {
                    dragInteractionStart?.also {
                        coroutineScope.launch {
                            interactionSource?.emit(DragInteraction.Cancel(it))
                        }
                    }

                    if (dragStarted) {
                        onDragStopped()
                    }

                    dragStarted = false
                },
                onDrag = onDrag,
            )
        }
    }
}

internal fun Modifier.longPressDraggable(
    key1: Any?,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    onDragStarted: (Offset) -> Unit = { },
    onDragStopped: () -> Unit = { },
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit,
) = composed {
    val coroutineScope = rememberCoroutineScope()
    var dragInteractionStart by remember { mutableStateOf<DragInteraction.Start?>(null) }
    var dragStarted by remember { mutableStateOf(false) }

    DisposableEffect(key1) {
        onDispose {
            if (dragStarted) {
                dragInteractionStart?.also {
                    coroutineScope.launch {
                        interactionSource?.emit(DragInteraction.Cancel(it))
                    }
                }

                if (dragStarted) {
                    onDragStopped()
                }

                dragStarted = false
            }
        }
    }

    pointerInput(key1, enabled) {
        if (enabled) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    dragStarted = true
                    dragInteractionStart = DragInteraction.Start().also {
                        coroutineScope.launch {
                            interactionSource?.emit(it)
                        }
                    }

                    onDragStarted(it)
                },
                onDragEnd = {
                    dragInteractionStart?.also {
                        coroutineScope.launch {
                            interactionSource?.emit(DragInteraction.Stop(it))
                        }
                    }

                    if (dragStarted) {
                        onDragStopped()
                    }

                    dragStarted = false

                },
                onDragCancel = {
                    dragInteractionStart?.also {
                        coroutineScope.launch {
                            interactionSource?.emit(DragInteraction.Cancel(it))
                        }
                    }

                    if (dragStarted) {
                        onDragStopped()
                    }

                    dragStarted = false

                },
                onDrag = onDrag,
            )
        }
    }
}

internal fun Modifier.combinedGesture(
    key1: Any?,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit = {},
    onLongPress: () -> Unit = {},
    onDragStarted: (Offset) -> Unit = {},
    onDragStopped: () -> Unit = {},
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit,
) = composed {
    val coroutineScope = rememberCoroutineScope()

    pointerInput(key1, enabled) {
        val touchSlop = viewConfiguration.touchSlop
        if (enabled) {
            awaitPointerEventScope {
                while (true) {
                    // Wait for the finger to press down
                    val down = awaitPointerEvent().changes.firstOrNull()?.takeIf { it.pressed } ?: continue

                    // Initialize variables
                    var longPressed = false
                    var isDragging = false
                    var totalMovement = Offset.Zero
                    var pastTouchSlop = false
                    var dragInteractionStart: DragInteraction.Start? = null

                    // Start long-press job
                    val longPressTimeout = viewConfiguration.longPressTimeoutMillis
                    val longPressJob = coroutineScope.launch {
                        delay(longPressTimeout)
                        if (down.pressed) {
                            longPressed = true
                            onLongPress()
                        }
                    }

                    // Gesture processing loop
                    while (down.pressed) {
                        val event = awaitPointerEvent()
                        val change = event.changes.first()
                        val positionChange = change.positionChange()
                        totalMovement += positionChange
                        val distance = totalMovement.getDistance()

                        // Check if the movement exceeds the touch slop
                        if (!longPressed && !pastTouchSlop && distance > touchSlop) {
                            pastTouchSlop = true
                            longPressJob.cancel()
                            // Continue processing, wait for the finger to lift up
                        }

                        // Handle drag after long-press
                        if (positionChange != Offset.Zero && longPressed) {
                            isDragging = true
                            longPressJob.cancel()
                            dragInteractionStart = DragInteraction.Start().also {
                                coroutineScope.launch {
                                    interactionSource?.emit(it)
                                }
                            }
                            onDragStarted(change.position)
                            // Consume the event to prevent LazyColumn scrolling
                            change.consume()
                            break
                        }

                        // Handle finger lift, trigger onClick if conditions are met
                        if (change.changedToUp()) {
                            longPressJob.cancel()
                            if (!longPressed && !pastTouchSlop) {
                                onClick()
                            }
                            break
                        }
                    }

                    // Handle drag event
                    if (isDragging) {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.first()

                            if (change.pressed) {
                                val dragAmount = change.positionChange()
                                onDrag(change, dragAmount)
                                // Consume the event to prevent LazyColumn scrolling
                                change.consume()
                            } else {
                                dragInteractionStart?.also {
                                    coroutineScope.launch {
                                        interactionSource?.emit(DragInteraction.Stop(it))
                                    }
                                }
                                onDragStopped()
                                longPressed = false
                                break
                            }
                        }
                    } else {
                        longPressed = false
                    }
                }
            }
        }
    }
}
