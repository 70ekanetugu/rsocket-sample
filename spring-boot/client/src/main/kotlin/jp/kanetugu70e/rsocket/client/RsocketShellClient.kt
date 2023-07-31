package jp.kanetugu70e.rsocket.client

import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import java.util.*

private val LOGGER = LoggerFactory.getLogger(RsocketShellClient::class.java)
private val CLIENT = "Client"
private val REQUEST = "Request"
private val FIRE_AND_FORGET = "Fire-And-Forget"
private val STREAM = "Stream"
private val CLIENT_ID = UUID.randomUUID().toString()

@ShellComponent
class RsocketShellClient(
    private val builder: RSocketRequester.Builder,
    private val strategies: RSocketStrategies
) {
    private val rsocketRequester = builder.tcp("localhost", 7000)

    @PreDestroy
    @ShellMethod("close")
    fun rsocketClose() {
        rsocketRequester.rsocket().dispose()
    }

    @ShellMethod("request-response")
    fun requestResponse() {
        val response = rsocketRequester.route("request-response")
            .data("kanetugu-70eです")
            .retrieveMono(String::class.java)
            .block()
        LOGGER.info(response)
    }

    @ShellMethod("fire-forget")
    fun fireForget() {
        rsocketRequester.route("fire-forget")
            .data("beacon")
            .send()
            .block()
    }

    @ShellMethod("request-stream")
    fun requestStream() {
        val disposable = rsocketRequester.route("request-stream")
            .data("""
                |kanetugu=70eですううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |うううううううううううううううううううううううううううううううううううううううううううううううううううううううううううううう
                |!!
            """.trimMargin())
            .retrieveFlux(String::class.java)
            .subscribe { LOGGER.info("Response: $it") }
    }


}
