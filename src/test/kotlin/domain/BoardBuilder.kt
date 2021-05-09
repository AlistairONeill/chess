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

        fun pawn(file: File, rank: Int) =
            add(Pawn, file, rank)

        fun rook(file: File, rank: Int) =
            add(Rook, file, rank)

        fun knight(file: File, rank: Int) =
            add(Knight, file, rank)

        fun bishop(file: File, rank: Int) =
            add(Bishop, file, rank)

        fun queen(file: File, rank: Int) =
            add(Queen, file, rank)

        fun king(file: File, rank: Int) =
            add(King, file, rank)

        private fun add(piece: Piece, file: File, rank: Int) {
            data[Position(file, rank.toRank())] = PieceState(piece, player)
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