package pl.virtualszafa.backend

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun startServer() {
    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module,
    ).start(wait = true)
}
