package domain

import domain.Piece.*
import domain.Player.*

data class Board(
    val squares: List<List<PieceState?>>
) {
    companion object {
        private fun piecesRow(player: Player) = listOf(
            PieceState(rook, player),
            PieceState(knight, player),
            PieceState(bishop, player),
            PieceState(queen, player),
            PieceState(king, player),
            PieceState(bishop, player),
            PieceState(knight, player),
            PieceState(rook, player)
        )

        private fun pawnRow(player: Player) = List(8) { PieceState(pawn, player) }

        private fun emptyRow() = List(8) { null }

        fun mint() = Board (
                listOf(
                    piecesRow(White),
                    pawnRow(White),
                    emptyRow(),
                    emptyRow(),
                    emptyRow(),
                    emptyRow(),
                    pawnRow(Black),
                    piecesRow(Black)
                )
            )
    }
}

operator fun Board.get(position: Position) = squares[position.rank.index][position.file.index]
