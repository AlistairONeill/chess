package domain

@Suppress("EnumEntryName")
enum class Piece {
    pawn,
    rook,
    knight,
    bishop,
    queen,
    king
}

data class PieceState(
    val piece: Piece,
    val player: Player
)