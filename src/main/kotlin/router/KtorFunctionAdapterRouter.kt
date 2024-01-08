package io.github.kamo.ktor.springboot.router

import io.ktor.server.routing.*

class KtorFunctionAdapterRouter : (Routing) -> Unit {
    private val routers: MutableList<KtorRouterFun> = mutableListOf()

    fun addRouter(function: KtorRouterFun) {
        routers.add(function)
    }

    fun addRouter(router: KtorRouter) {
        routers.add { router.apply { register() } }
    }

    override fun invoke(router: Routing) {
        routers.forEach { it(router) }
    }
}