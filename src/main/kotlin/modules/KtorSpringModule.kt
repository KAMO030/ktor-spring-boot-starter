package io.github.ktor.springboot.modules

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

abstract class KtorSpringModule : KtorModule, ApplicationContextAware {

    protected lateinit var context: ApplicationContext


    override fun setApplicationContext(context: ApplicationContext) {
        this.context = context
    }

}
