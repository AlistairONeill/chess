package web

import domain.GameId
import domain.Move
import domain.PlayerId
import org.http4k.lens.Query
import org.http4k.lens.boolean
import org.http4k.lens.duration

object Lens {
    val gameId = Query.map(::GameId, GameId::value).required("gameId")
    val playerIdRequired = Query.map(::PlayerId, PlayerId::value).required("playerId")
    val playerIdOptional = Query.map(::PlayerId, PlayerId::value).optional("playerId")
    val move = Query.map(::Move, Move::notation).required("move")
    val public = Query.boolean().optional("public")
    val startingClock = Query.duration().optional("startingClock")
}