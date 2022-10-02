package me.yunkuangao.random.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.stream.Collectors


const val URL_FILE = "url.txt"
const val CATEGORY_FILE = "category.txt"

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

fun attachFile(text: String, file: String = URL_FILE) {
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