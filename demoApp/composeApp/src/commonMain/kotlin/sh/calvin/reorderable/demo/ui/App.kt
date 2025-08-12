package sh.calvin.reorderable.demo.ui

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
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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

            CompositionLocalProvider(
                LocalMinimumInteractiveComponentSize provides Dp.Unspecified
            ) {
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
                                scene("TwoReorderableLazyColumnScreen") { TwoReorderableLazyColumnScreen() }
                                scene("SimpleLongPressHandleReorderableLazyColumn") { SimpleLongPressHandleReorderableLazyColumnScreen() }
                                scene("SimpleReorderableLazyVerticalGrid") { SimpleReorderableLazyVerticalGridScreen() }
                                scene("SimpleReorderableLazyVerticalStaggeredGrid") { SimpleReorderableLazyVerticalStaggeredGridScreen() }
                                scene("ReorderableColumn") { ReorderableColumnScreen() }
                                scene("LongPressHandleReorderableColumn") { LongPressHandleReorderableColumnScreen() }
                                scene("ReorderableColumnWithWeight") { ReorderableColumnWithWeightScreen() }
                                scene("SimpleReorderableLazyRow") { SimpleReorderableLazyRowScreen() }
                                scene("ComplexReorderableLazyRow") { ComplexReorderableLazyRowScreen() }
                                scene("SimpleReorderableLazyHorizontalGrid") { SimpleReorderableLazyHorizontalGridScreen() }
                                scene("SimpleReorderableLazyHorizontalStaggeredGrid") { SimpleReorderableLazyHorizontalStaggeredGridScreen() }
                                scene("ReorderableRow") { ReorderableRowScreen() }
                            }
                        }
                    }
                )
            }
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
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            Text("LazyColumn")

            Button(onClick = { navController.navigate("SimpleReorderableLazyColumn") }) {
                Text("Simple Reorderable LazyColumn")
            }
            Button(
                onClick = { navController.navigate("ComplexReorderableLazyColumn") }) {
                Text("Complex Reorderable LazyColumn")
            }
            Button(
                onClick = { navController.navigate("TwoReorderableLazyColumnScreen") }) {
                Text("2 Lists Reorderable LazyColumn")
            }
            Button(
                onClick = { navController.navigate("SimpleLongPressHandleReorderableLazyColumn") }) {
                Text(
                    "Simple Reorderable LazyColumn with\n.longPressDraggableHandle",
                    textAlign = TextAlign.Center
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            Text("LazyVerticalGrid")

            Button(onClick = { navController.navigate("SimpleReorderableLazyVerticalGrid") }) {
                Text("Simple Reorderable LazyVerticalGrid")
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            Text("LazyVerticalStaggeredGrid")

            Button(onClick = { navController.navigate("SimpleReorderableLazyVerticalStaggeredGrid") }) {
                Text("Simple Reorderable LazyVerticalStaggeredGrid")
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Column")

            Button(
                onClick = { navController.navigate("ReorderableColumn") }) {
                Text("ReorderableColumn")
            }
            Button(
                onClick = { navController.navigate("LongPressHandleReorderableColumn") }) {
                Text(
                    "ReorderableColumn with\n.longPressDraggableHandle",
                    textAlign = TextAlign.Center
                )
            }
            Button(
                onClick = { navController.navigate("ReorderableColumnWithWeight") }) {
                Text("ReorderableColumn\nwith Modifier.weight")
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("LazyRow")

            Button(
                onClick = { navController.navigate("SimpleReorderableLazyRow") }) {
                Text("Simple Reorderable LazyRow")
            }
            Button(
                onClick = { navController.navigate("ComplexReorderableLazyRow") }) {
                Text("Complex Reorderable LazyRow")
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            Text("LazyHorizontalGrid")

            Button(onClick = { navController.navigate("SimpleReorderableLazyHorizontalGrid") }) {
                Text("Simple Reorderable LazyHorizontalGrid")
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            Text("LazyHorizontalStaggeredGrid")

            Button(onClick = { navController.navigate("SimpleReorderableLazyHorizontalStaggeredGrid") }) {
                Text("Simple Reorderable LazyHorizontalStaggeredGrid")
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Row")

            Button(
                onClick = { navController.navigate("ReorderableRow") }
            ) {
                Text("ReorderableRow")
            }
        }
    }
}
