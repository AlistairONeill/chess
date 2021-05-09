package web

import app.*
import domain.GameId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.then
import org.junit.jupiter.api.Test

class ErrorHandlingTest {

    @Test
    fun `Private Game Exception`() = assertStatus(
        PrivateGameException(),
        Status.UNAUTHORIZED,
        "This is a private game"
    )

    @Test
    fun `Not your turn`() = assertStatus(
        NotYourTurnException(),
        Status.UNAUTHORIZED,
        "It is not your turn"
    )

    @Test
    fun `Game not found`() = assertStatus(
        GameNotFoundException(GameId("foo")),
        Status.NOT_FOUND,
        "Could not find game with id [foo]"
    )

    @Test
    fun `Could not parse`() = assertStatus(
        CouldNotParseException("foo"),
        Status.BAD_REQUEST,
        "Could not parse [foo]"
    )

    @Test
    fun `Not a capture`() = assertStatus(
        NotACaptureException,
        Status.BAD_REQUEST,
        "This move is not a capture"
    )

    @Test
    fun `Generic Exception`() = assertStatus(
        Exception(),
        Status.INTERNAL_SERVER_ERROR,
        "Ruh-roh"
    )

    private fun assertStatus(exception: Exception, status: Status, message: String) =
        assertThat(
            ErrorHandling.then {
                throw exception
            } ( Request(Method.GET, "/foo") ),
            allOf(
                hasStatus(
                    status
                ),
                hasBody(
                    message
                )
            )

        )
}