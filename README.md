# Reorderable

Reorderable is a simple library that allows you to reorder items in [`LazyColumn`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyColumn(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>) and [`LazyRow`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyRow(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Horizontal,androidx.compose.ui.Alignment.Vertical,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>) as well as [`Column`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary#Column(androidx.compose.ui.Modifier,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,kotlin.Function1)>) and [`Row`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary#Row(androidx.compose.ui.Modifier,androidx.compose.foundation.layout.Arrangement.Horizontal,androidx.compose.ui.Alignment.Vertical,kotlin.Function1)>) in Jetpack Compose with drag and drop.

<img src="demo.webp" width="320" />

## Features

- Supports items of different sizes
- Some items can be made non-reorderable
- Scrolls when dragging to the edge of the screen (only for [`LazyColumn`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyColumn(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>) and [`LazyRow`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyRow(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Horizontal,androidx.compose.ui.Alignment.Vertical,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>)) The scroll speed is based on the distance from the edge of the screen
- Uses the new [`Modifier.animateItemPlacement`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/LazyItemScope#(androidx.compose.ui.Modifier).animateItemPlacement(androidx.compose.animation.core.FiniteAnimationSpec)>) API to animate item movement in [`LazyColumn`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyColumn(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>) and [`LazyRow`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyRow(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Horizontal,androidx.compose.ui.Alignment.Vertical,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>)
- Supports using a child of an item as the drag handle

## Usage

### Gradle

Add the following to your `build.gradle` file:

#### Kotlin DSL

```kotlin
dependencies {
    implementation("sh.calvin.reorderable:reorderable:1.0.1")
}
```

#### Groovy DSL

```groovy
dependencies {
    implementation 'sh.calvin.reorderable:reorderable:1.0.1'
}
```

### Code

See [demo app code](demoApp/src/main/java/sh/calvin/reorderable) for more examples.

#### [`LazyColumn`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyColumn(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>)

Find more examples in [`SimpleReorderableLazyColumnScreen.kt`](demoApp/src/main/java/sh/calvin/reorderable/SimpleReorderableLazyColumnScreen.kt) and [`ComplexReorderableLazyColumnScreen.kt`](demoApp/src/main/java/sh/calvin/reorderable/ComplexReorderableLazyColumnScreen.kt) in the demo app.

```kotlin
val lazyListState = rememberLazyListState()
val reorderableLazyColumnState = rememberReorderableLazyColumnState(lazyListState) { from, to ->
    // Update the list
}

LazyColumn(state = lazyListState) {
    items(list, key = { /* item key */ }) {
        ReorderableItem(reorderableLazyColumnState, key = /* item key */) { isDragging ->
            // Item content

            IconButton(modifier = Modifier.draggableHandle(), /* ... */)
        }
    }
}

```

Since `Modifier.draggableHandle` can only be used in `ReorderableItemScope`, you may need to pass `ReorderableItemScope` to a child composable. For example:

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
    IconButton(modifier = with(scope) { Modifier.draggableHandle() }, /* ... */)
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

#### [`Column`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary#Column(androidx.compose.ui.Modifier,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,kotlin.Function1)>)

Find more examples in [`ReorderableColumnScreen.kt`](demoApp/src/main/java/sh/calvin/reorderable/ReorderableColumnScreen.kt) in the demo app.

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

Since `Modifier.draggableHandle` can only be used in `ReorderableScope`, you may need to pass `ReorderableScope` to a child composable. For example:

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

#### LazyRow

See [`SimpleReorderableLazyRowScreen.kt`](demoApp/src/main/java/sh/calvin/reorderable/SimpleReorderableLazyRowScreen.kt) and [`ComplexReorderableLazyRowScreen.kt`](demoApp/src/main/java/sh/calvin/reorderable/ComplexReorderableLazyRowScreen.kt) in the demo app.

You can just replace `Column` with `Row` in the `LazyColumn` examples above.

#### Row

See [`ReorderableRowScreen.kt`](demoApp/src/main/java/sh/calvin/reorderable/ReorderableRowScreen.kt) in the demo app.

You can just replace `Column` with `Row` in the `Column` examples above.

## API

### [`LazyColumn`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyColumn(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>) / [`LazyRow`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyRow(androidx.compose.ui.Modifier,androidx.compose.foundation.lazy.LazyListState,androidx.compose.foundation.layout.PaddingValues,kotlin.Boolean,androidx.compose.foundation.layout.Arrangement.Horizontal,androidx.compose.ui.Alignment.Vertical,androidx.compose.foundation.gestures.FlingBehavior,kotlin.Boolean,kotlin.Function1)>)

- [`rememberReorderableLazyColumnState`](reorderable/src/main/java/sh/calvin/reorderable/ReorderableLazyList.kt)
- [`rememberReorderableLazyRowState`](reorderable/src/main/java/sh/calvin/reorderable/ReorderableLazyList.kt)
- [`ReorderableItem`](reorderable/src/main/java/sh/calvin/reorderable/ReorderableLazyList.kt)
- [`Modifier.draggableHandle`](reorderable/src/main/java/sh/calvin/reorderable/ReorderableLazyList.kt)

### [`Column`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary#Column(androidx.compose.ui.Modifier,androidx.compose.foundation.layout.Arrangement.Vertical,androidx.compose.ui.Alignment.Horizontal,kotlin.Function1)>) / [`Row`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary#Row(androidx.compose.ui.Modifier,androidx.compose.foundation.layout.Arrangement.Horizontal,androidx.compose.ui.Alignment.Vertical,kotlin.Function1)>)

- [`ReorderableColumn`](reorderable/src/main/java/sh/calvin/reorderable/ReorderableList.kt)
- [`ReorderableRow`](reorderable/src/main/java/sh/calvin/reorderable/ReorderableList.kt)
- [`Modifier.draggableHandle`](reorderable/src/main/java/sh/calvin/reorderable/ReorderableList.kt)

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
