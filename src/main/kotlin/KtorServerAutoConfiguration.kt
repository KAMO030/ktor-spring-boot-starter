package io.github.ktor.springboot

import io.github.ktor.springboot.plugins.KtorPlugin
import io.github.ktor.springboot.plugins.KtorRoutePlugin
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@AutoConfiguration
@EnableConfigurationProperties(KtorServerProperties::class)
@ConditionalOnClass(ApplicationEngine::class)
@Import(KtorRoutePlugin::class, KtorServerStartStopLifecycle::class)
class KtorServerAutoConfiguration(
    private val context: ApplicationContext,
    private val properties: KtorServerProperties
) {

    @Bean
    fun engine(engineFactory: ApplicationEngineFactory<*, *>): ApplicationEngine {
        return embeddedServer(
            factory = engineFactory,
            port = properties.port
        ) { context.getBeansOfType(KtorPlugin::class.java).values.forEach { it.apply { install() } } }
    }

    @Bean
    @ConditionalOnMissingBean
    fun engineFactory(): ApplicationEngineFactory<ApplicationEngine, out ApplicationEngine.Configuration> = Netty

}
