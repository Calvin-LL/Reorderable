package sh.calvin.reorderable.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import sh.calvin.reorderable.demo.ui.theme.ReorderableTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun App() {
    PreComposeApp {
        ReorderableTheme {
            val navController = rememberNavigator()
            val showBackButton = navController.canGoBack.collectAsState(false)
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Reorderable Demo") },
                        navigationIcon = { if (showBackButton.value) BackArrow(navController::popBackStack) },
                    )
                },
                content = {
                    Box(modifier = Modifier.padding(it)) {
                        NavHost(
                            navController,
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            initialRoute = "main",
                        ) {
                            scene("main") { MainScreen(navController) }
                            scene("SimpleReorderableLazyColumn") { SimpleReorderableLazyColumnScreen() }
                            scene("ComplexReorderableLazyColumn") { ComplexReorderableLazyColumnScreen() }
                            scene("SimpleLongPressHandleReorderableLazyColumn") { SimpleLongPressHandleReorderableLazyColumnScreen() }
                            scene("ReorderableColumn") { ReorderableColumnScreen() }
                            scene("LongPressHandleReorderableColumn") { LongPressHandleReorderableColumnScreen() }
                            scene("SimpleReorderableLazyRow") { SimpleReorderableLazyRowScreen() }
                            scene("ComplexReorderableLazyRow") { ComplexReorderableLazyRowScreen() }
                            scene("ReorderableRow") { ReorderableRowScreen() }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun BackArrow(onBackAction: () -> Unit) {
    IconButton(onClick = onBackAction) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
    }
}

@Composable
fun MainScreen(navController: Navigator) {
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
