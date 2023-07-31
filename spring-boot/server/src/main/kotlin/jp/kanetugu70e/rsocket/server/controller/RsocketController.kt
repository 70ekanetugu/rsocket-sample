package jp.kanetugu70e.rsocket.server.controller

import io.rsocket.Payload
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration


private val LOGGER = LoggerFactory.getLogger(RsocketController::class.java)
private val CLIENTS = mutableListOf<RSocketRequester>()

@Controller
class RsocketController {
    @PreDestroy
    fun shutdown() = CLIENTS.forEach { it.rsocket().dispose() }

    /**
     * クライアント毎のコネクションを管理するための処理
     * コネクションを張っている間、後述{@code @MessageMapping}はそのコネクションにおける個々のリクエストを処理できる。(多重通信)
     */
    @ConnectMapping("sample-client")
    fun handle(requester: RSocketRequester, client: Payload) {
        requester.rsocket()
            .onClose()
            .doFirst { CLIENTS.add(requester) }
            .doOnError { LOGGER.error(it.message, it) }
            .doFinally { CLIENTS.remove(requester) }
            .subscribe()

        requester.route("echo")
            .data("")
            .retrieveFlux(String.javaClass)
            .doOnNext { LOGGER.info("client: $client")}
            .subscribe()
    }

    /**
     * Request-Responseモデル
     */
    @MessageMapping("request-response")
    fun requestResponse(message: String = "default") = Mono.just("echo: $message")

    /**
     * Fire-Fotgetモデル
     */
    @MessageMapping("fire-forget")
    fun fireForget(message: String = "default") = LOGGER.info("fire-forget: ${message}")

    /**
     * Request-Streamモデル
     */
    @MessageMapping("request-stream")
    fun requestStream(message: String): Flux<String> {
        Flux.interval(Duration.ofSeconds(2)).map {  }
        return Flux.fromStream {
            message
                .map { c -> "request-stream: ${c.code.toString(16)}: ${c.uppercase()}" }
                .stream()
        }
    }

    @MessageMapping("channel")
    fun channel(messages: Flux<String>): Flux<String> {
        return messages
            .doOnNext { LOGGER.info("channel: $it") }
            .doOnCancel { LOGGER.error("channel cancelled") }
            .map { "channel: ${it.uppercase()}" }
    }
}
