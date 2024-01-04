package io.github.ktor.springboot.plugins

import io.ktor.server.application.*


interface  KtorPlugin {

    fun Application.install()

}