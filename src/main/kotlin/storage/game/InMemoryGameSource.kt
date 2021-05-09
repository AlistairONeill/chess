package storage.game

import domain.Game
import domain.GameId

class InMemoryGameSource: GameSource {
    private val games = HashMap<GameId, Game>()
    override operator fun get(gameId: GameId) = games[gameId]
    override fun put(game: Game) { games[game.gameId] = game }
}