package me.yunkuangao.random.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.yunkuangao.random.persistence.categoryUrlMap

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/random/{category}") {
            if (call.parameters["category"]?.isNotEmpty() == true) {
                val category = call.parameters["category"]
                if (categoryUrlMap.containsKey(category)) {
                    categoryUrlMap[category]?.let { call.respondRedirect(it.random()) }
                } else {
                    call.respondText(text = "404: category not found", status = HttpStatusCode.NotFound)
                }
            } else {
                categoryUrlMap["default"]?.let { call.respondRedirect(it.random()) }
            }
        }
    }
}
