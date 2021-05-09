package web

import app.ChessApp
import app.state
import domain.Game
import domain.json.JGame
import org.http4k.contract.contract
import org.http4k.contract.meta
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.APPLICATION_JSON
import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.format.Jackson
import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import render.render
import java.time.Duration

class ChessRoutes(private val chessApp: ChessApp) {
    companion object {
        private const val API_DESCRIPTION_PATH = "/api/swagger.json"
    }

    private val contractRoutes = contract {
        renderer = OpenApi3(ApiInfo("chess api", "v1.0", "API description"), Jackson)
        descriptionPath = API_DESCRIPTION_PATH
        routes += ping()
        routes += newGame()
        routes += getGame()
        routes += makeMove()
    }

    private val swaggerUi = routes(
        "docs" bind Method.GET to {
            Response(Status.FOUND).header("Location", "/docs/index.html?url=$API_DESCRIPTION_PATH")
        },
        // For some reason the static handler does not work without "/" path prefix.
        "/docs" bind static(Classpath("META-INF/resources/webjars/swagger-ui/3.25.2"))
    )

    val handler = routes(
        swaggerUi,
        contractRoutes
    )

    private fun ping() =
        "/ping" meta {
            summary = "ping"
            description = "returns pong"
            produces += TEXT_PLAIN
            returning(OK to "pong")
            returning(INTERNAL_SERVER_ERROR to "Everything is FUBAR")
        } bindContract Method.GET to {
            Response(OK).body("pong")
        }

    private fun newGame() =
        "/game" meta {
            summary = "New Game"
            description = "Creates a New Game"
            produces += APPLICATION_JSON
            queries += Lens.public
            queries += Lens.startingClock
            returning(OK to "Game")
            returning(INTERNAL_SERVER_ERROR to "Everything is FUBAR")
            returning(BAD_REQUEST to "You sent something bad")
        } bindContract Method.PUT to { request ->
            chessApp.newGame(
                Lens.public(request) ?: false,
                Lens.startingClock(request) ?: Duration.ofMinutes(15)
            ).let {
                Response(OK).bodyAsJson(JGame, it)
            }
        }

    private fun getGame() =
        "/game" meta {
            summary = "View Game"
            description = "Views a game state"
            produces += TEXT_PLAIN
            queries += Lens.gameId
            queries += Lens.playerIdOptional
            returning(OK to "Game ASCII")
            returning(INTERNAL_SERVER_ERROR to "Everything is FUBAR")
            returning(BAD_REQUEST to "You sent something bad")
            returning(NOT_FOUND to "Game not found")
            returning(UNAUTHORIZED to "You can't view this game")
        } bindContract Method.GET to { request ->
            chessApp.getGame(
                Lens.gameId(request),
                Lens.playerIdOptional(request)
            )
                .let(Game::state)
                .run { Response(OK).bodyAsText(render()) }
        }

    private fun makeMove() =
        "/game" meta {
            summary = "Make Move"
            description = "Make a move"
            produces += TEXT_PLAIN
            queries += Lens.gameId
            queries += Lens.playerIdRequired
            queries += Lens.move
            returning(OK to "Game ASCII")
            returning(INTERNAL_SERVER_ERROR to "Everything is FUBAR")
            returning(BAD_REQUEST to "You sent something bad")
            returning(NOT_FOUND to "Game not found")
            returning(UNAUTHORIZED to "You can't view this game or it isn't your turn")
        } bindContract Method.POST to { request ->
            chessApp.makeMove(
                Lens.gameId(request),
                Lens.playerIdRequired(request),
                Lens.move(request)
            )
            Response(OK)
        }
}

