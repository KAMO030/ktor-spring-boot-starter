package io.github.ktor.springboot

import org.springframework.boot.context.properties.ConfigurationProperties
@ConfigurationProperties(prefix = "ktor.server", ignoreUnknownFields = true)
data class KtorServerProperties(
    var port: Int = 8080,
    var host: String = "0.0.0.0",
    var path: String = "/"
)