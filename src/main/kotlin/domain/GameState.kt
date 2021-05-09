package domain

import domain.Player.White
import java.time.Duration
import java.time.Instant

data class GameState(
    val board: Board,
    val lastMove: Instant,
    val clock: Clock,
    val flags: Flags
) {
    data class Flags(
        val toMove: Player,
        val justAdvancedPawn: Boolean,
        val white: PlayerFlags,
        val black: PlayerFlags
    ) {
        data class PlayerFlags(
            val kingMoved: Boolean,
            val kingSideRookMoved: Boolean,
            val queenSideRookMoved: Boolean
        ) {
            companion object {
                fun mint() = PlayerFlags(
                    kingMoved = false,
                    kingSideRookMoved = false,
                    queenSideRookMoved = false
                )
            }
        }

        companion object {
            fun mint() = Flags(
                toMove = White,
                justAdvancedPawn = false,
                white = PlayerFlags.mint(),
                black = PlayerFlags.mint()
            )
        }
    }

    companion object {
        fun mint(startTime: Instant, startingClock: Duration) : GameState {
            return GameState(
                Board.mint(),
                startTime,
                Clock(startingClock, startingClock),
                Flags.mint()
            )
        }
    }

    //TODO: REMOVE
    val toMove = flags.toMove
}