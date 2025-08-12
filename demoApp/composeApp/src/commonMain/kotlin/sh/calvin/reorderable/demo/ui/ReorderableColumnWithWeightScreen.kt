package sh.calvin.reorderable.demo.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import sh.calvin.reorderable.ReorderableColumn
import sh.calvin.reorderable.demo.ReorderHapticFeedbackType
import sh.calvin.reorderable.demo.items
import sh.calvin.reorderable.demo.rememberReorderHapticFeedback

@Composable
fun ReorderableColumnWithWeightScreen() {
    val haptic = rememberReorderHapticFeedback()

    var list by remember { mutableStateOf(items.take(3)) }

    ReorderableColumn(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        list = list,
        onSettle = { fromIndex, toIndex ->
            list = list.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            }
        },
        onMove = {
            haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
        },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) { index, item, _ ->
        key(item.id) {
            ReorderableItem(Modifier.weight(1f)) {
                val interactionSource = remember { MutableInteractionSource() }

                Card(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxHeight()
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
