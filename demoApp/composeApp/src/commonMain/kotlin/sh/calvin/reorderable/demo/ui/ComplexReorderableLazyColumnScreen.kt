package sh.calvin.reorderable.demo.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Card
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
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.demo.ReorderHapticFeedbackType
import sh.calvin.reorderable.demo.items
import sh.calvin.reorderable.demo.rememberReorderHapticFeedback
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComplexReorderableLazyColumnScreen() {
    val haptic = rememberReorderHapticFeedback()

    var list by remember { mutableStateOf(items) }
    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
        list = list.toMutableList().apply {
            // can't use .index because there are other items in the list (headers, footers, etc)
            val fromIndex = indexOfFirst { it.id == from.key }
            val toIndex = indexOfFirst { it.id == to.key }

            add(toIndex, removeAt(fromIndex))
        }

        haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text("Header", Modifier.padding(8.dp), MaterialTheme.colorScheme.onBackground)
        }
        list.chunked(5).forEachIndexed { group, subList ->
            stickyHeader {
                Text(
                    "Sticky Header $group",
                    Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(8.dp),
                    MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            itemsIndexed(subList, key = { _, item -> item.id }) { subListIndex, item ->
                ReorderableItem(reorderableLazyColumnState, item.id) {
                    val interactionSource = remember { MutableInteractionSource() }

                    Card(
                        onClick = {},
                        modifier = Modifier
                            .height(item.size.dp)
                            .padding(horizontal = 8.dp)
                            .semantics {
                                customActions = listOf(
                                    CustomAccessibilityAction(
                                        label = "Move Up",
                                        action = {
                                            val index = subListIndex + group * 5

                                            if (index > 0) {
                                                list = list.toMutableList().apply {
                                                    add(index - 1, removeAt(index))
                                                }
                                                true
                                            } else {
                                                false
                                            }
                                        }
                                    ),
                                    CustomAccessibilityAction(
                                        label = "Move Down",
                                        action = {
                                            val index = subListIndex + group * 5

                                            if (index < list.size - 1) {
                                                list = list.toMutableList().apply {
                                                    add(index + 1, removeAt(index))
                                                }
                                                true
                                            } else {
                                                false
                                            }
                                        }
                                    ),
                                )
                            },
                        interactionSource = interactionSource,
                    ) {
                        Row(
                            Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(item.text, Modifier.padding(horizontal = 8.dp))
                            IconButton(
                                modifier = Modifier
                                    .draggableHandle(
                                        onDragStarted = {
                                            haptic.performHapticFeedback(ReorderHapticFeedbackType.START)
                                        },
                                        onDragStopped = {
                                            haptic.performHapticFeedback(ReorderHapticFeedbackType.END)
                                        },
                                        interactionSource = interactionSource,
                                    )
                                    .clearAndSetSemantics { },
                                onClick = {},
                            ) {
                                Icon(Icons.Rounded.DragHandle, contentDescription = "Reorder")
                            }
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
