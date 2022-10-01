package me.yunkuangao.random.persistence

import me.yunkuangao.random.utils.readFile
import java.util.*

var urlMap: MutableMap<String, String> = readFile().associateBy { UUID.fromString(it).toString() }.toMutableMap()

var categoryUrlMap: MutableMap<String, MutableList<String>> = readFile("category.txt")
    .associateWith { readFile("$it.txt").toMutableList() }
    .toMutableMap()
    .also {
        "default" to readFile().toMutableList()
    }