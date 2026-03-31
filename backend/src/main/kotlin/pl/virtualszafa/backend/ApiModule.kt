package pl.virtualszafa.backend

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.routing.routing
import pl.virtualszafa.backend.routes.healthRoutes
import pl.virtualszafa.backend.routes.wardrobeRoutes

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        healthRoutes()
        wardrobeRoutes()
    }
}
