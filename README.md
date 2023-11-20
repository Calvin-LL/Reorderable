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
