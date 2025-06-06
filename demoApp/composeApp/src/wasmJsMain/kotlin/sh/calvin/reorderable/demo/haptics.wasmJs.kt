package sh.calvin.reorderable.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.window

@Composable
actual fun rememberReorderHapticFeedback(): ReorderHapticFeedback {
    val reorderHapticFeedback = remember {
        object : ReorderHapticFeedback() {
            override fun performHapticFeedback(type: ReorderHapticFeedbackType) {
                try {
                    when (type) {
                        ReorderHapticFeedbackType.START ->
                            window.navigator.vibrate(5)

                        ReorderHapticFeedbackType.MOVE ->
                            window.navigator.vibrate(1)

                        ReorderHapticFeedbackType.END ->
                            window.navigator.vibrate(3)
                    }
                } catch (e: Throwable) {
                    println("Haptic feedback not supported: ${e.message}")
                }
            }
        }
    }

    return reorderHapticFeedback
}
