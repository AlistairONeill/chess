package app

import domain.GameState
import domain.Move
import domain.stamp
import games.ExampleGames
import org.junit.jupiter.api.Test
import render.render
import java.time.Duration
import java.time.Instant

class EngineTest {
    @Test
    fun `it will be a miracle if this works`() {
        var gameState = GameState.mint(Instant.now(), Duration.ofMinutes(15))

        ExampleGames.KasparovTopalov.forEach{
            val notation = it.first
            println("---------")
            println(gameState.board.render())
            println("Attempting to apply:")
            println(notation)
            val stampedMove = Move(notation).stamp()
            gameState = Engine.applyMove(gameState, stampedMove)
        }

        println(gameState.board.render())
    }
}