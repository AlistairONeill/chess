package app

import domain.*
import domain.Player.Black
import domain.Player.White
import storage.game.GameSource
import java.time.Duration

interface ChessApp {
    fun newGame(public: Boolean, startingClock: Duration) : Game
    fun getGame(gameId: GameId, playerId: PlayerId?) : Game
    fun makeMove(gameId: GameId, playerId: PlayerId, move: Move) : Game
}

class RealChessApp(
    private val gameSource: GameSource
): ChessApp {
    override fun newGame(public: Boolean, startingClock: Duration) = Game.new(public, startingClock).also {
        gameSource.put(it)
    }

    override fun getGame(gameId: GameId, playerId: PlayerId?): Game {
        val game = gameSource[gameId] ?: throw GameNotFoundException(gameId)
        if (game.public
            || game.white == playerId
            || game.black == playerId) return game
        throw PrivateGameException()
    }

    override fun makeMove(gameId: GameId, playerId: PlayerId, move: Move): Game {
        val stampedMove = move.stamp()
        val game = getGame(gameId, playerId)
        val gameState = game.state()
        val expectedPlayerId = when (gameState.toMove) {
            White -> game.white
            Black -> game.black
        }
        if (playerId != expectedPlayerId) throw NotYourTurnException()

        Engine.applyMove(gameState, stampedMove)

        return game.copy(stampedMoves = game.stampedMoves + stampedMove).also {
            gameSource.put(it)
        }
    }
}