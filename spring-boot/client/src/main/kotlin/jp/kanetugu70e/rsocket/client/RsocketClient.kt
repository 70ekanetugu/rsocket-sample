package jp.kanetugu70e.rsocket.client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RsocketClient

fun main(args: Array<String>) {
    runApplication<RsocketClient>(*args)
}
