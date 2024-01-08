package io.github.kamo.ktor.springboot.modules

import io.ktor.server.application.*

class KtorFunctionAdapterModule : (Application) -> Unit {
    private val modules: MutableList<KtorModuleFun> = mutableListOf()

    fun addModule(function: KtorModuleFun) {
        modules.add(function)
    }

    fun addModule(module: KtorModule) {
        modules.add { module.apply { install() } }
    }

    override fun invoke(application: Application) {
        modules.forEach { it(application) }
    }
}