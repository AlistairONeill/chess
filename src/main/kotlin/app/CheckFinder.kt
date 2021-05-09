package app

import domain.*
import domain.Piece.*
import domain.Player.*

// CheckFinder not DangerFinder because screw worrying about en passant
class CheckFinder(
    private val board: Board,
    private val position: Position,
    private val from: Player
) {
    companion object {
        private val pieces = Piece.values().filter { it != Pawn }

        fun isInDirectCheck(board: Board, player: Player) =
            CheckFinder(board, board.findKing(player), player.other()).isInDanger()

        fun isInCheck(board: Board, position: Position, from: Player) =
            CheckFinder(board, position, from).isInDanger()
    }

    private val dangerPawnRankOffset = when (from) {
        White -> -1
        Black -> 1
    }

    private fun isInDangerFromPawn(fileOffset: Int) =
        try {
            val pieceState = board[position.add(fileOffset to dangerPawnRankOffset)]
            pieceState != null && pieceState.piece == Pawn && pieceState.player == from
        }
        catch (e: ArrayIndexOutOfBoundsException) {
            false
        }

    private fun isInDangerFrom(piece: Piece) = OriginFinder(board, position, piece, from).find()

    fun isInDanger() =
        isInDangerFromPawn(-1)
                || isInDangerFromPawn(1)
                || pieces.map(this::isInDangerFrom).flatten().isNotEmpty()
}

fun Board.findKing(player: Player): Position {
    for (file in File.values()) {
        for (rank in Rank.values()) {
            val position = Position(file, rank)
            val pieceState = get(position)
            if (pieceState != null && pieceState.piece == King && pieceState.player == player) {
                return position
            }
        }
    }
    throw Exception("Erm... The king has gone walkabouts")
}

