package app

import domain.GameState
import domain.stamp
import games.ExampleGames
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import render.render
import java.time.Duration
import java.time.Instant

class EngineTest {
    @Test
    fun `can run through example games`() {
        ExampleGames.allExampleGames.forEach { game ->
            var gameState = GameState.mint(Instant.now(), Duration.ofMinutes(15))

            game.moves.forEach { (move, _) ->
                gameState = Engine.applyMove(gameState, move.stamp())
            }

            assertThat(
                gameState.board,
                equalTo(
                    game.finalBoard
                )
            )
        }
    }
}