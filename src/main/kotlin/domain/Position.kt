package domain

@Suppress("EnumEntryName")
enum class File(val index: Int) {
    a(0),
    b(1),
    c(2),
    d(3),
    e(4),
    f(5),
    g(6),
    h(7)
}

enum class Rank(val index: Int) {
    ONE(0),
    TWO(1),
    THREE(2),
    FOUR(3),
    FIVE(4),
    SIX(5),
    SEVEN(6),
    EIGHT(7)
}

data class Position(
    val file: File,
    val rank: Rank
)
fun Int.toRank() = Rank.values()[this - 1]
fun File.add(i: Int) = File.values()[index + i]
fun Rank.add(i: Int) = Rank.values()[index + i]
fun Position.add(d: Pair<Int, Int>) = copy(
    file = file.add(d.first),
    rank = rank.add(d.second)
)