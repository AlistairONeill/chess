package web

import domain.GameId
import domain.Move
import domain.PlayerId
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.lens.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.Duration

class LensTest {
    /*
    val gameId = Query.map(domain::GameId, GameId::value).required("gameId")
    val playerIdRequired = Query.map(domain::PlayerId, PlayerId::value).required("playerId")
    val playerIdOptional = Query.map(domain::PlayerId, PlayerId::value).optional("playerId")
    val move = Query.map(domain::Move, Move::notation).required("move")
    val public = Query.boolean().optional("public")
    val startingClock = Query.duration().optional("startingClock")
    */

    @Test
    fun gameId() = assertRequired(
        Lens.gameId,
        "gameId",
        "foo",
        GameId("foo")
    )

    @Test
    fun playerIdRequired() = assertRequired(
        Lens.playerIdRequired,
        "playerId",
        "foo",
        PlayerId("foo")
    )

    @Test
    fun playerIdOptional() = assertOptional(
        Lens.playerIdOptional,
        "playerId",
        "foo",
        PlayerId("foo")
    )

    @Test
    fun move() = assertRequired(
        Lens.move,
        "move",
        "e4",
        Move("e4")
    )

    @Test
    fun public() = assertOptional(
        Lens.public,
        "public",
        "true",
        true
    )

    @Test
    fun startingClock() = assertOptional(
        Lens.startingClock,
        "startingClock",
        "PT10M20S",
        Duration.ofMinutes(10) + Duration.ofSeconds(20)
    )

    private fun <T> assertRequired(
        lens: BiDiLens<Request, T>,
        name: String,
        input: String,
        expected: T
    ) {
        assert(lens, name, input, expected)
        assertThrows(LensFailure::class.java) {
            lens(request())
        }
    }

    private fun <T> assertOptional(
        lens: BiDiLens<Request, T?>,
        name: String,
        input: String,
        expected: T
    ) {
        assert(lens, name, input, expected)
        assertThat(
            lens(request()),
            equalTo(null)
        )
    }

    private fun <T> assert(
        lens: BiDiLens<Request, T>,
        name: String,
        input: String,
        expected: T
    ) = assertThat(
        lens(
            request(name, input)
        ),
        equalTo(
            expected
        )
    )

    private fun request() = Request(GET, "/foo")
    private fun request(name: String, input: String) = request().query(name, input)
}