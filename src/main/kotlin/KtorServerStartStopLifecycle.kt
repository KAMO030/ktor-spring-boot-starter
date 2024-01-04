package io.github.ktor.springboot

import io.ktor.server.engine.*
import org.springframework.context.SmartLifecycle

class KtorServerStartStopLifecycle(private val engine: ApplicationEngine) : SmartLifecycle {
    private var running = false
    override fun start() {
        engine.start(true)
        running = true
    }

    override fun stop() {
        engine.stop()
    }

    override fun isRunning(): Boolean = running
}