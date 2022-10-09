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
import java.io.File

@Serializable
data class Url(val url: String = "", val category: String = "")

@Serializable
data class CategoryUrl(val category: String, val urls: List<String>)

fun Application.configureRouting() {

    routing {

        static("/") {
            /*
            todo 增加页面
            包括一个分类列表
            一个url列表
            一个查询功能
            在分类列表中增加链接复制按钮
             */
            staticRootFolder = File("frontend/public")
            file("build/bundle.js")
            file("build/bundle.css")
            file("build/bundle.js.map")
            file("global.css")
            file("index.html")
            file("favicon.png")
            default("index.html")
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
            if (urlValid(url.url)) {
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
            saveCategory(url.category)
            call.response.status(HttpStatusCode.OK)
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
