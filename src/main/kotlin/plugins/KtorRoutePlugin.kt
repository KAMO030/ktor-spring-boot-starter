package io.github.ktor.springboot.plugins

import io.github.ktor.springboot.KtorServerProperties
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.springframework.beans.factory.getBean
import io.github.ktor.springboot.router.KtorRouter

class KtorRoutePlugin : KtorSpringPlugin() {

    private fun Route.registerRoutes() {
        context.getBeansOfType(KtorRouter::class.java).values.forEach {
            it.apply { register() }
        }
    }

    override fun Application.install() {
        routing {
            val properties = context.getBean<KtorServerProperties>()
            route(properties.path) {
                registerRoutes()
            }
        }
    }

}