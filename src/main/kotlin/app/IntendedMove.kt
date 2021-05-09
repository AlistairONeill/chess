package app

import app.CheckStatus.*
import domain.*
import domain.File.*
import domain.Piece.*
import domain.Rank.*

enum class CheckStatus {
    None, Check, Mate
}

data class Intention(
    val move: IntendedMove,
    val checkStatus: CheckStatus
)

sealed class IntendedMove

object KingSideCastle: IntendedMove()
object QueensSideCastle: IntendedMove()

data class SimpleIntendedMove(
    val piece: Piece,
    val capture: Boolean,
    val destination: Position,
    val disambiguationFile: File?,
    val disambiguationRank: Rank?
): IntendedMove()

data class PawnPromotion(
    val capture: Boolean,
    val destination: Position,
    val newPiece: Piece,
    val disambiguationFile: File?,
    val disambiguationRank: Rank?
): IntendedMove()

fun Move.parse(): Intention {
    val exception = CouldNotParseException(this.notation)
    var notation = notation.toList()

    fun checkEmpty() {
        if (notation.isEmpty()) throw exception
    }

    checkEmpty()

    // The last character might denote a check/mate
    val checkStatus = when (notation.last()) {
        '+' -> Check
        '#' -> Mate
        else -> None
    }

    if (checkStatus != None) {
        notation = notation.dropLast(1)
        checkEmpty()
    }

    // Check for castles
    if (notation == "O-O".toList() || notation == "0-0".toList()) return Intention(KingSideCastle, checkStatus)
    if (notation == "O-O-O".toList() || notation == "0-0-0".toList()) return Intention(QueensSideCastle, checkStatus)

    val movingPiece = notation.first().asPiece() ?: Pawn

    val promotion = if (movingPiece != Pawn) {
        notation = notation.drop(1)
        checkEmpty()
        null
    }
    else {
        notation.last().asPiece()
    }

    if (promotion != null) {
        notation = notation.dropLast(1)
        checkEmpty()
    }

    // Should now have position on the right hand side
    val destinationRank = notation.last().asRank() ?: throw exception
    notation = notation.dropLast(1)
    val destinationFile = notation.last().asFile() ?: throw exception
    notation = notation.dropLast(1)

    val destination = Position(destinationFile, destinationRank)

    val capture = notation.isNotEmpty() && notation.last() == 'x'

    if (capture) {
        notation = notation.dropLast(1)
    }

    val disambiguationFile = if (notation.isNotEmpty()) {
        notation.first().asFile()
    }
    else {
        null
    }

    if (disambiguationFile != null) {
        notation = notation.drop(1)
    }

    val disambiguationRank = if (notation.isNotEmpty()) {
        notation.first().asRank()
    }
    else {
        null
    }

    if (disambiguationRank != null) {
        notation = notation.drop(1)
    }

    // Everything should be parsed by this point!!!
    if (notation.isNotEmpty()) {
        throw exception
    }

    return Intention(
        if (promotion == null) {
            SimpleIntendedMove(
                movingPiece,
                capture,
                destination,
                disambiguationFile,
                disambiguationRank
            )
        }
        else {
            PawnPromotion(
                capture,
                destination,
                promotion,
                disambiguationFile,
                disambiguationRank
            )
        },
        checkStatus
    )
}

private fun Char.asPiece(): Piece? = when (this) {
    'R' -> Rook
    'N' -> Knight
    'B' -> Bishop
    'K' -> King
    'Q' -> Queen
    else -> null
}

private fun Char.asRank(): Rank? = when (this) {
    '1' -> ONE
    '2' -> TWO
    '3' -> THREE
    '4' -> FOUR
    '5' -> FIVE
    '6' -> SIX
    '7' -> SEVEN
    '8' -> EIGHT
    else -> null
}

private fun Char.asFile(): File? = when (this) {
    'a' -> A
    'b' -> B
    'c' -> C
    'd' -> D
    'e' -> E
    'f' -> F
    'g' -> G
    'h' -> H
    else -> null
}

