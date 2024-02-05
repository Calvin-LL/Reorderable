import androidx.compose.ui.window.ComposeUIViewController
import sh.calvin.reorderable.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
