package routes

import io.ktor.server.routing.*
import io.rsocket.kotlin.RSocketRequestHandler
import io.rsocket.kotlin.ktor.server.rSocket
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

fun Route.rsocketRoute() = rSocket("rsocket") {
    println(config.setupPayload.data.readText())

    RSocketRequestHandler {
        requestResponse { request: Payload ->
            delay(1000)
            buildPayload {
                data("""{ "data": "Server Response" }""")
            }
        }

        fireAndForget { request: Payload ->
            println(request.data.readText())
            delay(10000)
        }

        requestStream { request: Payload ->
            println(request.data.readText())
            flow {
                repeat(10) {
                    emit (
                        buildPayload {
                            data("""{ "data": "Server stream response $it" }""")
                        }
                    )
                    delay(500)
                }
            }
        }
    }
}

