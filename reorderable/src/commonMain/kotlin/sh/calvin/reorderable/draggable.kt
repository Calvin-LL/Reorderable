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
    onClick: () -> Unit = { },
    onLongPress: () -> Unit = { },
    onDragStarted: (Offset) -> Unit = { },
    onDragStopped: () -> Unit = { },
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit,
) = composed {
    val coroutineScope = rememberCoroutineScope()
    var dragInteractionStart by remember { mutableStateOf<DragInteraction.Start?>(null) }
    var dragStarted by remember { mutableStateOf(false) }
    var longPressed by remember { mutableStateOf(false) }

    pointerInput(key1, enabled) {
        if (enabled) {
            awaitPointerEventScope {
                while (true) {
                    val down = awaitPointerEvent().changes.firstOrNull()?.takeIf { it.pressed } ?: continue

                    val longPressTimeout = viewConfiguration.longPressTimeoutMillis
                    val longPressJob = coroutineScope.launch {
                        delay(longPressTimeout)
                        if (down.pressed) {
                            longPressed = true
                            onLongPress()
                        }
                    }

                    var isDragging = false
                    while (down.pressed) {
                        val event = awaitPointerEvent()
                        val change = event.changes.first()

                        if (change.positionChange() != Offset.Zero && longPressed) {
                            isDragging = true
                            dragStarted = true
                            longPressJob.cancel()
                            dragInteractionStart = DragInteraction.Start().also {
                                coroutineScope.launch {
                                    interactionSource?.emit(it)
                                }
                            }
                            onDragStarted(change.position)
                            break
                        }

                        if (change.changedToUp()) {
                            longPressJob.cancel()
                            if (!longPressed) {
                                onClick()
                            }
                            break
                        }
                    }

                    if (isDragging) {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.first()

                            if (change.pressed) {
                                val dragAmount = change.positionChange()
                                onDrag(change, dragAmount)
                                change.consume()
                            } else {
                                dragInteractionStart?.also {
                                    coroutineScope.launch {
                                        interactionSource?.emit(DragInteraction.Stop(it))
                                    }
                                }
                                onDragStopped()
                                dragStarted = false
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
