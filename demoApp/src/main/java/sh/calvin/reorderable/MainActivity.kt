package sh.calvin.reorderable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import sh.calvin.reorderable.ui.theme.ReorderableTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReorderableTheme {
                val navController = rememberNavController()

                NavHost(
                    navController,
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    startDestination = "main",
                ) {
                    composable("main") { MainScreen(navController) }
                    composable("SimpleReorderableLazyColumn") { SimpleReorderableLazyColumnScreen() }
                    composable("ComplexReorderableLazyColumn") { ComplexReorderableLazyColumnScreen() }
                    composable("SimpleLongPressHandleReorderableLazyColumn") { SimpleLongPressHandleReorderableLazyColumnScreen() }
                    composable("ReorderableColumn") { ReorderableColumnScreen() }
                    composable("LongPressHandleReorderableColumn") { LongPressHandleReorderableColumnScreen() }
                    composable("SimpleReorderableLazyRow") { SimpleReorderableLazyRowScreen() }
                    composable("ComplexReorderableLazyRow") { ComplexReorderableLazyRowScreen() }
                    composable("ReorderableRow") { ReorderableRowScreen() }
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavController) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("LazyColumn", Modifier.padding(8.dp), color = MaterialTheme.colorScheme.onBackground)
        Button(onClick = { navController.navigate("SimpleReorderableLazyColumn") }) {
            Text("Simple ReorderableLazyColumn")
        }
        Button(onClick = { navController.navigate("ComplexReorderableLazyColumn") }) {
            Text("Complex ReorderableLazyColumn")
        }
        Button(onClick = { navController.navigate("SimpleLongPressHandleReorderableLazyColumn") }) {
            Text(
                "Simple ReorderableLazyColumn with\n.longPressDraggableHandle",
                textAlign = TextAlign.Center
            )
        }
        Text("Column", Modifier.padding(8.dp), color = MaterialTheme.colorScheme.onBackground)
        Button(onClick = { navController.navigate("ReorderableColumn") }) {
            Text("ReorderableColumn")
        }
        Button(onClick = { navController.navigate("LongPressHandleReorderableColumn") }) {
            Text("ReorderableColumn with\n.longPressDraggableHandle", textAlign = TextAlign.Center)
        }

        Text("LazyRow", Modifier.padding(8.dp), color = MaterialTheme.colorScheme.onBackground)
        Button(onClick = { navController.navigate("SimpleReorderableLazyRow") }) {
            Text("Simple ReorderableLazyRow")
        }
        Button(onClick = { navController.navigate("ComplexReorderableLazyRow") }) {
            Text("Complex ReorderableLazyRow")
        }
        Text("Row", Modifier.padding(8.dp), color = MaterialTheme.colorScheme.onBackground)
        Button(onClick = { navController.navigate("ReorderableRow") }) {
            Text("ReorderableRow")
        }
    }
}