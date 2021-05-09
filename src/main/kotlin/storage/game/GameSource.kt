package storage.game

import domain.Game
import domain.GameId

interface GameSource {
    operator fun get(gameId: GameId): Game?
    fun put(game: Game)
}