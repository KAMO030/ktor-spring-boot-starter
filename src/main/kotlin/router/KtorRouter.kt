package io.github.kamo.ktor.springboot.router

import io.ktor.server.routing.*

interface KtorRouter {

    fun Routing.register()

}

typealias KtorRouterFun = Routing.() -> Unit
