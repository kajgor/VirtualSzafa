package pl.virtualszafa.backend.routes

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import pl.virtualszafa.backend.model.WardrobeItem

fun Route.wardrobeRoutes() {
    get("/api/v1/items") {
        call.respond(
            listOf(
                WardrobeItem(id = "1", name = "White Shirt", category = "tops", color = "white", size = "M"),
                WardrobeItem(id = "2", name = "Blue Jeans", category = "bottoms", color = "blue", size = "32"),
            )
        )
    }
}
