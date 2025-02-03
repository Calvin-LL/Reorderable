package sh.calvin.reorderable.demo.ui

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.demo.ReorderHapticFeedbackType
import sh.calvin.reorderable.demo.items
import sh.calvin.reorderable.demo.rememberReorderHapticFeedback
import sh.calvin.reorderable.rememberReorderableLazyListState

var list1 = items.take(3)
var list2 = items.slice(3..5)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TwoReorderableLazyColumnScreen() {
    val haptic = rememberReorderHapticFeedback()

    var combinedList by remember { mutableStateOf(list1 + list2) }

    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
        combinedList = combinedList.toMutableList().apply {
            // can't use .index because there are other items in the list (headers, footers, etc)
            val fromIndex = indexOfFirst { it.id == from.key }
            val toIndex = indexOfFirst { it.id == to.key }

            add(toIndex, removeAt(fromIndex))
        }

        list1 = combinedList.take(3)
        list2 = combinedList.slice(3..5)

        haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        combinedList.chunked(3).forEachIndexed { group, subList ->
            if (group != 0) {
                item {
                    Text(
                        "Another list",
                        Modifier
                            .fillMaxWidth()
                            .animateItem(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            itemsIndexed(subList, key = { _, item -> item.id }) { index, item ->
                ReorderableItem(reorderableLazyColumnState, item.id) {
                    val interactionSource = remember { MutableInteractionSource() }

                    Card(
                        onClick = {},
                        modifier = Modifier
                            .height(item.size.dp)
                            .padding(horizontal = 8.dp),
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
        // this one will lose the dragging item when it moves to the next list
//        itemsIndexed(combinedList.take(3), key = { _, item -> item.id }) { index, item ->
//            ReorderableItem(reorderableLazyColumnState, item.id) {
//                val interactionSource = remember { MutableInteractionSource() }
//
//                Card(
//                    onClick = {},
//                    modifier = Modifier
//                        .height(item.size.dp)
//                        .padding(horizontal = 8.dp),
//                    interactionSource = interactionSource,
//                ) {
//                    Row(
//                        Modifier.fillMaxSize(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        Text(item.text, Modifier.padding(horizontal = 8.dp))
//                        IconButton(
//                            modifier = Modifier
//                                .draggableHandle(
//                                    onDragStarted = {
//                                        haptic.performHapticFeedback(ReorderHapticFeedbackType.START)
//                                    },
//                                    onDragStopped = {
//                                        haptic.performHapticFeedback(ReorderHapticFeedbackType.END)
//                                    },
//                                    interactionSource = interactionSource,
//                                )
//                                .clearAndSetSemantics { },
//                            onClick = {},
//                        ) {
//                            Icon(Icons.Rounded.DragHandle, contentDescription = "Reorder")
//                        }
//                    }
//                }
//            }
//        }
//        item {
//            Text(
//                "Another list",
//                Modifier
//                    .fillMaxWidth()
//                    .animateItem(),
//                textAlign = TextAlign.Center
//            )
//        }
//        itemsIndexed(combinedList.slice(3..5), key = { _, item -> item.id }) { index, item ->
//            ReorderableItem(reorderableLazyColumnState, item.id) {
//                val interactionSource = remember { MutableInteractionSource() }
//
//                Card(
//                    onClick = {},
//                    modifier = Modifier
//                        .height(item.size.dp)
//                        .padding(horizontal = 8.dp),
//                    interactionSource = interactionSource,
//                ) {
//                    Row(
//                        Modifier.fillMaxSize(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        Text(item.text, Modifier.padding(horizontal = 8.dp))
//                        IconButton(
//                            modifier = Modifier
//                                .draggableHandle(
//                                    onDragStarted = {
//                                        haptic.performHapticFeedback(ReorderHapticFeedbackType.START)
//                                    },
//                                    onDragStopped = {
//                                        haptic.performHapticFeedback(ReorderHapticFeedbackType.END)
//                                    },
//                                    interactionSource = interactionSource,
//                                )
//                                .clearAndSetSemantics { },
//                            onClick = {},
//                        ) {
//                            Icon(Icons.Rounded.DragHandle, contentDescription = "Reorder")
//                        }
//                    }
//                }
//            }
//        }
    }
}
