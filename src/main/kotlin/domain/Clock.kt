package domain

import domain.Player.Black
import domain.Player.White
import java.time.Duration

data class Clock(
    internal val white: Duration,
    internal val black: Duration
) {
    operator fun get(player: Player) = when (player) {
        White -> white
        Black -> black
    }
}

fun Clock.reduce(player: Player, duration: Duration) = when (player) {
    White -> copy(white = white - duration)
    Black -> copy(black = black - duration)
}