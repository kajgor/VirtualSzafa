package pl.virtualszafa.backend.routes

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import pl.virtualszafa.backend.repository.WardrobeRepository

fun Route.wardrobeRoutes(repository: WardrobeRepository) {
    get("/api/v1/items") {
        call.respond(repository.getAll())
    }
}
