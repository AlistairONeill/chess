package web

import app.GameNotFoundException
import app.InvalidMoveException
import app.UnauthorizedException
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNAUTHORIZED

object ErrorHandling: Filter {
    override fun invoke(next: HttpHandler) = { request: Request ->
            try {
                next(request)
            }
            catch (e: UnauthorizedException) {
                Response(UNAUTHORIZED).bodyAsText(e.message)
            }
            catch (e: GameNotFoundException) {
                Response(NOT_FOUND).bodyAsText(e.message)
            }
            catch (e: InvalidMoveException) {
                Response(BAD_REQUEST).bodyAsText(e.message)
            }
            catch (e: Exception) {
                Response(INTERNAL_SERVER_ERROR).bodyAsText("Ruh-roh")
            }
        }
}