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
                "e4" to move(pawn, e, 4),
                "d6" to move(pawn, d, 6),
                "d4" to move(pawn, d, 4),
                "Nf6" to move(knight, f, 6),
                "Nc3" to move(knight, c, 3),
                "g6" to move(pawn, g, 6),
                "Be3" to move(bishop, e, 3),
                "Bg7" to move(bishop, g, 7),
                "Qd2" to move(queen, d, 2),
                "c6" to move(pawn, c, 6),
                "f3" to move(pawn, f, 3),
                "b5" to move(pawn, b, 5),
                "Nge2" to move(knight, e, 2, disambiguationFile = g),
                "Nbd7" to move(knight, d, 7, disambiguationFile = b),
                "Bh6" to move(bishop, h, 6),
                "Bxh6" to take(bishop, h, 6),
                "Qxh6" to take(queen, h, 6),
                "Bb7" to move(bishop, b, 7),
                "a3" to move(pawn, a, 3),
                "e5" to move(pawn, e, 5),
                "O-O-O" to Intention(QueensSideCastle, None),
                "Qe7" to move(queen, e, 7),
                "Kb1" to move(king, b, 1),
                "a6" to move(pawn, a, 6),
                "Nc1" to move(knight, c, 1),
                "O-O-O" to Intention(QueensSideCastle, None),
                "Nb3" to move(knight, b, 3),
                "exd4" to take(pawn, d, 4, disambiguationFile = e),
                "Rxd4" to take(rook, d, 4),
                "c5" to move(pawn, c, 5),
                "Rd1" to move(rook, d, 1),
                "Nb6" to move(knight, b, 6),
                "g3" to move(pawn, g, 3),
                "Kb8" to move(king, b, 8),
                "Na5" to move(knight, a, 5),
                "Ba8" to move(bishop, a, 8),
                "Bh3" to move(bishop, h, 3),
                "d5" to move(pawn, d, 5),
                "Qf4+" to move(queen, f, 4, checkStatus = Check),
                "Ka7" to move(king, a, 7),
                "Rhe1" to move(rook, e, 1, disambiguationFile = h),
                "d4" to move(pawn, d, 4),
                "Nd5" to move(knight, d, 5),
                "Nbxd5" to take(knight, d, 5, disambiguationFile = b),
                "exd5" to take(pawn, d, 5, disambiguationFile = e),
                "Qd6" to move(queen, d, 6),
                "Rxd4" to take(rook, d, 4),
                "cxd4" to take(pawn, d, 4, disambiguationFile = c),
                "Re7+" to move(rook, e, 7, checkStatus = Check),
                "Kb6" to move(king, b, 6),
                "Qxd4+" to take(queen, d, 4, checkStatus = Check),
                "Kxa5" to take(king, a, 5),
                "b4+" to move(pawn, b, 4, checkStatus = Check),
                "Ka4" to move(king, a, 4),
                "Qc3" to move(queen, c, 3),
                "Qxd5" to take(queen, d, 5),
                "Ra7" to move(rook, a, 7),
                "Bb7" to move(bishop, b, 7),
                "Rxb7" to take(rook, b, 7),
                "Qc4" to move(queen, c, 4),
                "Qxf6" to take(queen, f, 6),
                "Kxa3" to take(king, a, 3),
                "Qxa6+" to take(queen, a, 6, checkStatus = Check),
                "Kxb4" to take(king, b, 4),
                "c3+" to move(pawn, c, 3, checkStatus = Check),
                "Kxc3" to take(king, c, 3),
                "Qa1+" to move(queen, a, 1, checkStatus = Check),
                "Kd2" to move(king, d, 2),
                "Qb2+" to move(queen, b, 2, checkStatus = Check),
                "Kd1" to move(king, d, 1),
                "Bf1" to move(bishop, f, 1),
                "Rd2" to move(rook, d, 2),
                "Rd7" to move(rook, d, 7),
                "Rxd7" to take(rook, d, 7),
                "Bxc4" to take(bishop, c, 4),
                "bxc4" to take(pawn, c, 4, disambiguationFile = b),
                "Qxh8" to take(queen, h, 8),
                "Rd3" to move(rook, d, 3),
                "Qa8" to move(queen, a, 8),
                "c3" to move(pawn, c, 3),
                "Qa4+" to move(queen, a, 4, checkStatus = Check),
                "Ke1" to move(king, e, 1),
                "f4" to move(pawn, f, 4),
                "f5" to move(pawn, f, 5),
                "Kc1" to move(king, c, 1),
                "Rd2" to move(rook, d, 2),
                "Qa7" to move(queen, a, 7)
            ).map { Move(it.first) to it.second },
            board {
                white {
                    queen(a, 7)
                    king(c, 1)
                    pawn(f, 4)
                    pawn(g, 3)
                    pawn(h, 2)
                }
                black {
                    pawn(h, 7)
                    pawn(g, 6)
                    pawn(f, 5)
                    pawn(c, 3)
                    rook(d, 2)
                    king(e, 1)
                }
            }
    )

    private val DeepBlueVsKasparov1 = "1.e4 c5 2.c3 d5 3.exd5 Qxd5 4.d4 Nf6 5.Nf3 Bg4 6.Be2 e6 7.h3 Bh5 8.0-0 Nc6 9.Be3 cxd4 10.cxd4 Bb4 11.a3 Ba5 12.Nc3 Qd6 13.Nb5 Qe7 14.Ne5 Bxe2 15.Qxe2 0-0 16.Rac1 Rac8 17.Bg5 Bb6 18.Bxf6 gxf6 19.Nc4 Rfd8 20.Nxb6 axb6 21.Rfd1 f5 22.Qe3 Qf6 23.d5 Rxd5 24.Rxd5 exd5 25.b3 Kh8 26.Qxb6 Rg8 27.Qc5 d4 28.Nd6 f4 29.Nxb7 Ne5 30.Qd5 f3 31.g3 Nd3 32.Rc7 Re8 33.Nd6 Re1+ 34.Kh2 Nxf2 35.Nxf7+ Kg7 36.Ng5+ Kh6 37.Rxh7+"
        .toExampleGame(
            board {
                white {
                    rook(h, 7)
                    knight(g, 5)
                    queen(d, 5)
                    pawn(a, 3)
                    pawn(b, 3)
                    pawn(g, 3)
                    pawn(h, 3)
                    king(h, 2)
                }
                black {
                    king(h, 6)
                    queen(f, 6)
                    pawn(d, 4)
                    pawn(f, 3)
                    knight(f, 2)
                    rook(e, 1)
                }
            }
        )

    private val DeepBlueVsKasparov2 = "1.Nf3 d5 2.d4 e6 3.g3 c5 4.Bg2 Nc6 5.0-0 Nf6 6.c4 dxc4 7.Ne5 Bd7 8.Na3 cxd4 9.Naxc4 Bc5 10.Qb3 0-0 11.Qxb7 Nxe5 12.Nxe5 Rb8 13.Qf3 Bd6 14.Nc6 Bxc6 15.Qxc6 e5 16.Rb1 Rb6 17.Qa4 Qb8 18.Bg5 Be7 19.b4 Bxb4 20.Bxf6 gxf6 21.Qd7 Qc8 22.Qxa7 Rb8 23.Qa4 Bc3 24.Rxb8 Qxb8 25.Be4 Qc7 26.Qa6 Kg7 27.Qd3 Rb8 28.Bxh7 Rb2 29.Be4 Rxa2 30.h4 Qc8 31.Qf3 Ra1 32.Rxa1 Bxa1 33.Qh5 Qh8 34.Qg4+ Kf8 35.Qc8+ Kg7 36.Qg4+ Kf8 37.Bd5 Ke7 38.Bc6 Kf8 39.Bd5 Ke7 40.Qf3 Bc3 41.Bc4 Qc8 42.Qd5 Qe6 43.Qb5 Qd7 44.Qc5+ Qd6 45.Qa7+ Qd7 46.Qa8 Qc7 47.Qa3+ Qd6 48.Qa2 f5 49.Bxf7 e4 50.Bh5 Qf6 51.Qa3+ Kd7 52.Qa7+ Kd8 53.Qb8+ Kd7 54.Be8+ Ke7 55.Bb5 Bd2 56.Qc7+ Kf8 57.Bc4 Bc3 58.Kg2 Be1 59.Kf1 Bc3 60.f4 exf3 61.exf3 Bd2 62.f4 Ke8 63.Qc8+ Ke7 64.Qc5+ Kd8 65.Bd3 Be3 66.Qxf5 Qc6 67.Qf8+ Kc7 68.Qe7+ Kc8 69.Bf5+ Kb8 70.Qd8+ Kb7 71.Qd7+ Qxd7 72.Bxd7 Kc7 73.Bb5"
        .toExampleGame(
            board {
                white {
                    bishop(b, 5)
                    pawn(f, 4)
                    pawn(g, 3)
                    pawn(h, 4)
                    king(f, 1)
                }
                black {
                    king(c, 7)
                    pawn(d, 4)
                    bishop(e, 3)
                }
            }
        )

    private val DeepBlueVsKasparov3 = "1.Nf3 d5 2.g3 Bg4 3.Bg2 Nd7 4.h3 Bxf3 5.Bxf3 c6 6.d3 e6 7.e4 Ne5 8.Bg2 dxe4 9.Bxe4 Nf6 10.Bg2 Bb4+ 11.Nd2 h5 12.Qe2 Qc7 13.c3 Be7 14.d4 Ng6 15.h4 e5 16.Nf3 exd4 17.Nxd4 0-0-0 18.Bg5 Ng4 19.0-0-0 Rhe8 20.Qc2 Kb8 21.Kb1 Bxg5 22.hxg5 N6e5 23.Rhe1 c5 24.Nf3 Rxd1+ 25.Rxd1 Nc4 26.Qa4 Rd8 27.Re1 Nb6 28.Qc2 Qd6 29.c4 Qg6 30.Qxg6 fxg6 31.b3 Nxf2 32.Re6 Kc7 33.Rxg6 Rd7 34.Nh4 Nc8 35.Bd5 Nd6 36.Re6 Nb5 37.cxb5 Rxd5 38.Rg6 Rd7 39.Nf5 Ne4 40.Nxg7 Rd1+ 41.Kc2 Rd2+ 42.Kc1 Rxa2 43.Nxh5 Nd2 44.Nf4 Nxb3+ 45.Kb1 Rd2 46.Re6 c4 47.Re3 Kb6 48.g6 Kxb5 49.g7 Kb4"
        .toExampleGame(
            board {
                white {
                    pawn(g, 7)
                    pawn(g, 3)
                    knight(f, 4)
                    king(b, 1)
                    rook(e, 3)
                }
                black {
                    pawn(a, 7)
                    pawn(b, 7)
                    king(b, 4)
                    knight(b, 3)
                    pawn(c, 4)
                    rook(d, 2)
                }
            }
        )

    val allExampleGames = listOf(
        KasparovTopalov,
        DeepBlueVsKasparov1,
        DeepBlueVsKasparov2,
        DeepBlueVsKasparov3
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