package io.github.kamo.ktor.springboot

import io.github.kamo.ktor.springboot.modules.KtorFunctionAdapterModule
import io.github.kamo.ktor.springboot.modules.KtorModule
import io.github.kamo.ktor.springboot.router.KtorFunctionAdapterRouter
import io.github.kamo.ktor.springboot.router.KtorRouter
import io.ktor.util.reflect.*
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.core.ResolvableType
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberExtensionFunctions
import kotlin.reflect.full.extensionReceiverParameter

class KtorFunctionBeanPostProcessor : BeanPostProcessor, BeanDefinitionRegistryPostProcessor {
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

    private val moduleAdapter: KtorFunctionAdapterModule = KtorFunctionAdapterModule()
    private val routerAdapter: KtorFunctionAdapterRouter = KtorFunctionAdapterRouter()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (beanName == MODULE_ADAPTER_BEAN_NAME || beanName == ROUTER_ADAPTER_BEAN_NAME) {
            return bean
        }
        if (bean is KtorModule) {
            moduleAdapter.addModule(bean)
        } else if (bean is KtorRouter) {
            routerAdapter.addRouter(bean)
        }
        val extFunctions = runCatching {
            bean::class.declaredMemberExtensionFunctions
        }.getOrNull() ?: return bean

        extFunctions.filter { it.parameters.size == 2 }
            .forEach {
                if (isExtensionFunBy(bean, it, APPLICATION_TYPE_NAME, KtorModule::class)) {
                    moduleAdapter.addModule { it.call(bean, this) }
                } else if (isExtensionFunBy(bean, it, ROUTING_TYPE_NAME, KtorRouter::class)) {
                    routerAdapter.addRouter { it.call(bean, this) }
                }
            }
        return bean
    }

    private fun isExtensionFunBy(bean: Any, function: KFunction<*>, typeName: String, type: KClass<*>): Boolean =
        function.extensionReceiverParameter?.type?.toString() == typeName
                && (!bean.instanceOf(type) || defaultFunNameMaps[type] != function.name)

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {

        val moduleAdapterType = ResolvableType.forInstance(moduleAdapter)
        val extensionModuleAdapterBD = BeanDefinitionBuilder
            .rootBeanDefinition(moduleAdapterType) { moduleAdapter }.beanDefinition
        val routerAdapterType = ResolvableType.forInstance(routerAdapter)
        val extensionRouterAdapterBD = BeanDefinitionBuilder
            .rootBeanDefinition(routerAdapterType) {
                routerAdapter
            }.beanDefinition
        registry.registerBeanDefinition(MODULE_ADAPTER_BEAN_NAME, extensionModuleAdapterBD)
        registry.registerBeanDefinition(ROUTER_ADAPTER_BEAN_NAME, extensionRouterAdapterBD)
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) = Unit

}
