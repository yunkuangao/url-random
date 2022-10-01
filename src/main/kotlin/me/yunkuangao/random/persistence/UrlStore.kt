package me.yunkuangao.random.persistence

import me.yunkuangao.random.utils.URL_FILE
import me.yunkuangao.random.utils.attachFile
import me.yunkuangao.random.utils.readFile
import java.util.*

var urlMap: MutableMap<String, String> = readFile().associateBy { UUID.fromString(it).toString() }.toMutableMap()

var categoryUrlMap: MutableMap<String, MutableList<String>> = readFile("category.txt")
    .associateWith { readFile("$it.txt").toMutableList() }
    .toMutableMap()
    .also {
        "default" to readFile().toMutableList()
    }

fun saveUrl(url: String, category: String = "") {
    //  todo 添加url验证
    

    //添加到默认
    categoryUrlMap["default"]?.add(url)
    attachFile(url, URL_FILE)

    //添加到类型文件
    if (category.isNotEmpty()) {
        categoryUrlMap[category]?.add(url)
        attachFile(url, "$category.txt")
    }
}