package sh.calvin.reorderable

data class Item(val id: Int, val text: String, val size: Int)

val items = (0..200).map {
    Item(id = it, text = "Item #$it", size = if (it % 2 == 0) 50 else 100)
}