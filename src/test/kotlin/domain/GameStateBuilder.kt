package domain

import domain.Player.*
import java.time.Duration
import java.time.Instant

class GameStateBuilder: Builder<GameState> {
    companion object {
        val gameState = build(::GameStateBuilder)
    }

    private var board = Board.mint()
    private var flags = GameState.Flags.mint()
    private val clock = Clock(Duration.ofMinutes(15), Duration.ofMinutes(15))


    fun board(block: BoardBuilder.() -> Unit) {
        board = build(::BoardBuilder)(block)
    }

    fun flags(block: FlagsBuilder.() -> Unit) {
        flags = build(::FlagsBuilder)(block)
    }

    override fun build() = GameState(
        board,
        Instant.now(),
        clock,
        flags
    )
}

class FlagsBuilder: Builder<GameState.Flags> {
    var toMove = White
    var pawnCharge: Position? = null
    private var white: GameState.Flags.PlayerFlags = GameState.Flags.PlayerFlags.mint()
    private var black: GameState.Flags.PlayerFlags = GameState.Flags.PlayerFlags.mint()

    class PlayerFlagsBuilder: Builder<GameState.Flags.PlayerFlags> {
        var kingMoved = false
        var kingRookMoved = false
        var queenRookMoved = false

        override fun build() = GameState.Flags.PlayerFlags(
            kingMoved,
            kingRookMoved,
            queenRookMoved
        )
    }

    fun white(block: PlayerFlagsBuilder.() -> Unit) {
        white = build(::PlayerFlagsBuilder)(block)
    }

    fun black(block: PlayerFlagsBuilder.() -> Unit) {
        black = build(::PlayerFlagsBuilder)(block)
    }

    override fun build() = GameState.Flags(
        toMove,
        pawnCharge,
        white,
        black
    )
}