import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*

fun main() {
    embeddedServer(Netty, 9090, module = Application::myApplicationModule).start(wait = true)
}

fun Application.myApplicationModule() {
    val shoppingList = mutableListOf(
        ShoppingListItem("Cucumbers 🥒", 1),
        ShoppingListItem("Tomatoes 🍅", 2),
        ShoppingListItem("Orange Juice 🍊", 3)
    )
    install(ContentNegotiation) { json() }
    install(CORS) {
        allowMethod(io.ktor.http.HttpMethod.Get)
        allowMethod(io.ktor.http.HttpMethod.Post)
        allowMethod(io.ktor.http.HttpMethod.Delete)
        anyHost()
    }
    install(Compression) {
        gzip()
    }
    routing {
        get("/") {
            call.respondText(
                this::class.java.classLoader.getResource("index.html")!!.readText(),
                ContentType.Text.Html
            )
        }
        static("/") {
            resources("")
        }
        route(ShoppingListItem.path) {
            get {
                call.respond(shoppingList)
            }
            post {
                shoppingList += call.receive<ShoppingListItem>()
                call.respond(HttpStatusCode.OK)
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                shoppingList.removeIf { it.id == id }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}