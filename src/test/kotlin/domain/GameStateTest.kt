package domain

import domain.Player.Black
import domain.Player.White
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import render.render
import java.time.Duration
import java.time.Instant

class GameStateTest {
    @Test
    fun `board is initialised correctly`() {
        val now = Instant.now()
        val startingClock = Duration.ofMinutes(10)
        GameState.mint(
            now,
            startingClock
        ).run {
            assertThat(
                board.render(),
                equalTo(
                    "♜♞♝♛♚♝♞♜\n" +
                            "♟♟♟♟♟♟♟♟\n" +
                            "        \n" +
                            "        \n" +
                            "        \n" +
                            "        \n" +
                            "♙♙♙♙♙♙♙♙\n" +
                            "♖♘♗♕♔♗♘♖"
                )
            )

            assertThat(
                toMove,
                equalTo(
                    White
                )
            )

            assertThat(
                lastMove,
                equalTo(
                    now
                )
            )

            assertThat(
                clock[Black],
                equalTo(
                    startingClock
                )
            )

            assertThat(
                clock[White],
                equalTo(
                    startingClock
                )
            )
        }
    }
}