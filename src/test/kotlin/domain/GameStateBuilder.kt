package domain

import domain.Player.*
import java.time.Duration
import java.time.Instant

class GameStateBuilder {
    companion object {
        fun gameState(block: GameStateBuilder.() -> Unit): GameState {
            val builder = GameStateBuilder()
            block(builder)
            return builder.build()
        }
    }

    private var board = Board.mint()
    private var flags = GameState.Flags.mint()
    private val clock = Clock(Duration.ofMinutes(15), Duration.ofMinutes(15))


    fun board(block: BoardBuilder.() -> Unit) {
        val builder = BoardBuilder()
        block(builder)
        board = builder.build()
    }

    fun flags(block: FlagsBuilder.() -> Unit) {
        val builder = FlagsBuilder()
        block(builder)
        flags = builder.build()
    }

    fun build() = GameState(
        board,
        Instant.now(),
        clock,
        flags
    )
}

class FlagsBuilder {
    var toMove = White
    var pawnCharge: Position? = null
    private var white: GameState.Flags.PlayerFlags = GameState.Flags.PlayerFlags.mint()
    private var black: GameState.Flags.PlayerFlags = GameState.Flags.PlayerFlags.mint()

    class PlayerFlagsBuilder {
        var kingMoved = false
        var kingRookMoved = false
        var queenRookMoved = false

        fun build() = GameState.Flags.PlayerFlags(
            kingMoved,
            kingRookMoved,
            queenRookMoved
        )
    }

    fun white(block: PlayerFlagsBuilder.() -> Unit) {
        val builder = PlayerFlagsBuilder()
        block(builder)
        white = builder.build()
    }

    fun black(block: PlayerFlagsBuilder.() -> Unit) {
        val builder = PlayerFlagsBuilder()
        block(builder)
        black = builder.build()
    }

    fun build() = GameState.Flags(
        toMove,
        pawnCharge,
        white,
        black
    )
}