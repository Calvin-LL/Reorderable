# Reorderable

Reorderable is a simple library that allows you to reorder items in [`LazyColumn`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyColumn(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>) and [`LazyRow`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyRow(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Horizontal,androidx.compose.ui.Alignment.Vertical,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>) as well as [`Column`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary#Column(androidx.compose.ui.Modifier,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,kotlin.Function1)>) and [`Row`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary#Row(androidx.compose.ui.Modifier,androidx.compose.foundation.layout.Arrangement.Horizontal,androidx.compose.ui.Alignment.Vertical,kotlin.Function1)>) in Jetpack Compose with drag and drop.

The latest demo app APK can be found in the [releases](https://github.com/Calvin-LL/Reorderable/releases) section under the "Assets" section of the latest release.

<img src="demo.webp" width="320" />

## Features

- Supports Compose Multiplatform (Android, iOS, Desktop/JVM)
- Supports items of different sizes
- Some items can be made non-reorderable
- Supports dragging immediately or long press to start dragging
- Scrolls when dragging to the edge of the screen (only for [`LazyColumn`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyColumn(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>) and [`LazyRow`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyRow(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Horizontal,androidx.compose.ui.Alignment.Vertical,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>)) The scroll speed is based on the distance from the edge of the screen
- Uses the new [`Modifier.animateItemPlacement`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/LazyItemScope#(androidx.compose.ui.Modifier).animateItemPlacement(androidx.compose.animation.core.FiniteAnimationSpec)>) API to animate item movement in [`LazyColumn`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyColumn(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>) and [`LazyRow`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyRow(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Horizontal,androidx.compose.ui.Alignment.Vertical,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>)
- Supports using a child of an item as the drag handle
- Supports Wasm in [`1.4.0-beta02`](https://github.com/Calvin-LL/Reorderable/tree/beta/v1.4.0-beta02)

## Usage

### Version Catalog

If you're using Version Catalog, add the following to your `libs.versions.toml` file:

```toml
[versions]
#...
reorderable = "1.3.3"

[libraries]
#...
reorderable = { module = "sh.calvin.reorderable:reorderable", version.ref = "reorderable" }
```

or

```toml
[libraries]
#...
reorderable = { module = "sh.calvin.reorderable:reorderable", version = "1.3.3" }
```

### Gradle

If you're using Gradle instead, add the following to your `build.gradle` file:

#### Kotlin DSL

```kotlin
dependencies {
    implementation("sh.calvin.reorderable:reorderable:1.3.3")
}
```

#### Groovy DSL

```groovy
dependencies {
    implementation 'sh.calvin.reorderable:reorderable:1.3.3'
}
```

### Examples

See [demo app code](demoApp/composeApp/src/commonMain/kotlin/sh/calvin/reorderable/demo) for more examples.

#### Table of Contents

- [`LazyColumn`](#lazycolumn)
- [`Column`](#column)
- [`LazyRow`](#lazyrow)
- [`Row`](#row)

#### [`LazyColumn`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyColumn(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>)

Find more examples in [`SimpleReorderableLazyColumnScreen.kt`](demoApp/composeApp/src/commonMain/kotlin/sh/calvin/reorderable/demo/SimpleReorderableLazyColumnScreen.kt), [`SimpleLongPressHandleReorderableLazyColumnScreen.kt`](demoApp/composeApp/src/commonMain/kotlin/sh/calvin/reorderable/demo/SimpleLongPressHandleReorderableLazyColumnScreen.kt) and [`ComplexReorderableLazyColumnScreen.kt`](demoApp/composeApp/src/commonMain/kotlin/sh/calvin/reorderable/demo/ComplexReorderableLazyColumnScreen.kt) in the demo app.

To use this library with [`LazyColumn`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyColumn(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>), follow this basic structure:

```kotlin
val lazyListState = rememberLazyListState()
val reorderableLazyColumnState = rememberReorderableLazyColumnState(lazyListState) { from, to ->
    // Update the list
}

LazyColumn(state = lazyListState) {
    items(list, key = { /* item key */ }) {
        ReorderableItem(reorderableLazyColumnState, key = /* item key */) { isDragging ->
            // Item content

            IconButton(
                modifier = Modifier.draggableHandle(),
                /* ... */
            )
        }
    }
}

```

Since `Modifier.draggableHandle` and `Modifier.longPressDraggableHandle` can only be used in `ReorderableItemScope`, you may need to pass `ReorderableItemScope` to a child composable. For example:

```kotlin
@Composable
fun List() {
    // ...

    LazyColumn(state = lazyListState) {
        items(list, key = { /* item key */ }) {
            ReorderableItem(reorderableLazyColumnState, key = /* item key */) { isDragging ->
                // Item content

                DragHandle(this)
            }
        }
    }
}

@Composable
fun DragHandle(scope: ReorderableItemScope) {
    IconButton(
        modifier = with(scope) {
            Modifier.draggableHandle()
        },
        /* ... */
    )
}
```

Here's a more complete example with (with haptic feedback):

```kotlin
val view = LocalView.current

var list by remember { mutableStateOf(List(100) { "Item $it" }) }
val lazyListState = rememberLazyListState()
val reorderableLazyColumnState = rememberReorderableLazyColumnState(lazyListState) { from, to ->
    list = list.toMutableList().apply {
        add(to.index, removeAt(from.index))
    }

    view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)
}

LazyColumn(
    modifier = Modifier.fillMaxSize(),
    state = lazyListState,
    contentPadding = PaddingValues(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(list, key = { it }) {
        ReorderableItem(reorderableLazyColumnState, key = it) { isDragging ->
            val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

            Surface(shadowElevation = elevation) {
                Row {
                    Text(it, Modifier.padding(horizontal = 8.dp))
                    IconButton(
                        modifier = Modifier.draggableHandle(
                            onDragStarted = {
                                view.performHapticFeedback(HapticFeedbackConstants.DRAG_START)
                            },
                            onDragStopped = {
                                view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                            },
                        ),
                        onClick = {},
                    ) {
                        Icon(Icons.Rounded.DragHandle, contentDescription = "Reorder")
                    }
                }
            }
        }
    }
}
```

If you want to use the [material3's Clickable Card](<https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#Card(kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Boolean,androidx.compose.ui.graphics.Shape,androidx.compose.material3.CardColors,androidx.compose.material3.CardElevation,androidx.compose.foundation.BorderStroke,androidx.compose.foundation.interaction.MutableInteractionSource,kotlin.Function1)>), you can create a [`MutableInteractionSource`](https://developer.android.com/reference/kotlin/androidx/compose/foundation/interaction/MutableInteractionSource) and pass it to both the [`Card`](<https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#Card(kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Boolean,androidx.compose.ui.graphics.Shape,androidx.compose.material3.CardColors,androidx.compose.material3.CardElevation,androidx.compose.foundation.BorderStroke,androidx.compose.foundation.interaction.MutableInteractionSource,kotlin.Function1)>) and the `Modifier.draggableHandle` (or `Modifier.longPressDraggableHandle`), `Modifier.draggableHandle` will emit drag events to the [`MutableInteractionSource`](https://developer.android.com/reference/kotlin/androidx/compose/foundation/interaction/MutableInteractionSource) so that the [`Card`](<https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#Card(kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Boolean,androidx.compose.ui.graphics.Shape,androidx.compose.material3.CardColors,androidx.compose.material3.CardElevation,androidx.compose.foundation.BorderStroke,androidx.compose.foundation.interaction.MutableInteractionSource,kotlin.Function1)>) can respond to the drag events:

```kotlin
val view = LocalView.current

var list by remember { mutableStateOf(List(100) { "Item $it" }) }
val lazyListState = rememberLazyListState()
val reorderableLazyColumnState = rememberReorderableLazyColumnState(lazyListState) { from, to ->
    list = list.toMutableList().apply {
        add(to.index, removeAt(from.index))
    }

    view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)
}

LazyColumn(
    modifier = Modifier.fillMaxSize(),
    state = lazyListState,
    contentPadding = PaddingValues(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(list, key = { it }) { item ->
        ReorderableItem(reorderableLazyColumnState, key = item) {
            val interactionSource = remember { MutableInteractionSource() }

            Card(
                onClick = {},
                interactionSource = interactionSource,
            ) {
                Row {
                    Text(item, Modifier.padding(horizontal = 8.dp))
                    IconButton(
                        modifier = Modifier.draggableHandle(
                            onDragStarted = {
                                view.performHapticFeedback(HapticFeedbackConstants.DRAG_START)
                            },
                            onDragStopped = {
                                view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                            },
                            interactionSource = interactionSource,
                        ),
                        onClick = {},
                    ) {
                        Icon(Icons.Rounded.DragHandle, contentDescription = "Reorder")
                    }
                }
            }
        }
    }
}
```

#### [`Column`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary#Column(androidx.compose.ui.Modifier,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,kotlin.Function1)>)

Find more examples in [`ReorderableColumnScreen.kt`](demoApp/composeApp/src/commonMain/kotlin/sh/calvin/reorderable/demo/ReorderableColumnScreen.kt) and [`LongPressHandleReorderableColumnScreen.kt`](demoApp/composeApp/src/commonMain/kotlin/sh/calvin/reorderable/demo/LongPressHandleReorderableColumnScreen.kt) in the demo app.

To use this library with [`Column`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary#Column(androidx.compose.ui.Modifier,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,kotlin.Function1)>), follow this basic structure:

```kotlin
ReorderableColumn(
    list = list,
    onSettle = { fromIndex, toIndex ->
        // Update the list
    },
) { index, item, isDragging ->
    key(item.id) {
        // Item content

        IconButton(modifier = Modifier.draggableHandle(), /* ... */)
    }
}
```

Since `Modifier.draggableHandle` and `Modifier.longPressDraggableHandle` can only be used in `ReorderableScope`, you may need to pass `ReorderableScope` to a child composable. For example:

```kotlin
@Composable
fun List() {
    // ...

    ReorderableColumn(
        list = list,
        onSettle = { fromIndex, toIndex ->
            // Update the list
        },
    ) { index, item, isDragging ->
        key(item.id) {
            // Item content

            DragHandle(this)
        }
    }
}

@Composable
fun DragHandle(scope: ReorderableScope) {
    IconButton(modifier = with(scope) { Modifier.draggableHandle() }, /* ... */)
}
```

Here's a more complete example (with haptic feedback):

```kotlin
val view = LocalView.current

var list by remember { mutableStateOf(List(4) { "Item $it" }) }

ReorderableColumn(
    modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
    list = list,
    onSettle = { fromIndex, toIndex ->
        list = list.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }
    },
    onMove = {
        view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)
    },
    verticalArrangement = Arrangement.spacedBy(8.dp),
) { _, item, isDragging ->
    key(item) {
        val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

        Surface(shadowElevation = elevation) {
            Row {
                Text(item, Modifier.padding(horizontal = 8.dp))
                IconButton(
                    modifier = Modifier.draggableHandle(
                        onDragStarted = {
                            view.performHapticFeedback(HapticFeedbackConstants.DRAG_START)
                        },
                        onDragStopped = {
                            view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                        },
                    ),
                    onClick = {},
                ) {
                    Icon(Icons.Rounded.DragHandle, contentDescription = "Reorder")
                }
            }
        }
    }
}
```

If you want to use the [material3's Clickable Card](<https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#Card(kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Boolean,androidx.compose.ui.graphics.Shape,androidx.compose.material3.CardColors,androidx.compose.material3.CardElevation,androidx.compose.foundation.BorderStroke,androidx.compose.foundation.interaction.MutableInteractionSource,kotlin.Function1)>), you can create a [`MutableInteractionSource`](https://developer.android.com/reference/kotlin/androidx/compose/foundation/interaction/MutableInteractionSource) and pass it to both the [`Card`](<https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#Card(kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Boolean,androidx.compose.ui.graphics.Shape,androidx.compose.material3.CardColors,androidx.compose.material3.CardElevation,androidx.compose.foundation.BorderStroke,androidx.compose.foundation.interaction.MutableInteractionSource,kotlin.Function1)>) and the `Modifier.draggableHandle` (or `Modifier.longPressDraggableHandle`), `Modifier.draggableHandle` will emit drag events to the [`MutableInteractionSource`](https://developer.android.com/reference/kotlin/androidx/compose/foundation/interaction/MutableInteractionSource) so that the [`Card`](<https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#Card(kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Boolean,androidx.compose.ui.graphics.Shape,androidx.compose.material3.CardColors,androidx.compose.material3.CardElevation,androidx.compose.foundation.BorderStroke,androidx.compose.foundation.interaction.MutableInteractionSource,kotlin.Function1)>) can respond to the drag events:

```kotlin
val view = LocalView.current

var list by remember { mutableStateOf(List(4) { "Item $it" }) }

ReorderableColumn(
    modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
    list = list,
    onSettle = { fromIndex, toIndex ->
        list = list.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }
    },
    onMove = {
        view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)
    },
    verticalArrangement = Arrangement.spacedBy(8.dp),
) { _, item, _ ->
    key(item) {
        val interactionSource = remember { MutableInteractionSource() }

        Card(
            onClick = {},
            interactionSource = interactionSource,
        ) {
            Row {
                Text(item, Modifier.padding(horizontal = 8.dp))
                IconButton(
                    modifier = Modifier.draggableHandle(
                        onDragStarted = {
                            view.performHapticFeedback(HapticFeedbackConstants.DRAG_START)
                        },
                        onDragStopped = {
                            view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                        },
                        interactionSource = interactionSource,
                    ),
                    onClick = {},
                ) {
                    Icon(Icons.Rounded.DragHandle, contentDescription = "Reorder")
                }
            }
        }
    }
}
```

#### LazyRow

See [`SimpleReorderableLazyRowScreen.kt`](demoApp/composeApp/src/commonMain/kotlin/sh/calvin/reorderable/demo/SimpleReorderableLazyRowScreen.kt) and [`ComplexReorderableLazyRowScreen.kt`](demoApp/composeApp/src/commonMain/kotlin/sh/calvin/reorderable/demo/ComplexReorderableLazyRowScreen.kt) in the demo app.

You can just replace `Column` with `Row` in the `LazyColumn` examples above.

#### Row

See [`ReorderableRowScreen.kt`](demoApp/composeApp/src/commonMain/kotlin/sh/calvin/reorderable/demo/ReorderableRowScreen.kt) in the demo app.

You can just replace `Column` with `Row` in the `Column` examples above.

## API

### [`LazyColumn`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyColumn(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>) / [`LazyRow`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyRow(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Horizontal,androidx.compose.ui.Alignment.Vertical,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>)

- [`rememberReorderableLazyColumnState`](reorderable/src/commonMain/kotlin/sh/calvin/reorderable/demo/ReorderableLazyList.kt)
- [`rememberReorderableLazyRowState`](reorderable/src/commonMain/kotlin/sh/calvin/reorderable/demo/ReorderableLazyList.kt)
- [`ReorderableItem`](reorderable/src/commonMain/kotlin/sh/calvin/reorderable/demo/ReorderableLazyList.kt)
- [`Modifier.draggableHandle`](reorderable/src/commonMain/kotlin/sh/calvin/reorderable/demo/ReorderableLazyList.kt)
- [`Modifier.longPressDraggableHandle`](reorderable/src/commonMain/kotlin/sh/calvin/reorderable/demo/ReorderableLazyList.kt)

### [`Column`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary#Column(androidx.compose.ui.Modifier,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,kotlin.Function1)>) / [`Row`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary#Row(androidx.compose.ui.Modifier,androidx.compose.foundation.layout.Arrangement.Horizontal,androidx.compose.ui.Alignment.Vertical,kotlin.Function1)>)

- [`ReorderableColumn`](reorderable/src/commonMain/kotlin/sh/calvin/reorderable/demo/ReorderableList.kt)
- [`ReorderableRow`](reorderable/src/commonMain/kotlin/sh/calvin/reorderable/demo/ReorderableList.kt)
- [`Modifier.draggableHandle`](reorderable/src/commonMain/kotlin/sh/calvin/reorderable/demo/ReorderableList.kt)
- [`Modifier.longPressDraggableHandle`](reorderable/src/commonMain/kotlin/sh/calvin/reorderable/demo/ReorderableList.kt)

## Contributing

Open this project with Android Studio Preview.

You'll want to install the [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile) in Android Studio before you open this project.

## License

```
Copyright 2023 Calvin Liang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
