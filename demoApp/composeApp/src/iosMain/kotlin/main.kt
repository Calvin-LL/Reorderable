import androidx.compose.ui.window.ComposeUIViewController
import sh.calvin.reorderable.demo.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
