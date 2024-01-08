package io.github.kamo.ktor.springboot

import io.github.kamo.ktor.springboot.modules.KtorModuleFun
import io.github.kamo.ktor.springboot.router.KtorRouterFun
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment

@AutoConfiguration
@EnableConfigurationProperties(KtorServerProperties::class)
@ConditionalOnClass(ApplicationEngine::class)
@Import(KtorServerStartStopLifecycle::class, KtorFunctionBeanPostProcessor::class)
class KtorServerAutoConfiguration(
    private val properties: KtorServerProperties,
    private val environment: Environment
) {

    @Bean
    fun engine(
        engineFactory: ApplicationEngineFactory<ApplicationEngine, out ApplicationEngine.Configuration>,
        environment: ApplicationEngineEnvironment
    ): ApplicationEngine {
        return embeddedServer(
            factory = engineFactory,
            environment = environment
        )
    }

    @Bean
    @ConditionalOnMissingBean
    fun engineFactory(): ApplicationEngineFactory<ApplicationEngine, out ApplicationEngine.Configuration> = Netty


    @Bean
    @ConditionalOnMissingBean
    fun defaultEnvironment(
        modules: List<KtorModuleFun>,
        connectors: List<EngineConnectorConfig>
    ): ApplicationEngineEnvironment = applicationEngineEnvironment {
        this.log = KtorSimpleLogger(environment.getProperty("spring.application.name") ?: "ktor.application")
        this.rootPath = properties.path
        this.modules.addAll(modules)
        this.connectors.addAll(connectors)
    }

    @Bean
    fun routeModules(
        routeFunList: List<KtorRouterFun>
    ): KtorModuleFun = {
        routing {
            routeFunList.forEach { it() }
        }
    }


    @Bean
    fun defaultEngineConnectorConfig(): EngineConnectorConfig {
        return EngineConnectorBuilder().apply {
            this.port = properties.port
            this.host = properties.host
        }
    }

}
