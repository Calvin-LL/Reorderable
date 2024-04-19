import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import sh.calvin.reorderable.demo.ui.App

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
