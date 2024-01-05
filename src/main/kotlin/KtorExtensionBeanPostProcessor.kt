package io.github.ktor.springboot

import io.github.ktor.springboot.modules.KtorModule
import io.github.ktor.springboot.router.KtorRouter
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.context.annotation.Role
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberExtensionFunctions
import kotlin.reflect.full.extensionReceiverParameter

@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
class KtorExtensionBeanPostProcessor : BeanPostProcessor, BeanDefinitionRegistryPostProcessor {
    companion object {
        private const val BEAN_NAME: String = "extensionBeanPostProcessorModule"
        private const val APPLICATION_TYPE_NAME = "io.ktor.server.application.Application"
        private const val ROUTING_TYPE_NAME = "io.ktor.server.routing.Routing"
    }

    private val moduleFunctions = mutableListOf<Pair<Any, KFunction<*>>>()
    private val routingFunctions = mutableListOf<Pair<Any, KFunction<*>>>()


    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (beanName == BEAN_NAME) {
            return bean
        }
        val extFunctions = runCatching {
            bean::class.declaredMemberExtensionFunctions
        }.getOrNull() ?: return bean

        extFunctions.forEach {
            if (it.extensionReceiverParameter?.type?.toString() == APPLICATION_TYPE_NAME && !bean.instanceOf(KtorModule::class)) {
                moduleFunctions.add(bean to it)
            }else if (it.extensionReceiverParameter?.type?.toString() == ROUTING_TYPE_NAME && !bean.instanceOf(KtorRouter::class)) {
                routingFunctions.add(bean to it)
            }
        }
        return bean
    }

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        val beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(KtorModule::class.java) {
            object : KtorModule {
                override fun Application.install() {
                    moduleFunctions.forEach { it.second.call(it.first, this) }
                    routing {
                        routingFunctions.forEach {
                            it.second.call(it.first, this)
                        }
                    }
                }
            }
        }.beanDefinition
        registry.registerBeanDefinition(BEAN_NAME, beanDefinition)
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) = Unit
}
