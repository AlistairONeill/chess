package domain

import java.time.Duration
import java.time.Instant
import java.util.*

data class GameId(val value: String) {
    companion object {
        fun mint() = GameId(UUID.randomUUID().toString())
    }
}

data class PlayerId(val value: String) {
    companion object {
        fun mint() = PlayerId(UUID.randomUUID().toString())
    }
}

data class StampedMove(val move: Move, val stamp: Instant)

data class Move(val notation: String)

fun Move.stamp() = StampedMove(this, Instant.now())

data class Game(
    val gameId: GameId,
    val startTime: Instant,
    val startingClock: Duration,
    val white: PlayerId,
    val black: PlayerId,
    val public: Boolean,
    val stampedMoves: List<StampedMove>
) {
    companion object {
        fun new(public: Boolean, startingClock: Duration) =
            Game(
                GameId.mint(),
                Instant.now(),
                startingClock,
                PlayerId.mint(),
                PlayerId.mint(),
                public,
                emptyList()
            )
    }
}