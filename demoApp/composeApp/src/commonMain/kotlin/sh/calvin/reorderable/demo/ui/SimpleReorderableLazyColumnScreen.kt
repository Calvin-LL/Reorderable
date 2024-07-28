package sh.calvin.reorderable.demo.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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

@Composable
fun SimpleReorderableLazyColumnScreen() {
    val haptic = rememberReorderHapticFeedback()

    var list by remember { mutableStateOf(items) }
    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
        list = list.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }

        haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(list, key = { _, item -> item.id }) { index, item ->
            ReorderableItem(reorderableLazyColumnState, item.id) {
                val interactionSource = remember { MutableInteractionSource() }

                Card(
                    onClick = {},
                    modifier = Modifier
                        .height(item.size.dp)
                        .semantics {
                            customActions = listOf(
                                CustomAccessibilityAction(
                                    label = "Move Up",
                                    action = {
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
}
