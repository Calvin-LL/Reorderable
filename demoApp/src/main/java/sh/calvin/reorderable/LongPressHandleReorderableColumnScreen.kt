package sh.calvin.reorderable

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp

@Composable
fun LongPressHandleReorderableColumnScreen() {
    val view = LocalView.current

    var list by remember { mutableStateOf(items.take(5)) }

    ReorderableColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        list = list,
        onSettle = { fromIndex, toIndex ->
            list = list.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            }
        },
        onMove = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)
            }
        },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) { _, item, isDragging ->
        key(item.id) {
            val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

            Card(
                modifier = Modifier.height(item.size.dp),
                shadowElevation = elevation,
            ) {
                Text(item.text, Modifier.padding(horizontal = 8.dp))
                IconButton(
                    modifier = Modifier.longPressDraggableHandle(
                        onDragStarted = {
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        },
                        onDragStopped = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                            }
                        },
                    ),
                    onClick = {},
                ) {
                    Icon(Icons.Rounded.DragHandle, contentDescription = "Reorder")
                }
            }
        }
    }
}