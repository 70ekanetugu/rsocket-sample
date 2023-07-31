import io.ktor.utils.io.core.*
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.core.RSocketConnector
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import io.rsocket.kotlin.transport.ktor.tcp.TcpClientTransport
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

fun main(): Unit = runBlocking {
    val transient = TcpClientTransport("localhost", 8080)
    val connector = RSocketConnector {
        this.maxFragmentSize = 1024
    }

    val rsocket: RSocket = connector.connect(transient)
    val response1 = async {
        rsocket.requestResponse(buildPayload {
            data("""{ "data": "reuest response" }""")
        })
    }
    val response2 = async {
        rsocket.fireAndForget(buildPayload {
            data("""{ "data": "fire and forget" }""")
        })
    }
    val response3 = async {
        val flow = rsocket.requestStream(buildPayload {
            data("""{ "data": "request stream" }""""")
        })
        flow.onEach {
            println(it.data.readText())
        }
    }

    launch {
        println(response1.await().data.readText())
    }
    launch {
        response2.await().run { println("complete fire and forget") }
    }
    launch {
        response3.await()
            .flowOn(Dispatchers.IO)
            .collect { payload ->
                print(payload.data.readText())
            }
    }
}
