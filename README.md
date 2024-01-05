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
    class TestComponent {
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
    class TestComponent { 
        fun Routing.e() {
            get("/e"){ call.respondText { "e" } }
        }
    }
   ```
  > KtorRouter 和 RoutingExtensionFun 不能混用 （暂时）