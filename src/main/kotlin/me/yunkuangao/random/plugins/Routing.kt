package me.yunkuangao.random.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import me.yunkuangao.random.persistence.categoryUrlMap
import me.yunkuangao.random.persistence.saveUrl

fun Application.configureRouting() {

    routing {
        get("/") {
            /*
            todo 增加页面
            包括一个分类列表
            一个url列表
            一个查询功能
            在分类列表中增加链接复制按钮
             */
        }

        post("/url/add") {
            val url = call.receive<Url>()
            saveUrl(url.url, url.category)
        }

        post("/url/delete") {
            // todo 添加删除url逻辑
        }

        post("/category/add") {
            // todo 添加增加分类逻辑
        }

        post("/category/delete") {
            // todo 添加删除分类逻辑
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

@Serializable
data class Url(val url: String, val category: String = "")