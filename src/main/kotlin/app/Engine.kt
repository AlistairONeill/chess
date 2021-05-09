package app

import domain.*
import domain.File.*
import domain.Piece.*
import domain.Player.*
import domain.Rank.*
import java.time.Duration

class Engine(
    private val gameState: GameState,
    private val stampedMove: StampedMove
) {
    companion object {
        fun applyMove(gameState: GameState, stampedMove: StampedMove) =
            Engine(gameState, stampedMove).run()
    }

    fun run(): GameState {
        val newClock = gameState.clock.reduce(
            gameState.toMove,
            Duration.between(gameState.lastMove, stampedMove.stamp)
        )
        val intention = stampedMove.move.parse()
        val outcome = when(val intendedMove = intention.move) {
            is KingSideCastle -> applyKingSideCastle()
            is QueensSideCastle -> applyQueenSideCastle()
            is SimpleIntendedMove -> applySimpleIntendedMove(intendedMove)
            is PawnPromotion -> applyPawnPromotion(intendedMove)
        }

        val newBoard = gameState.board.applyOutcome(outcome)

        if (CheckFinder.isInDirectCheck(newBoard, gameState.toMove)) {
            throw InCheckException
        }

        val newFlags = gameState.flags.applyOutcome(outcome)

        return GameState(
            board = newBoard,
            clock = newClock,
            lastMove = stampedMove.stamp,
            flags = newFlags
        )
    }

    private val backRank = when (gameState.toMove) {
        White -> ONE
        Black -> EIGHT
    }

    private fun isInCheck(file: File) = CheckFinder.isInCheck(gameState.board, Position(file, backRank), gameState.toMove.other())

    private fun isEmpty(file: File) = gameState.board[Position(file, backRank)] == null

    private fun applyKingSideCastle(): Outcome {
        if (gameState.currentPlayerFlags.kingMoved) throw InvalidCastleException("Your king has moved")
        if (gameState.currentPlayerFlags.kingSideRookMoved) throw InvalidCastleException("Your rook has moved")
        if (isInCheck(e)) throw InvalidCastleException("You cannot castle out of check")
        if (isInCheck(f) || isInCheck(g)) throw InvalidCastleException("You cannot castle through check")
        if (!isEmpty(f) || !isEmpty(g)) throw InvalidCastleException("There is a piece in the way")
        return KingSideCastleOutcome(gameState.toMove)
    }

    private fun applyQueenSideCastle(): Outcome {
        if (gameState.currentPlayerFlags.kingMoved) throw InvalidCastleException("Your king has moved")
        if (gameState.currentPlayerFlags.queenSideRookMoved) throw InvalidCastleException("Your rook has moved")
        if (isInCheck(e)) throw InvalidCastleException("You cannot castle out of check")
        if (isInCheck(d) || isInCheck(c)) throw InvalidCastleException("You cannot castle through check")
        if (!isEmpty(d) || !isEmpty(c) || !isEmpty(b)) throw InvalidCastleException("There is a piece in the way")
        return QueenSideCastleOutcome(gameState.toMove)
    }

    private fun applySimpleIntendedMove(intendedMove: SimpleIntendedMove) : Outcome {
        val targetPieceState = gameState.board[intendedMove.destination]

        if (intendedMove.capture) {
            if (targetPieceState == null) {
                return attemptEnPassantResolution(intendedMove) ?: throw NotACaptureException
            }
            if (targetPieceState.player == gameState.toMove) throw CapturingOwnPieceException
        }
        else {
            if (targetPieceState != null) throw ActuallyACaptureException
        }

        val possibleOrigins = if (intendedMove.piece == pawn) {
            if (intendedMove.capture) {
                OriginFinder.findPawnTakeOrigin(gameState.board, intendedMove.destination, gameState.toMove)
            }
            else {
                OriginFinder.findPawnMoveOrigin(gameState.board, intendedMove.destination, gameState.toMove)
            }
        }
        else {
            OriginFinder.findOrigin(gameState.board, intendedMove.destination, intendedMove.piece, gameState.toMove)
        }

        if (possibleOrigins.isEmpty()) throw CouldNotFindPieceException(intendedMove.piece)

        val origin = when (possibleOrigins.size) {
            0 -> throw CouldNotFindPieceException(intendedMove.piece)
            1 -> {
                if (intendedMove.disambiguationRank != null) {
                    throw UnnecessaryDisambiguationException
                }
                if (intendedMove.piece != pawn && intendedMove.disambiguationFile != null) {
                    throw UnnecessaryDisambiguationException
                }
                possibleOrigins.first()
            }
            else -> {
                when {
                    intendedMove.disambiguationFile != null -> {
                        val slimmed = possibleOrigins.filter { it.file == intendedMove.disambiguationFile }
                        when (slimmed.size) {
                            possibleOrigins.size -> throw UnnecessaryDisambiguationException
                            0 -> throw CouldNotFindPieceException(intendedMove.piece)
                            1 -> {
                                if (intendedMove.disambiguationRank != null) {
                                    throw UnnecessaryDisambiguationException
                                }
                                slimmed.first()
                            }
                            else -> {
                                if (intendedMove.disambiguationRank == null) {
                                    throw AmbiguityException(intendedMove.piece)
                                }

                                val evenMoreSlimmed = slimmed.filter { it.rank == intendedMove.disambiguationRank }
                                when (evenMoreSlimmed.size) {
                                    0 -> throw CouldNotFindPieceException(intendedMove.piece)
                                    1 -> evenMoreSlimmed.first()
                                    else -> throw AmbiguityException(intendedMove.piece)
                                }
                            }
                        }
                    }
                    intendedMove.disambiguationRank != null -> {
                        val slimmed = possibleOrigins.filter { it.rank == intendedMove.disambiguationRank }
                        when (slimmed.size) {
                            0 -> throw CouldNotFindPieceException(intendedMove.piece)
                            1 -> slimmed.first()
                            else -> throw AmbiguityException(intendedMove.piece)
                        }
                    }
                    else -> throw AmbiguityException(intendedMove.piece)
                }
            }
        }
        return SimpleMove(intendedMove.piece, origin, intendedMove.destination, gameState.toMove)
    }

    private fun attemptEnPassantResolution(intendedMove: SimpleIntendedMove): Outcome? {
        if (intendedMove.piece != pawn) return null
        if (!intendedMove.capture) return null
        if (intendedMove.disambiguationRank != null) throw UnnecessaryDisambiguationException
        if (gameState.flags.pawnCharge == null) return null

        val expectedRank = when(gameState.toMove) {
            White -> SIX
            Black -> THREE
        }

        if (intendedMove.destination.rank != expectedRank) return null
        if (intendedMove.disambiguationFile == null) throw AmbiguityException(pawn)

        val originRank = when(gameState.toMove) {
            White -> FIVE
            Black -> FOUR
        }

        val originPosition = Position(
            intendedMove.disambiguationFile,
            originRank
        )

        val originState = gameState.board[originPosition] ?: return null

        if (originState.player != gameState.toMove) return null
        if (originState.piece != pawn) return null

        val victimPosition = Position(
            intendedMove.destination.file,
            originRank
        )

        if (victimPosition != gameState.flags.pawnCharge) return null


        val victimState = gameState.board[victimPosition] ?: return null
        if (victimState.piece != pawn) return null
        if (victimState.player == gameState.toMove) return null

        return EnPassantOutcome(originPosition, victimPosition, intendedMove.destination)
    }

    private val opponentsBackRank = when(gameState.toMove) {
        White -> EIGHT
        Black -> ONE
    }

    private fun applyPawnPromotion(intendedMove: PawnPromotion) : Outcome {
        if (intendedMove.destination.rank != opponentsBackRank) throw PromotingNotOnBackRank
        if (intendedMove.disambiguationRank != null) throw UnnecessaryDisambiguationException
        if (intendedMove.newPiece == pawn || intendedMove.newPiece == king) throw CannotPromoteToException(intendedMove.newPiece)

        val targetPieceState = gameState.board[intendedMove.destination]

        if (intendedMove.capture) {
            if (targetPieceState == null) throw NotACaptureException
            if (targetPieceState.player == gameState.toMove) throw CapturingOwnPieceException
        }
        else {
            if (targetPieceState != null) throw ActuallyACaptureException
        }

        val possibleOrigins = if (intendedMove.capture) {
            OriginFinder.findPawnTakeOrigin(gameState.board, intendedMove.destination, gameState.toMove)
        }
        else {
            OriginFinder.findPawnMoveOrigin(gameState.board, intendedMove.destination, gameState.toMove)
        }

        if (possibleOrigins.isEmpty()) throw CouldNotFindPieceException(pawn)

        val origin = when (possibleOrigins.size) {
            0 -> throw CouldNotFindPieceException(pawn)
            1 -> {
                if (intendedMove.disambiguationFile != null) {
                    throw UnnecessaryDisambiguationException
                }
                possibleOrigins.first()
            }
            else -> {
                when {
                    intendedMove.disambiguationFile != null -> {
                        val slimmed = possibleOrigins.filter { it.file == intendedMove.disambiguationFile }
                        when (slimmed.size) {
                            possibleOrigins.size -> throw UnnecessaryDisambiguationException
                            0 -> throw CouldNotFindPieceException(pawn)
                            1 -> slimmed.first()
                            else -> throw AmbiguityException(pawn)
                        }
                    }
                    else -> throw AmbiguityException(pawn)
                }
            }
        }

        return PawnPromotionOutcome(intendedMove.newPiece, origin, intendedMove.destination)
    }

    private fun Board.remove(position: Position) = Board(
        squares.mapIndexed { rank, line ->
            line.mapIndexed { file, pieceState ->
                if (position.rank.index == rank && position.file.index == file) null else pieceState
            }
        }
    )

    private fun Board.add(piece: Piece, position: Position) = Board(
        squares.mapIndexed { rank, line ->
            line.mapIndexed { file, pieceState ->
                if (position.rank.index == rank && position.file.index == file) {
                    PieceState(
                        piece,
                        gameState.toMove
                    )
                }
                else {
                    pieceState
                }
            }
        }
    )

    private fun Board.applyOutcome(outcome: Outcome): Board {
        var current = this
        outcome.toRemove.forEach {
            current = current.remove(it)
        }
        outcome.toAdd.forEach { (position, piece) ->
            current = current.add(piece, position)
        }
        return current
    }

    private fun GameState.Flags.applyOutcome(outcome: Outcome): GameState.Flags {
        val newCurrentPlayerFlags = currentPlayerFlags.run {
            copy(
                kingMoved = kingMoved || outcome.kingMoved,
                kingSideRookMoved = kingSideRookMoved || outcome.kingRookMoved,
                queenSideRookMoved = queenSideRookMoved || outcome.queenRookMoved
            )
        }

        return when (toMove) {
            White -> copy(
                toMove = Black,
                pawnCharge = outcome.pawnCharge,
                white = newCurrentPlayerFlags
            )
            Black -> copy(
                toMove = White,
                pawnCharge = outcome.pawnCharge,
                black = newCurrentPlayerFlags
            )
        }
    }
}

sealed class Outcome {
    abstract val toRemove: List<Position>
    abstract val toAdd: Map<Position, Piece>
    abstract val pawnCharge: Position?
    abstract val kingMoved: Boolean
    abstract val kingRookMoved: Boolean
    abstract val queenRookMoved: Boolean
}

class SimpleMove(piece: Piece, origin: Position, destination: Position, player: Player): Outcome() {
    override val toRemove = listOf(origin)
    override val toAdd = mapOf(destination to piece)
    override val pawnCharge = destination.takeIf {
        piece == pawn
                && (origin.file == destination.file)
                && ((origin.rank == TWO && destination.rank == FOUR)
                || (origin.rank == SEVEN && destination.rank == FIVE))
    }
    override val kingMoved = piece == king
    override val kingRookMoved = (piece == rook
            && origin == when(player) {
                White -> Position(h, ONE)
                Black -> Position(h, EIGHT)
            })
    override val queenRookMoved = (piece == rook
            && origin == when(player) {
                White -> Position(a, ONE)
                Black -> Position(a, EIGHT)
            })
}

class KingSideCastleOutcome(player: Player): Outcome() {
    private val backRank = when (player) {
        White -> ONE
        Black -> EIGHT
    }

    override val toRemove = listOf(
        Position(e, backRank),
        Position(h, backRank)
    )

    override val toAdd = mapOf(
        Position(f, backRank) to rook,
        Position(g, backRank) to king
    )

    override val pawnCharge: Position? = null
    override val kingMoved = true
    override val kingRookMoved = true
    override val queenRookMoved = false
}

class QueenSideCastleOutcome(player: Player): Outcome() {
    private val backRank = when (player) {
        White -> ONE
        Black -> EIGHT
    }

    override val toRemove = listOf(
        Position(e, backRank),
        Position(a, backRank)
    )

    override val toAdd = mapOf(
        Position(d, backRank) to rook,
        Position(c, backRank) to king
    )

    override val pawnCharge: Position? = null
    override val kingMoved = true
    override val kingRookMoved = false
    override val queenRookMoved = true
}

class PawnPromotionOutcome(piece: Piece, origin: Position, destination: Position): Outcome() {
    override val toRemove = listOf(origin)
    override val toAdd = mapOf(destination to piece)
    override val pawnCharge: Position? = null
    override val kingMoved = false
    override val kingRookMoved = false
    override val queenRookMoved = false
}

class EnPassantOutcome(origin: Position, victim: Position, destination: Position): Outcome() {
    override val toRemove = listOf(origin, victim)
    override val toAdd = mapOf(destination to pawn)
    override val pawnCharge: Position? = null
    override val kingMoved = false
    override val kingRookMoved = false
    override val queenRookMoved = false
}

fun Game.state(): GameState {
    var state = GameState.mint(startTime, startingClock)
    stampedMoves.forEach { state = Engine.applyMove(state, it) }
    return state
}
