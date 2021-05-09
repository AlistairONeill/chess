package games

import app.*
import app.CheckStatus.Check
import app.CheckStatus.None
import domain.*
import domain.BoardBuilder.Companion.board
import domain.File.*
import domain.Piece.*
import domain.Rank.*

object ExampleGames {
    data class ExampleGame(
        val moves: List<Pair<Move, Intention?>>,
        val finalBoard: Board
    )

    private val KasparovTopalov =
        ExampleGame(
            listOf(
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
            ).map { Move(it.first) to it.second },
            board {
                white {
                    queen(A, 7)
                    king(C, 1)
                    pawn(F, 4)
                    pawn(G, 3)
                    pawn(H, 2)
                }
                black {
                    pawn(H, 7)
                    pawn(G, 6)
                    pawn(F, 5)
                    pawn(C, 3)
                    rook(D, 2)
                    king(E, 1)
                }
            }
    )

    private val DeepBlueVsKasparov1 = "1.e4 c5 2.c3 d5 3.exd5 Qxd5 4.d4 Nf6 5.Nf3 Bg4 6.Be2 e6 7.h3 Bh5 8.0-0 Nc6 9.Be3 cxd4 10.cxd4 Bb4 11.a3 Ba5 12.Nc3 Qd6 13.Nb5 Qe7 14.Ne5 Bxe2 15.Qxe2 0-0 16.Rac1 Rac8 17.Bg5 Bb6 18.Bxf6 gxf6 19.Nc4 Rfd8 20.Nxb6 axb6 21.Rfd1 f5 22.Qe3 Qf6 23.d5 Rxd5 24.Rxd5 exd5 25.b3 Kh8 26.Qxb6 Rg8 27.Qc5 d4 28.Nd6 f4 29.Nxb7 Ne5 30.Qd5 f3 31.g3 Nd3 32.Rc7 Re8 33.Nd6 Re1+ 34.Kh2 Nxf2 35.Nxf7+ Kg7 36.Ng5+ Kh6 37.Rxh7+"
        .toExampleGame(
            board {
                white {
                    rook(H, 7)
                    knight(G, 5)
                    queen(D, 5)
                    pawn(A, 3)
                    pawn(B, 3)
                    pawn(G, 3)
                    pawn(H, 3)
                    king(H, 2)
                }
                black {
                    king(H, 6)
                    queen(F, 6)
                    pawn(D, 4)
                    pawn(F, 3)
                    knight(F, 2)
                    rook(E, 1)
                }
            }
        )

    private val DeepBlueVsKasparov2 = "1.Nf3 d5 2.d4 e6 3.g3 c5 4.Bg2 Nc6 5.0-0 Nf6 6.c4 dxc4 7.Ne5 Bd7 8.Na3 cxd4 9.Naxc4 Bc5 10.Qb3 0-0 11.Qxb7 Nxe5 12.Nxe5 Rb8 13.Qf3 Bd6 14.Nc6 Bxc6 15.Qxc6 e5 16.Rb1 Rb6 17.Qa4 Qb8 18.Bg5 Be7 19.b4 Bxb4 20.Bxf6 gxf6 21.Qd7 Qc8 22.Qxa7 Rb8 23.Qa4 Bc3 24.Rxb8 Qxb8 25.Be4 Qc7 26.Qa6 Kg7 27.Qd3 Rb8 28.Bxh7 Rb2 29.Be4 Rxa2 30.h4 Qc8 31.Qf3 Ra1 32.Rxa1 Bxa1 33.Qh5 Qh8 34.Qg4+ Kf8 35.Qc8+ Kg7 36.Qg4+ Kf8 37.Bd5 Ke7 38.Bc6 Kf8 39.Bd5 Ke7 40.Qf3 Bc3 41.Bc4 Qc8 42.Qd5 Qe6 43.Qb5 Qd7 44.Qc5+ Qd6 45.Qa7+ Qd7 46.Qa8 Qc7 47.Qa3+ Qd6 48.Qa2 f5 49.Bxf7 e4 50.Bh5 Qf6 51.Qa3+ Kd7 52.Qa7+ Kd8 53.Qb8+ Kd7 54.Be8+ Ke7 55.Bb5 Bd2 56.Qc7+ Kf8 57.Bc4 Bc3 58.Kg2 Be1 59.Kf1 Bc3 60.f4 exf3 61.exf3 Bd2 62.f4 Ke8 63.Qc8+ Ke7 64.Qc5+ Kd8 65.Bd3 Be3 66.Qxf5 Qc6 67.Qf8+ Kc7 68.Qe7+ Kc8 69.Bf5+ Kb8 70.Qd8+ Kb7 71.Qd7+ Qxd7 72.Bxd7 Kc7 73.Bb5"
        .toExampleGame(
            board {
                white {
                    bishop(B, 5)
                    pawn(F, 4)
                    pawn(G, 3)
                    pawn(H, 4)
                    king(F, 1)
                }
                black {
                    king(C, 7)
                    pawn(D, 4)
                    bishop(E, 3)
                }
            }
        )

    val allExampleGames = listOf(
        KasparovTopalov,
        DeepBlueVsKasparov1,
        DeepBlueVsKasparov2
    )

    private fun String.toExampleGame(finalBoard: Board) =
        ExampleGame(
            split(" ")
                .map { it.split(".").last() }
                .map { Move(it) to null },
            finalBoard
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