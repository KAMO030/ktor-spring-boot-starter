package io.github.ktor.springboot.test

import io.github.ktor.springboot.router.KtorRouter
import io.github.ktor.springboot.router.KtorRouterFun
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller

fun main() {
    runApplication<TestConfig>()
}

@SpringBootApplication
class TestConfig {

    @Bean
    fun c(): KtorRouterFun = {
        get("/c") {
            call.respondText { "c" }
        }
    }

    @Bean
    fun d(): KtorRouterFun = {
        get("/d") {
            call.respondText { "d" }
        }
    }

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