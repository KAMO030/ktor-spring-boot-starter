package io.github.ktor.springboot.test

import io.github.ktor.springboot.router.KtorRouter
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller

fun main() {
    runApplication<TestConfig>()
}

@SpringBootApplication
class TestConfig {

}

@Controller
class TestController : KtorRouter {
    override fun Route.register() {
        get("/a") { a() }
        get("/b") { b() }

    }

    suspend fun PipelineContext<Unit, ApplicationCall>.a() {
        call.respondText { "a" }
    }

    suspend fun PipelineContext<Unit, ApplicationCall>.b() {
        call.respondText { "b" }
    }
}