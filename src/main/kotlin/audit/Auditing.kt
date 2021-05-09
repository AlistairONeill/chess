package audit

import org.http4k.core.*

object Auditing: Filter {
    override fun invoke(next: HttpHandler) = { request: Request ->
        println("Processing Request:")
        println("\tOn: ${request.uri}")
        println("\tMethod: ${request.method}")
        println("\tBody: ${request.bodyString()}")
        next(request)
    }
}