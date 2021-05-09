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
        val newBoard = when(val intendedMove = intention.move) {
            is KingSideCastle -> applyKingSideCastle()
            is QueensSideCastle -> applyQueenSideCastle()
            is SimpleIntendedMove -> applySimpleIntendedMove(intendedMove)
            is PawnPromotion -> applyPawnPromotion(intendedMove)
        }

        if (CheckFinder.isInDirectCheck(newBoard, gameState.toMove)) {
            throw InCheckException
        }

        return GameState(
            board = newBoard,
            clock = newClock,
            lastMove = stampedMove.stamp,
            toMove = gameState.toMove.other()
        )
    }

    private val backRank = when (gameState.toMove) {
        White -> ONE
        Black -> EIGHT
    }

    private fun isInCheck(file: File) = CheckFinder.isInCheck(gameState.board, Position(file, backRank), gameState.toMove.other())

    private fun assertKingHasNotMoved() {
        val pieceState = gameState.board[Position(E, backRank)]
        if (pieceState == null || pieceState.hasMoved || pieceState.piece != King || pieceState.player != gameState.toMove) throw InvalidCastleException("Your king has moved")
    }

    private fun assertRookHasNotMoved(file: File) {
        val pieceState = gameState.board[Position(file, backRank)]
        if (pieceState == null || pieceState.hasMoved || pieceState.piece != Rook || pieceState.player != gameState.toMove) throw InvalidCastleException("Your rook has moved")
    }

    private fun isEmpty(file: File) = gameState.board[Position(file, backRank)] == null

    private fun applyKingSideCastle(): Board {
        assertKingHasNotMoved()
        assertRookHasNotMoved(H)
        if (isInCheck(E)) throw InvalidCastleException("You cannot castle out of check")
        if (isInCheck(F) || isInCheck(G)) throw InvalidCastleException("You cannot castle through check")
        if (!isEmpty(F) || !isEmpty(G)) throw InvalidCastleException("There is a piece in the way")
        return gameState.board
            .refresh()
            .remove(Position(E, backRank))
            .remove(Position(H, backRank))
            .add(King, Position(G, backRank))
            .add(Rook, Position(F, backRank))
    }

    private fun applyQueenSideCastle(): Board {
        assertKingHasNotMoved()
        assertRookHasNotMoved(A)
        if (isInCheck(E)) throw InvalidCastleException("You cannot castle out of check")
        if (isInCheck(D) || isInCheck(C)) throw InvalidCastleException("You cannot castle through check")
        if (!isEmpty(D) || !isEmpty(C) || !isEmpty(B)) throw InvalidCastleException("There is a piece in the way")
        return gameState.board
            .refresh()
            .remove(Position(E, backRank))
            .remove(Position(A, backRank))
            .add(King, Position(C, backRank))
            .add(Rook, Position(D, backRank))
    }

    private fun applySimpleIntendedMove(intendedMove: SimpleIntendedMove) : Board {
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


        val possibleOrigins = if (intendedMove.piece == Pawn) {
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
                if (intendedMove.piece != Pawn && intendedMove.disambiguationFile != null) {
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

        return gameState.board
            .refresh()
            .remove(origin)
            .add(intendedMove.piece, intendedMove.destination)
    }

    private fun attemptEnPassantResolution(intendedMove: SimpleIntendedMove): Board? {
        if (intendedMove.piece != Pawn) return null
        if (!intendedMove.capture) return null
        if (intendedMove.disambiguationRank != null) throw UnnecessaryDisambiguationException

        val expectedRank = when(gameState.toMove) {
            White -> SIX
            Black -> THREE
        }

        if (intendedMove.destination.rank != expectedRank) return null
        if (intendedMove.disambiguationFile == null) throw AmbiguityException(Pawn)

        val originPosition = Position(
            intendedMove.disambiguationFile,
            when(gameState.toMove) {
                White -> FIVE
                Black -> FOUR
            }
        )

        val originState = gameState.board[originPosition] ?: return null

        if (originState.player != gameState.toMove) return null
        if (originState.piece != Pawn) return null

        val victimPosition = Position(
            intendedMove.destination.file,
            when(gameState.toMove) {
                White -> FIVE
                Black -> FOUR
            }
        )

        val victimState = gameState.board[victimPosition] ?: return null

        if (victimState.piece != Pawn) return null
        if (!victimState.justMoved) return null
        if (victimState.player == gameState.toMove) return null

        return gameState.board
            .refresh()
            .remove(victimPosition)
            .remove(originPosition)
            .add(Pawn, intendedMove.destination)
    }

    private val opponentsBackRank = when(gameState.toMove) {
        White -> EIGHT
        Black -> ONE
    }

    private fun applyPawnPromotion(intendedMove: PawnPromotion) : Board {
        if (intendedMove.destination.rank != opponentsBackRank) throw PromotingNotOnBackRank
        if (intendedMove.disambiguationRank != null) throw UnnecessaryDisambiguationException
        if (intendedMove.newPiece == Pawn || intendedMove.newPiece == King) throw CannotPromoteToException(intendedMove.newPiece)

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

        if (possibleOrigins.isEmpty()) throw CouldNotFindPieceException(Pawn)

        val origin = when (possibleOrigins.size) {
            0 -> throw CouldNotFindPieceException(Pawn)
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
                            0 -> throw CouldNotFindPieceException(Pawn)
                            1 -> slimmed.first()
                            else -> throw AmbiguityException(Pawn)
                        }
                    }
                    else -> throw AmbiguityException(Pawn)
                }
            }
        }

        return gameState.board
            .refresh()
            .remove(origin)
            .add(intendedMove.newPiece, intendedMove.destination)
    }

    private fun Board.refresh() = Board(
        squares.map { line ->
            line.map {
                it?.copy(
                    justMoved = false
                )
            }
        }
    )

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
                        gameState.toMove,
                        justMoved = true,
                        hasMoved = true
                    )
                }
                else {
                    pieceState
                }
            }
        }
    )
}

fun Game.state(): GameState {
    var state = GameState.mint(startTime, startingClock)
    stampedMoves.forEach { state = Engine.applyMove(state, it) }
    return state
}


