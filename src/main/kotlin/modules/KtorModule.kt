package io.github.kamo.ktor.springboot.modules

import io.ktor.server.application.*


interface  KtorModule {

    fun Application.install()

}

typealias KtorModuleFun = Application.() -> Unit