package sh.calvin.reorderable

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope

fun interface DragGestureDetector {
    suspend fun PointerInputScope.detect(
        onDragStart: (Offset) -> Unit,
        onDragEnd: () -> Unit,
        onDragCancel: () -> Unit,
        onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit
    )

    object Press : DragGestureDetector {
        override suspend fun PointerInputScope.detect(
            onDragStart: (Offset) -> Unit,
            onDragEnd: () -> Unit,
            onDragCancel: () -> Unit,
            onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit
        ) {
            detectDragGestures(onDragStart, onDragEnd, onDragCancel, onDrag)
        }
    }

    object LongPress : DragGestureDetector {
        override suspend fun PointerInputScope.detect(
            onDragStart: (Offset) -> Unit,
            onDragEnd: () -> Unit,
            onDragCancel: () -> Unit,
            onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit
        ) {
            detectDragGesturesAfterLongPress(onDragStart, onDragEnd, onDragCancel, onDrag)
        }
    }
}
