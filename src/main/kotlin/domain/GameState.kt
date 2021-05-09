package domain

import domain.Player.White
import java.time.Duration
import java.time.Instant

data class GameState(
    val board: Board,
    val lastMove: Instant,
    val clock: Clock,
    val toMove: Player
) {
    companion object {
        fun mint(startTime: Instant, startingClock: Duration) : GameState {
            return GameState(
                Board.mint(),
                startTime,
                Clock(startingClock, startingClock),
                White
            )
        }
    }
}
