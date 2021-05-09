package app

import domain.GameId
import domain.Piece

sealed class ChessException(override val message: String): Exception()
sealed class UnauthorizedException(message: String): ChessException(message)

class PrivateGameException: UnauthorizedException("This is a private game")
class NotYourTurnException: UnauthorizedException("It is not your turn")

class GameNotFoundException(gameId: GameId): ChessException("Could not find game with id [${gameId.value}]")

sealed class InvalidMoveException(message: String): ChessException(message)

class CouldNotParseException(notation: String): InvalidMoveException("Could not parse [$notation]")
object NotACaptureException: InvalidMoveException("This move is not a capture")
object InCheckException: InvalidMoveException("You can't end your turn in check")
class InvalidCastleException(reason: String): InvalidMoveException("You can't castle because: $reason")
object ActuallyACaptureException: InvalidMoveException("This move is actually a capture")
object CapturingOwnPieceException: InvalidMoveException("You can't capture your own piece...")
object UnnecessaryDisambiguationException: InvalidMoveException("You provided unnecessary disambiguation")
class AmbiguityException(piece: Piece): InvalidMoveException("There are multiple ${piece.name}s that you could be moving")
class CouldNotFindPieceException(piece: Piece): InvalidMoveException("Could not find the ${piece.name} you are trying to move")
object PromotingNotOnBackRank: InvalidMoveException("Nice try... You can't promote a pawn until it reaches the back rank!")
class CannotPromoteToException(piece: Piece): InvalidMoveException("You cannot promote a pawn to a ${piece.name}")