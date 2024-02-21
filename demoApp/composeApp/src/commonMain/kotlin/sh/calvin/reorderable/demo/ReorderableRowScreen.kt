package sh.calvin.reorderable.demo

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import sh.calvin.reorderable.ReorderableRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReorderableRowScreen() {
    val haptic = rememberReorderHapticFeedback()

    var list by remember { mutableStateOf(items.take(5)) }

    ReorderableRow(
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
            haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
        },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) { _, item, _ ->
        key(item.id) {
            val interactionSource = remember { MutableInteractionSource() }

            Card(
                onClick = {},
                modifier = Modifier
                    .width(item.size.dp)
                    .height(128.dp),
                interactionSource = interactionSource,
            ) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(
                        modifier = Modifier.draggableHandle(
                            onDragStarted = {
                                haptic.performHapticFeedback(ReorderHapticFeedbackType.START)
                            },
                            onDragStopped = {
                                haptic.performHapticFeedback(ReorderHapticFeedbackType.END)
                            },
                            interactionSource = interactionSource,
                        ),
                        onClick = {},
                    ) {
                        Icon(Icons.Rounded.DragHandle, contentDescription = "Reorder")
                    }

                    Text(item.text, Modifier.padding(8.dp))
                }
            }
        }
    }
}