
import app.RealChessApp
import audit.Auditing
import org.http4k.core.then
import org.http4k.server.Netty
import org.http4k.server.asServer
import storage.game.InMemoryGameSource
import web.ChessRoutes
import web.ErrorHandling

fun main() {
    val gameSource = InMemoryGameSource()
    val app = RealChessApp(gameSource)
    val routes = ChessRoutes(app)
    println("Starting Server")
    Auditing
        .then(ErrorHandling)
        .then(routes.handler)
        .asServer(Netty(9000))
        .start()
}