package me.yunkuangao.random.utils

import java.io.File
import java.net.URI
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.stream.Collectors


const val CATEGORY_FILE = "category.txt"

fun fileExist(urlFile: String) {
    val file = File(urlFile)
    if (!file.exists()) {
        file.createNewFile()
    }
}

fun readFile(urlFile: String): List<String> {
    fileExist(urlFile)
    return File(urlFile).readLines()
}

fun attachFile(text: String, file: String) {
    fileExist(file)
    File(file).appendText(text + "\n")
}

fun removeLine(lineContent: String, filename: String) {
    val file = File(filename)
    val out: List<String> = Files.lines(file.toPath())
        .filter { line -> !line.contains(lineContent) }
        .collect(Collectors.toList())
    Files.write(file.toPath(), out, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)
}

fun urlValid(url: String): Boolean {
    val uri: URI = try {
        URI(url)
    } catch (e: URISyntaxException) {
        e.printStackTrace()
        return false
    }

    if (uri.host == null) {
        return false
    }
    if (uri.scheme.equals(other = "http", ignoreCase = true) || uri.scheme.equals(other = "https", ignoreCase = true)) {
        return true
    }
    return false
}