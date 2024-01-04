package io.github.ktor.springboot.modules

import io.ktor.server.application.*


interface  KtorModule {

    fun Application.install()

}