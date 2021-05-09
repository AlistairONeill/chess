package domain

import domain.Piece.*

class BoardBuilder {
    companion object {
        fun buildBoard(block: BoardBuilder.() -> Unit): Board {
            val builder = BoardBuilder()
            block(builder)
            return builder.build()
        }
    }

    private var data = mutableMapOf<Position, PieceState>()

    class PlayerBuilder internal constructor(private val player: Player) {
        internal var data = mutableMapOf<Position, PieceState>()

        fun pawn(file: File, rank: Rank, justMoved: Boolean = false, hasMoved: Boolean = false) =
            add(Pawn, file, rank, justMoved, hasMoved)

        fun rook(file: File, rank: Rank, justMoved: Boolean = false, hasMoved: Boolean = false) =
            add(Rook, file, rank, justMoved, hasMoved)

        fun knight(file: File, rank: Rank, justMoved: Boolean = false, hasMoved: Boolean = false) =
            add(Knight, file, rank, justMoved, hasMoved)

        fun bishop(file: File, rank: Rank, justMoved: Boolean = false, hasMoved: Boolean = false) =
            add(Bishop, file, rank, justMoved, hasMoved)

        fun queen(file: File, rank: Rank, justMoved: Boolean = false, hasMoved: Boolean = false) =
            add(Queen, file, rank, justMoved, hasMoved)

        fun king(file: File, rank: Rank, justMoved: Boolean = false, hasMoved: Boolean = false) =
            add(King, file, rank, justMoved, hasMoved)

        private fun add(piece: Piece, file: File, rank: Rank, justMoved: Boolean = false, hasMoved: Boolean = false) {
            data[Position(file, rank)] = PieceState(piece, player, justMoved, hasMoved)
        }
    }

    fun white(block: PlayerBuilder.() -> Unit) {
        val builder = PlayerBuilder(Player.White)
        block(builder)
        data.putAll(builder.data)
    }

    fun black(block: PlayerBuilder.() -> Unit) {
        val builder = PlayerBuilder(Player.White)
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