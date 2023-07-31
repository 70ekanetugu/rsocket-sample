import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.rsocket.kotlin.ktor.server.RSocketSupport
import routes.httpRouter
import routes.rsocketRoute

fun main() {
    embeddedServer(Netty, port = 9000) {
        install(WebSockets)
        install(RSocketSupport) {
            server {
                maxFragmentSize = 1024
            }
        }
        routing {
            httpRouter()
            rsocketRoute()
        }
    }.start(true)
}
