package domain.json

import com.ubertob.kondor.json.*
import com.ubertob.kondor.json.datetime.JDuration
import com.ubertob.kondor.json.datetime.JInstant
import domain.*

object JGame : JAny<Game>() {
    private val gameId by JField(Game::gameId, JGameId)
    private val startTime by JField(Game::startTime, JInstant)
    private val startingClock by JField(Game::startingClock, JDuration)
    private val white by JField(Game::white, JPlayerId)
    private val black by JField(Game::black, JPlayerId)
    private val public by JField(Game::public, JBoolean)
    private val stampedMoves by JField(Game::stampedMoves, JList(JStampedMoves))

    override fun JsonNodeObject.deserializeOrThrow() =
        Game(
            +gameId,
            +startTime,
            +startingClock,
            +white,
            +black,
            +public,
            +stampedMoves
        )
}

object JGameId: JStringRepresentable<GameId>() {
    override val cons = ::GameId
    override val render = GameId::value
}

object JPlayerId: JStringRepresentable<PlayerId>() {
    override val cons = ::PlayerId
    override val render = PlayerId::value
}

object JStampedMoves: JAny<StampedMove>() {
    private val move by JField(StampedMove::move, JMove)
    private val stamp by JField(StampedMove::stamp, JInstant)

    override fun JsonNodeObject.deserializeOrThrow() =
        StampedMove(
            +move,
            +stamp
        )
}

object JMove: JStringRepresentable<Move>() {
    override val cons = ::Move
    override val render = Move::notation
}