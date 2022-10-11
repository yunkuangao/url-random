package me.yunkuangao.random.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import me.yunkuangao.random.persistence.*
import me.yunkuangao.random.utils.urlValid

@Serializable
data class Url(val url: String = "", val category: String = "")

@Serializable
data class CategoryUrl(val category: String, val urls: List<String>)

fun Application.configureRouting() {

    routing {

        singlePageApplication {
            // 并非react,但用自定义路径不起作用
            react("frontend/public")
        }

        get("/url/list/{category...}") {
            val category = call.parameters["category"]
            if (category?.isNotEmpty() == true) {
                call.respond(categoryUrlMap[category]?.map { Url(it, "") }?.toList() ?: listOf())
            } else {
                call.respond(categoryUrlMap.values.flatten())
            }
        }

        post("/url/add") {
            val url = call.receive<Url>()
            if (url.url.isEmpty() || urlExist(url.url, url.category)) {
                call.response.status(HttpStatusCode.BadRequest)
            } else if (urlValid(url.url)) {
                saveUrl(url.url, url.category)
                call.response.status(HttpStatusCode.OK)
            } else {
                call.respondText(text = "400: url is invalid", status = HttpStatusCode.BadRequest)
            }
        }

        post("/url/delete") {
            val url = call.receive<Url>()
            deleteUrl(url.url, url.category)
            call.response.status(HttpStatusCode.OK)
        }

        get("/category/list") {
            call.respond(categoryUrlMap.keys.map { Url("", it) }.toList())
        }

        post("/category/add") {
            val url = call.receive<Url>()
            if (url.category.isEmpty() || categoryExist(url.category)) {
                call.response.status(HttpStatusCode.BadRequest)
            } else {
                saveCategory(url.category)
                call.response.status(HttpStatusCode.OK)
            }
        }

        post("/category/delete") {
            val url = call.receive<Url>()
            deleteCategory(url.category)
            call.response.status(HttpStatusCode.OK)
        }

        get("/all") {
            call.respond(categoryUrlMap.map { CategoryUrl(it.key, it.value.toList()) })
        }

        get("/random/{category...}") {
            if (call.parameters["category"]?.isNotEmpty() == true) {
                val category = call.parameters["category"]
                if (categoryUrlMap.containsKey(category)) {
                    categoryUrlMap[category]?.let {
                        if (it.size > 0) call.respondRedirect(it.random()) else call.respondText(
                            text = "500: this category is empty",
                            status = HttpStatusCode.InternalServerError
                        )
                    }
                } else {
                    call.respondText(text = "404: category not found", status = HttpStatusCode.NotFound)
                }
            } else {
                categoryUrlMap.filter { it.value.size > 0 }
                    .let {
                        it[it.keys.random()]
                            .let { inner -> call.respondRedirect(inner?.random() ?: "") }
                    }
            }
        }
    }
}
