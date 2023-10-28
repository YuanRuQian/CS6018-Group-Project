package cs6018.lydiayuan.plugins

import cs6018.lydiayuan.models.ErrorResponse
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(ErrorResponse("Invalid route"))
        }
    }
}