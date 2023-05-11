import kotlinx.browser.document
import react.create
import react.dom.client.createRoot

fun main() {
//    document.getElementById("root")?.innerHTML = "Hello, Kotlin/JS!"
    val container = document.getElementById("root") ?: error("Couldn't find container!")
    createRoot(container).render(App.create());
}
