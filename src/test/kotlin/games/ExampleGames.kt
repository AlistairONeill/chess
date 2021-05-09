package games

import app.*
import app.CheckStatus.Check
import app.CheckStatus.None
import domain.File
import domain.File.*
import domain.Piece
import domain.Piece.*
import domain.Position
import domain.Rank
import domain.Rank.*

object ExampleGames {
    val KasparovTopalov = listOf(
        "e4" to move(Pawn, E, 4),
        "d6" to move(Pawn, D, 6),
        "d4" to move(Pawn, D, 4),
        "Nf6" to move(Knight, F, 6),
        "Nc3" to move(Knight, C, 3),
        "g6" to move(Pawn, G, 6),
        "Be3" to move(Bishop, E, 3),
        "Bg7" to move(Bishop, G, 7),
        "Qd2" to move(Queen, D, 2),
        "c6" to move(Pawn, C, 6),
        "f3" to move(Pawn, F, 3),
        "b5" to move(Pawn, B, 5),
        "Nge2" to move(Knight, E, 2, disambiguationFile = G),
        "Nbd7" to move(Knight, D, 7, disambiguationFile = B),
        "Bh6" to move(Bishop, H, 6),
        "Bxh6" to take(Bishop, H, 6),
        "Qxh6" to take(Queen, H, 6),
        "Bb7" to move(Bishop, B, 7),
        "a3" to move(Pawn, A, 3),
        "e5" to move(Pawn, E, 5),
        "O-O-O" to Intention(QueensSideCastle, None),
        "Qe7" to move(Queen, E, 7),
        "Kb1" to move(King, B, 1),
        "a6" to move(Pawn, A, 6),
        "Nc1" to move(Knight, C, 1),
        "O-O-O" to Intention(QueensSideCastle, None),
        "Nb3" to move(Knight, B, 3),
        "exd4" to take(Pawn, D, 4, disambiguationFile = E),
        "Rxd4" to take(Rook, D, 4),
        "c5" to move(Pawn, C, 5),
        "Rd1" to move(Rook, D, 1),
        "Nb6" to move(Knight, B, 6),
        "g3" to move(Pawn, G, 3),
        "Kb8" to move(King, B, 8),
        "Na5" to move(Knight, A, 5),
        "Ba8" to move(Bishop, A, 8),
        "Bh3" to move(Bishop, H, 3),
        "d5" to move(Pawn, D, 5),
        "Qf4+" to move(Queen, F, 4, checkStatus = Check),
        "Ka7" to move(King, A, 7),
        "Rhe1" to move(Rook, E, 1, disambiguationFile = H),
        "d4" to move(Pawn, D, 4),
        "Nd5" to move(Knight, D, 5),
        "Nbxd5" to take(Knight, D, 5, disambiguationFile = B),
        "exd5" to take(Pawn, D, 5, disambiguationFile = E),
        "Qd6" to move(Queen, D, 6),
        "Rxd4" to take(Rook, D, 4),
        "cxd4" to take(Pawn, D, 4, disambiguationFile = C),
        "Re7+" to move(Rook, E, 7, checkStatus = Check),
        "Kb6" to move(King, B, 6),
        "Qxd4+" to take(Queen, D, 4, checkStatus = Check),
        "Kxa5" to take(King, A, 5),
        "b4+" to move(Pawn, B, 4, checkStatus = Check),
        "Ka4" to move(King, A, 4),
        "Qc3" to move(Queen, C, 3),
        "Qxd5" to take(Queen, D, 5),
        "Ra7" to move(Rook, A, 7),
        "Bb7" to move(Bishop, B, 7),
        "Rxb7" to take(Rook, B, 7),
        "Qc4" to move(Queen, C, 4),
        "Qxf6" to take(Queen, F, 6),
        "Kxa3" to take(King, A, 3),
        "Qxa6+" to take(Queen, A, 6, checkStatus = Check),
        "Kxb4" to take(King, B, 4),
        "c3+" to move(Pawn, C, 3, checkStatus = Check),
        "Kxc3" to take(King, C, 3),
        "Qa1+" to move(Queen, A, 1, checkStatus = Check),
        "Kd2" to move(King, D, 2),
        "Qb2+" to move(Queen, B, 2, checkStatus = Check),
        "Kd1" to move(King, D, 1),
        "Bf1" to move(Bishop, F, 1),
        "Rd2" to move(Rook, D, 2),
        "Rd7" to move(Rook, D, 7),
        "Rxd7" to take(Rook, D, 7),
        "Bxc4" to take(Bishop, C, 4),
        "bxc4" to take(Pawn, C, 4, disambiguationFile = B),
        "Qxh8" to take(Queen, H, 8),
        "Rd3" to move(Rook, D, 3),
        "Qa8" to move(Queen, A, 8),
        "c3" to move(Pawn, C, 3),
        "Qa4+" to move(Queen, A, 4, checkStatus = Check),
        "Ke1" to move(King, E, 1),
        "f4" to move(Pawn, F, 4),
        "f5" to move(Pawn, F, 5),
        "Kc1" to move(King, C, 1),
        "Rd2" to move(Rook, D, 2),
        "Qa7" to move(Queen, A, 7)
    )

    private fun take(
        piece: Piece,
        file: File,
        rank: Int,
        disambiguationFile: File? = null,
        disambiguationRank: Rank? = null,
        checkStatus: CheckStatus = None
    ) = Intention(
        SimpleIntendedMove(
            piece,
            true,
            Position(file, rank.asRank()),
            disambiguationFile,
            disambiguationRank
        ),
        checkStatus
    )

    private fun move(
        piece: Piece,
        file: File,
        rank: Int,
        disambiguationFile: File? = null,
        disambiguationRank: Rank? = null,
        checkStatus: CheckStatus = None
    ) = Intention(
        SimpleIntendedMove(
            piece,
            false,
            Position(file, rank.asRank()),
            disambiguationFile,
            disambiguationRank
        ),
        checkStatus
    )

    private fun Int.asRank() = when (this) {
        1 -> ONE
        2 -> TWO
        3 -> THREE
        4 -> FOUR
        5 -> FIVE
        6 -> SIX
        7 -> SEVEN
        8 -> EIGHT
        else -> throw Exception("You dun goof'd Alistair")
    }
}