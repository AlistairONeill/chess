package domain

enum class File(val index: Int) {
    A(0),
    B(1),
    C(2),
    D(3),
    E(4),
    F(5),
    G(6),
    H(7)
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