package pl.virtualszafa.backend.routes

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.healthRoutes() {
    get("/health") {
        call.respond(mapOf("status" to "ok", "service" to "virtualszafa-backend"))
    }
}
