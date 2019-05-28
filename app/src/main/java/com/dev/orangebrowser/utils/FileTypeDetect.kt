package com.dev.orangebrowser.utils

import java.util.*

object FileTypeDetect {
    private val IMAGE_SUFFIXES = HashSet<String>()
    private val VIDEO_SUFFIXES = HashSet<String>()

    init {
        IMAGE_SUFFIXES.add("jpg")
        IMAGE_SUFFIXES.add("webp")
        IMAGE_SUFFIXES.add("png")
        IMAGE_SUFFIXES.add("gif")
        IMAGE_SUFFIXES.add("jpeg")
        IMAGE_SUFFIXES.add("bmp")
        IMAGE_SUFFIXES.add("svg")

        VIDEO_SUFFIXES.add("mp4")
        VIDEO_SUFFIXES.add("mkv")
        VIDEO_SUFFIXES.add("webm")
        VIDEO_SUFFIXES.add("avi")
        VIDEO_SUFFIXES.add("mov")
        VIDEO_SUFFIXES.add("flv")
        VIDEO_SUFFIXES.add("mpeg")
        VIDEO_SUFFIXES.add("mpg")
    }

    fun isImage(path: String): Boolean {
        val suffix = path.subSequence(path.lastIndexOf(".")+1, path.length).toString().toLowerCase()
        return IMAGE_SUFFIXES.contains(suffix)
    }
    fun isVideo(path: String): Boolean {
        val suffix = path.subSequence(path.lastIndexOf(".")+1, path.length).toString().toLowerCase()
        return VIDEO_SUFFIXES.contains(suffix)
    }
}
