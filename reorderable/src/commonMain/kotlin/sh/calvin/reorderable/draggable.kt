package sh.calvin.reorderable

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch

internal fun Modifier.draggable(
    key1: Any?,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    dragGestureDetector: DragGestureDetector = DragGestureDetector.Press,
    onDragStarted: (Offset) -> Unit = { },
    onDragStopped: () -> Unit = { },
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit,
) = composed {
    val coroutineScope = rememberCoroutineScope()
    var dragInteractionStart by remember { mutableStateOf<DragInteraction.Start?>(null) }
    var dragStarted by remember { mutableStateOf(false) }
    val onDragStartedState by rememberUpdatedState(onDragStarted)
    val onDragStoppedState by rememberUpdatedState(onDragStopped)
    val onDragState by rememberUpdatedState(onDrag)

    DisposableEffect(key1) {
        onDispose {
            if (dragStarted) {
                dragInteractionStart?.also {
                    coroutineScope.launch {
                        interactionSource?.emit(DragInteraction.Cancel(it))
                    }
                }

                if (dragStarted) {
                    onDragStoppedState()
                }

                dragStarted = false
            }
        }
    }

    pointerInput(key1, enabled) {
        if (!enabled) {
            return@pointerInput
        }

        with(dragGestureDetector) {
            detect(
                onDragStart = {
                    dragStarted = true
                    dragInteractionStart = DragInteraction.Start().also {
                        coroutineScope.launch {
                            interactionSource?.emit(it)
                        }
                    }

                    onDragStartedState(it)
                },
                onDragEnd = {
                    dragInteractionStart?.also {
                        coroutineScope.launch {
                            interactionSource?.emit(DragInteraction.Stop(it))
                        }
                    }

                    if (dragStarted) {
                        onDragStoppedState()
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
                        onDragStoppedState()
                    }

                    dragStarted = false
                },
                onDrag = { change, dragAmount -> onDragState(change, dragAmount) },
            )
        }
    }
}
