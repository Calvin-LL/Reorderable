package sh.calvin.reorderable

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

internal fun Modifier.longPressDraggable(
    enabled: Boolean = true,
    onDragStarted: (Offset) -> Unit = { },
    onDragStopped: () -> Unit = { },
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit
) = composed {
    var dragStarted = remember { false }

    pointerInput(enabled) {
        if (enabled) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    dragStarted = true

                    onDragStarted(it)
                },
                onDragEnd = {
                    if (dragStarted) {
                        onDragStopped()
                    }

                    dragStarted = false

                },
                onDragCancel = {
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