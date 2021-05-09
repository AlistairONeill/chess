package domain

import domain.Piece.*

class BoardBuilder {
    companion object {
        fun board(block: BoardBuilder.() -> Unit): Board {
            val builder = BoardBuilder()
            block(builder)
            return builder.build()
        }
    }

    private var data = mutableMapOf<Position, PieceState>()

    class PlayerBuilder internal constructor(private val player: Player) {
        internal var data = mutableMapOf<Position, PieceState>()

        infix fun Piece.on(shorthand: ShorthandPosition) {
            data[shorthand.position] = PieceState(this, player)
        }

    }

    fun white(block: PlayerBuilder.() -> Unit) {
        val builder = PlayerBuilder(Player.White)
        block(builder)
        data.putAll(builder.data)
    }

    fun black(block: PlayerBuilder.() -> Unit) {
        val builder = PlayerBuilder(Player.Black)
        block(builder)
        data.putAll(builder.data)
    }

    fun build() = Board(
        Rank.values().map { rank ->
            File.values().map { file ->
                data[Position(file, rank) ]
            }
        }
    )
}