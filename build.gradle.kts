import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "io.github.kamo"
version = "1.0.0"

repositories {
    mavenCentral()
}
configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    all {
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
    }
}
dependencies {
    implementation(libs.springboot.starter)
    implementation(libs.springboot.autoconfigure)
    implementation(libs.ktor.server.netty)
    api(libs.ktor.server.core)
    annotationProcessor(libs.springboot.configurationprocessor)
    testImplementation(libs.springboot.starter.test)
}

tasks.test {
    useJUnitPlatform()
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
    }
}
kotlin {
    jvmToolchain(8)
}

buildscript {
    dependencies {
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
    }
}
