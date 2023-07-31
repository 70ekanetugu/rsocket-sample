import io.rsocket.kotlin.RSocketRequestHandler
import io.rsocket.kotlin.core.RSocketServer
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import io.rsocket.kotlin.transport.ktor.tcp.TcpServer
import io.rsocket.kotlin.transport.ktor.tcp.TcpServerTransport
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import java.util.concurrent.Executors


fun main() {
    val poolDispatcher = Executors.newWorkStealingPool(4).asCoroutineDispatcher()
    runBlocking {
        val transport = TcpServerTransport("localhost", 8080)
        val connector = RSocketServer {
            maxFragmentSize = 1024
        }
        val server: TcpServer = connector.bindIn(CoroutineScope(poolDispatcher), transport) {
            RSocketRequestHandler {
                requestResponse { request: Payload ->
                    println("requestResponse: ${request.data.readText()}")
                    // クライアントは5s以上待つ
                    delay(5000)
                    buildPayload {
                        data("""{ "data": "Server response" }""")
                    }
                }

                fireAndForget { request: Payload ->
                    println("fireAndForget: ${request.data.readText()}")
                    // クライアント側は5sも待たされず即処理終了
                    delay(5000)
                }

                requestStream { request: Payload ->
                    println("requestStream: ${request.data.readText()}")
                    flow {
                        for (i in 1..10) {
                            emit(buildPayload { data ( "$i" )})
                            delay(1000)
                        }
                    }
                }
            }
        }
        server.handlerJob.also {
            println("Server started")
        }.join()
    }
}
