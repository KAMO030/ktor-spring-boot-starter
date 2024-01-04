package io.github.ktor.springboot.router

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

interface KtorRouter {

    fun Route.register()

}
typealias HandleFun = PipelineContext<Unit, ApplicationCall>
