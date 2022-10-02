package me.yunkuangao.random.persistence

import me.yunkuangao.random.utils.*
import java.util.*

var urlMap: MutableMap<String, String> = readFile().associateBy { UUID.fromString(it).toString() }.toMutableMap()

var categoryUrlMap: MutableMap<String, MutableList<String>> = readFile(CATEGORY_FILE)
    .associateWith { readFile("$it.txt").toMutableList() }
    .toMutableMap()
    .also {
        it["default"] = readFile().toMutableList()
    }

fun saveCategory(category: String) {
    if (!categoryUrlMap.containsKey(category)) {
        categoryUrlMap[category] = mutableListOf()
        attachFile(category, CATEGORY_FILE)
    }
}

fun deleteCategory(category: String) {
    if (!categoryUrlMap.containsKey(category)) {
        categoryUrlMap[category] = mutableListOf()
        removeLine(category, CATEGORY_FILE)
    }
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