package io.github.ktor.springboot.router

import io.ktor.server.routing.*

interface KtorRouter {

    fun Route.register()

}
