package domain

enum class Piece {
    Pawn,
    Rook,
    Knight,
    Bishop,
    Queen,
    King
}

data class PieceState(
    val piece: Piece,
    val player: Player,
    val justMoved: Boolean = false,
    val hasMoved: Boolean = false
)