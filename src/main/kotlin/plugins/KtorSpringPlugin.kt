package io.github.ktor.springboot.plugins

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

abstract class KtorSpringPlugin : KtorPlugin, ApplicationContextAware {

    protected lateinit var context: ApplicationContext


    override fun setApplicationContext(context: ApplicationContext) {
        this.context = context
    }

}
