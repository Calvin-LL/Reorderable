package sh.calvin.reorderable.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

@Composable
actual fun rememberReorderHapticFeedback(): ReorderHapticFeedback {
    val reorderHapticFeedback = remember {
        object : ReorderHapticFeedback() {
            override fun performHapticFeedback(type: ReorderHapticFeedbackType) {
                when (type) {
                    ReorderHapticFeedbackType.START ->
                        UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
                            .impactOccurred()

                    ReorderHapticFeedbackType.MOVE ->
                        UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleSoft)
                            .impactOccurred()

                    ReorderHapticFeedbackType.END ->
                        UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
                            .impactOccurred()
                }
            }
        }
    }

    return reorderHapticFeedback
}
