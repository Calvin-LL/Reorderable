# Reorderable

Reorderable is a simple library that allows you to reorder items in `LazyColumn` and `LazyRow` as well as `Column` and `Row` in Jetpack Compose.

https://github.com/Calvin-LL/Reorderable/assets/8357970/5970770d-7b65-4e29-ab0a-930ff7f80e90

## Features

- Supports items of different sizes
- Some items can be made non-reorderable
- Scrolls when dragging to the edge of the screen (only for `LazyColumn` and `LazyRow`)
- Uses the new [`Modifier.animateItemPlacement`](<https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/LazyItemScope#(androidx.compose.ui.Modifier).animateItemPlacement(androidx.compose.animation.core.FiniteAnimationSpec)>) API to animate item movement in `LazyColumn` and `LazyRow`
- Supports using a child of an item as the drag handle

## Usage

### Gradle

Add the following to your `build.gradle` file:

#### Kotlin DSL

```kotlin
dependencies {
    implementation("sh.calvin.reorderable:reorderable:1.0.0")
}
```

#### Groovy DSL

```groovy
dependencies {
    implementation 'sh.calvin.reorderable:reorderable:1.0.0'
}
```

### Code

See [demo app code](demoApp/src/main/java/sh/calvin/reorderable) for more examples.

#### LazyColumn

```kotlin
val view = LocalView.current

var list by remember { mutableStateOf(items) }
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
    items(list, key = { it.id }) {
        ReorderableItem(reorderableLazyColumnState, it.id) { isDragging ->
            val elevation by animateDpAsState(if (isDragging) 4.dp else 1.dp)

            Card(
                modifier = Modifier.height(it.size.dp),
                shadowElevation = elevation,
            ) {
                Text(it.text, Modifier.padding(horizontal = 8.dp))
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
                    Icon(Icons.Rounded.Reorder, contentDescription = "Reorder")
                }
            }
        }
    }
}
```

#### Column

```kotlin
val view = LocalView.current

var list by remember { mutableStateOf(items) }

ReorderableColumn(
    modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
    list = list,
    onEdit = { fromIndex, toIndex ->
        list = list.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }
    },
    onMove = {
        view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)
    },
    verticalArrangement = Arrangement.spacedBy(8.dp),
) { _, item, isDragging ->
    key(item.id) {
        val elevation by animateDpAsState(if (isDragging) 4.dp else 1.dp)

        Card(
            modifier = Modifier.height(item.size.dp),
            shadowElevation = elevation,
        ) {
            Text(item.text, Modifier.padding(horizontal = 8.dp))
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
                Icon(Icons.Rounded.Reorder, contentDescription = "Reorder")
            }
        }
    }
}
```

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
