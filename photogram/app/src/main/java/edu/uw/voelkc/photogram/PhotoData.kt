package edu.uw.voelkc.photogram


data class PhotoData(
    val imgURL: String ="",
    val title: String = "",
    val userUID: String = "",
    val likes: MutableMap<String, Boolean> = mutableMapOf()
)