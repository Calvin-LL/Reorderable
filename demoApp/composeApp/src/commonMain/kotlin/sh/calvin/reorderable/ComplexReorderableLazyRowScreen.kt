package sh.calvin.reorderable

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComplexReorderableLazyRowScreen() {
    val haptic = LocalHapticFeedback.current

    var list by remember { mutableStateOf(items) }
    val lazyListState = rememberLazyListState()
    val reorderableLazyRowState = rememberReorderableLazyRowState(lazyListState) { from, to ->
        list = list.toMutableList().apply {
            // can't use .index because there are other items in the list (headers, footers, etc)
            val fromIndex = indexOfFirst { it.id == from.key }
            val toIndex = indexOfFirst { it.id == to.key }

            add(toIndex, removeAt(fromIndex))
        }

        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    LazyRow(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                "Header",
                Modifier
                    .height(144.dp)
                    .padding(8.dp),
                MaterialTheme.colorScheme.onBackground,
            )
        }
        list.chunked(5).forEachIndexed { index, subList ->
            stickyHeader {
                Text(
                    "$index",
                    Modifier
                        .animateItemPlacement()
                        .height(144.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(8.dp),
                    MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            items(subList, key = { it.id }) {
                ReorderableItem(reorderableLazyRowState, it.id) { isDragging ->
                    val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                    Card(
                        modifier = Modifier
                            .width(it.size.dp)
                            .height(128.dp)
                            .padding(vertical = 8.dp),
                        shadowElevation = elevation,
                    ) {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            IconButton(
                                modifier = Modifier.draggableHandle(
                                    onDragStarted = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    onDragStopped = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                ),
                                onClick = {},
                            ) {
                                Icon(Icons.Rounded.DragHandle, contentDescription = "Reorder")
                            }
                            Text(it.text, Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
        item {
            Text("Footer", Modifier.padding(8.dp), MaterialTheme.colorScheme.onBackground)
        }
    }
}