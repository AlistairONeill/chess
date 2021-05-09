package render

import domain.*
import domain.Piece.*

fun Board.render() = Rank.values()
    .reversed()
    .joinToString("\n") { rank ->
        File.values()
            .joinToString("") { file ->
                get(Position(file, rank))?.render() ?: " "
            }
    }


fun PieceState.render() = when (player) {
    Player.White -> when (piece) {
        pawn -> "♙"
        rook -> "♖"
        knight -> "♘"
        bishop -> "♗"
        queen -> "♕"
        king -> "♔"
    }
    Player.Black -> when (piece) {
        pawn -> "♟"
        rook -> "♜"
        knight -> "♞"
        bishop -> "♝"
        queen -> "♛"
        king -> "♚"
    }
}

fun GameState.render() = listOf(
    board.render(),
    "",
    "White: ${clock.white}",
    "Black: ${clock.black}",
    "",
    "$toMove to move",
    ""
).joinToString("\n")

