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
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberExtensionFunctions
import kotlin.reflect.full.extensionReceiverParameter

@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
class KtorExtensionBeanPostProcessor : BeanPostProcessor, BeanDefinitionRegistryPostProcessor {
    companion object {
        private const val MODULE_ADAPTER_BEAN_NAME: String = "extensionBeanPostProcessorModuleAdapter"
        private const val ROUTER_ADAPTER_BEAN_NAME: String = "extensionBeanPostProcessorRouterAdapter"
        private const val APPLICATION_TYPE_NAME = "io.ktor.server.application.Application"
        private const val ROUTING_TYPE_NAME = "io.ktor.server.routing.Routing"
        private val defaultFunNameMaps = mapOf(
            KtorModule::class to "install",
            KtorRouter::class to "register"
        )
    }

    private val moduleFunctions = mutableListOf<Pair<Any, KFunction<*>>>()
    private val routerFunctions = mutableListOf<Pair<Any, KFunction<*>>>()

    private val extensionAdapter = object : KtorModule, KtorRouter {
        override fun Application.install() {
            moduleFunctions.forEach { it.second.call(it.first, this) }
        }

        override fun Routing.register() {
            routerFunctions.forEach {
                it.second.call(it.first, this)
            }
        }
    }

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (beanName == MODULE_ADAPTER_BEAN_NAME || beanName == ROUTER_ADAPTER_BEAN_NAME) {
            return bean
        }
        val extFunctions = runCatching {
            bean::class.declaredMemberExtensionFunctions
        }.getOrNull() ?: return bean

        extFunctions.filter { it.parameters.size == 2 }
            .forEach {
                if (isExtensionFunBy(bean, it, APPLICATION_TYPE_NAME, KtorModule::class)) {
                    moduleFunctions.add(bean to it)
                } else if (isExtensionFunBy(bean, it, ROUTING_TYPE_NAME, KtorRouter::class)) {
                    routerFunctions.add(bean to it)
                }
            }
        return bean
    }

    private fun isExtensionFunBy(bean: Any, function: KFunction<*>, typeName: String, type: KClass<*>): Boolean =
        function.extensionReceiverParameter?.type?.toString() == typeName
                && (!bean.instanceOf(type) || defaultFunNameMaps[type] != function.name)

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        val extensionModuleAdapterBD = BeanDefinitionBuilder
            .rootBeanDefinition(KtorModule::class.java) { extensionAdapter }.beanDefinition
        val extensionRouterAdapterBD = BeanDefinitionBuilder
            .rootBeanDefinition(KtorRouter::class.java) { extensionAdapter }.beanDefinition
        registry.registerBeanDefinition(MODULE_ADAPTER_BEAN_NAME, extensionModuleAdapterBD)
        registry.registerBeanDefinition(ROUTER_ADAPTER_BEAN_NAME, extensionRouterAdapterBD)
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) = Unit

}
