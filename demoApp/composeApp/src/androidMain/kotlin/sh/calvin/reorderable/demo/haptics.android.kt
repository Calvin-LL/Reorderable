package sh.calvin.reorderable.demo

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Composable
actual fun rememberReorderHapticFeedback(): ReorderHapticFeedback {
    val view = LocalView.current

    val reorderHapticFeedback = remember {
        object : ReorderHapticFeedback() {
            override fun performHapticFeedback(type: ReorderHapticFeedbackType) {
                when (type) {
                    ReorderHapticFeedbackType.START ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            view.performHapticFeedback(android.view.HapticFeedbackConstants.DRAG_START)
                        }

                    ReorderHapticFeedbackType.MOVE ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            view.performHapticFeedback(android.view.HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)
                        }

                    ReorderHapticFeedbackType.END ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            view.performHapticFeedback(android.view.HapticFeedbackConstants.GESTURE_END)
                        }
                }
            }
        }
    }

    return reorderHapticFeedback
}
