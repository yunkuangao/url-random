package me.yunkuangao.random.utils

import java.io.File

const val URL_FILE = "url.txt"

fun fileExist(urlFile: String) {
    val file = File(urlFile)
    if (!file.exists()) {
        file.createNewFile()
    }
}

fun readFile(urlFile: String = URL_FILE): List<String> {
    fileExist(urlFile)
    return File(urlFile).readLines()
}

fun attachFile(url: String, urlFile: String = URL_FILE) {
    fileExist(urlFile)
    File(urlFile).appendText(url + "\n")
}