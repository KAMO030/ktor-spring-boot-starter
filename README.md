## ktor-spring-boot-starter

### 使用

* application.yml
    ```yaml
    ktor:
      server:
        path: "/kamo"
        port: 8008
        host: "127.0.0.1"
    ```
* routing
    1. KtorRouter
   ```kotlin
    @Controller
    // or @Component
    class TestController : KtorRouter {
        override fun Routing.register() {
            get("/a") { a() }
            get("/b") { b() }
        }

        suspend fun PipelineContext<Unit, ApplicationCall>.a() {
            call.respondText { "a" }
        }

        suspend fun PipelineContext<Unit, ApplicationCall>.b() {
            call.respondText { "b" }
        }
    }
   ```
    2. KtorRouterFun (KtorRouterFun = Routing.() -> Unit)
   ```kotlin
    @Component
    class TestRouting {
        @Bean
        fun c(): KtorRouterFun = {
            get("/c") { call.respondText { "c" } }
        }

        @Bean
        fun d(): KtorRouterFun = {
            get("/d") { call.respondText { "d" } }
        }
    }
   ```
    3. RoutingExtensionFun
   ```kotlin
    @Component
    class TestRouting { 
        fun Routing.e() {
            get("/e"){ call.respondText { "e" } }
        }
    }
   ```

* module
  1. KtorModule
   ```kotlin
    @Component
    class TestModule : KtorModule { 
        override fun Application.install() {
            // ...
        }
    }
   ```
  2. KtorModuleFun (KtorModuleFun = Application.() -> Unit)
   ```kotlin
    @Component
    class TestModule {
        @Bean
        fun module(): KtorModuleFun = {
            // ...
        }
    }
   ```
  3. ApplicationExtensionFun
   ```kotlin
    @Component
    class TestModule { 
        fun Application.module() {
            // ...
        }
    }
   ```