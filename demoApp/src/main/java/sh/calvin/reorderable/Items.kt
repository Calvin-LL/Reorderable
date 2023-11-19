package sh.calvin.reorderable

import java.util.UUID

data class Item(val id: UUID = UUID.randomUUID(), val text: String, val size: Int)

val items = (0..200).map {
    Item(text = "Item #$it", size = if (it % 2 == 0) 50 else 100)
}