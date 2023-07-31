import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.websocket.*
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketRequestHandler
import io.rsocket.kotlin.core.WellKnownMimeType
import io.rsocket.kotlin.keepalive.KeepAlive
import io.rsocket.kotlin.ktor.client.RSocketSupport
import io.rsocket.kotlin.ktor.client.rSocket
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.PayloadMimeType
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class KtorClientTest {

    @Test
    fun rsocketTest() {
        val client = HttpClient(Java) {
            install(WebSockets)
            install(RSocketSupport) {
                engine {
                    threadsCount = 4
                    pipelining = true
                }

                connector {
                    maxFragmentSize = 1024
                    connectionConfig {
                        keepAlive = KeepAlive(
                            interval = 30.seconds,
                            maxLifetime = 2.minutes
                        )

                        setupPayload {
                            buildPayload {
                                data("""{ "data": "setup" }""")
                            }
                        }

                        payloadMimeType = PayloadMimeType(
                            data = WellKnownMimeType.ApplicationJson,
                            metadata = WellKnownMimeType.MessageRSocketCompositeMetadata
                        )
                    }

                    acceptor {
                        RSocketRequestHandler {
                            requestResponse { it }
                        }
                    }
                }
            }
        }
        println("テスト開始==================================")
        runBlocking {
            val rsocket: RSocket = client.rSocket(path = "rsocket", port = 9000)
            val response = async {
                rsocket.requestResponse(buildPayload { data("""{ "data": "Hello request response" }""") })
            }
            rsocket.fireAndForget(buildPayload { data("""{ "data": "Hello fire and forget" }""") })
            launch {
                println(response.await().data.readText())
            }

            val stream: Flow<Payload> = rsocket.requestStream(
                buildPayload {
                    data("""{ "data": "Hello request stream" }""")
                }
            )
            stream.take(5).collect { payload: Payload ->
                println(payload.data.readText())
            }
        }
        println("テスト終了==================================")
    }
}

