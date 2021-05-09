package app

import domain.*
import domain.Piece.*
import domain.Player.*

class OriginFinder(
    private val board: Board,
    private val position: Position,
    private val piece: Piece,
    private val player: Player
) {
    companion object {
        val orthogonalDirections = listOf(
            -1 to 0,
            1 to 0,
            0 to 1,
            0 to -1
        )

        val diagonalDirections = listOf(
            -1 to -1,
            -1 to 1,
            1 to -1,
            1 to 1
        )

        val allDirections = orthogonalDirections + diagonalDirections

        val knightOffsets = listOf(
            -2 to -1,
            -2 to 1,
            -1 to -2,
            -1 to 2,
            1 to -2,
            1 to 2,
            2 to -1,
            2 to 1
        )

        fun findOrigin(board: Board, position: Position, piece: Piece, player: Player) =
            OriginFinder(board, position, piece, player).find()

        fun findPawnMoveOrigin(board: Board, position: Position, player: Player) =
            PawnMoveOriginFinder(board, position, player).find()

        fun findPawnTakeOrigin(board: Board, position: Position, player: Player) =
            PawnTakeOriginFinder(board, position, player).find()
    }

    fun find(): List<Position> =
        when (piece) {
            rook -> orthogonalDirections.mapNotNull(this::firstPieceInDirection)
            knight -> knightOffsets.mapNotNull(this::tryOffset)
            bishop -> diagonalDirections.mapNotNull(this::firstPieceInDirection)
            queen -> allDirections.mapNotNull(this::firstPieceInDirection)
            king -> allDirections.mapNotNull(this::tryOffset)
            pawn -> throw Exception("SHOULD NOT BE COMING INTO HERE WITH PAWN QUERIES")
        }.filter {
            val pieceState = board[it]
            pieceState != null && pieceState.piece == piece && pieceState.player == player
        }

    private fun firstPieceInDirection(direction: Pair<Int, Int>): Position?  =
        try {
            var consider = position.add(direction)
            while (board[consider] == null) {
                consider = consider.add(direction)
            }
            consider
        }
        catch (e: ArrayIndexOutOfBoundsException) {
            null
        }

    private fun tryOffset(offset: Pair<Int, Int>): Position? =
        try {
            position.add(offset)
        }
        catch (e: ArrayIndexOutOfBoundsException) {
            null
        }
}

class PawnMoveOriginFinder(
    private val board: Board,
    private val position: Position,
    private val player: Player
) {
    //TODO: Rework!
    private val backwardsDirection = 0 to when(player) {
        White -> -1
        Black -> 1
    }

    fun find(): List<Position> =
        try {
            val oneBack = position.add(backwardsDirection)
            val oneState = board[oneBack]
            when {
                oneState == null -> {
                    val twoBack = oneBack.add(backwardsDirection)
                    val twoState = board[twoBack]
                    if (twoState != null && twoState.piece == pawn && twoState.player == player) {
                        listOf(twoBack)
                    }
                    else {
                        emptyList()
                    }
                }
                oneState.player == player && oneState.piece == pawn -> listOf(oneBack)
                else -> emptyList()
            }
        }
        catch(e: ArrayIndexOutOfBoundsException) {
            emptyList()
        }
}

class PawnTakeOriginFinder(
    private val board: Board,
    private val position: Position,
    private val player: Player
) {
    private val backwardsDirection = when(player) {
        White -> -1
        Black -> 1
    }

    private fun find(offset: Int): Position? =
        try {
            val consider = position.add(offset to backwardsDirection)
            val state = board[consider]
            if (state != null && state.piece == pawn && state.player == player) {
                consider
            }
            else {
                null
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            null
        }

    fun find(): List<Position> = listOf(-1, 1).mapNotNull(this::find)
}