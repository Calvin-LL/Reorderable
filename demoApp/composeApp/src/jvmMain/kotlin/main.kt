import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import moe.tlaster.precompose.ProvidePreComposeLocals
import sh.calvin.reorderable.demo.ui.App
import java.awt.Dimension

fun main() = application {
    Window(
        title = "Reorderable",
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = ::exitApplication,
    ) {
        window.minimumSize = Dimension(350, 600)
        ProvidePreComposeLocals {
            App()
        }
    }
}
