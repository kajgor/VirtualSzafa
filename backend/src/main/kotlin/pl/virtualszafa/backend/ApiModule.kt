package pl.virtualszafa.backend

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import pl.virtualszafa.backend.repository.InMemoryWardrobeRepository
import pl.virtualszafa.backend.routes.healthRoutes
import pl.virtualszafa.backend.routes.wardrobeRoutes

fun Application.module() {
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }

    val repository = InMemoryWardrobeRepository()

    routing {
        healthRoutes()
        wardrobeRoutes(repository)
    }
}
