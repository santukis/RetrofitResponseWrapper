package com.santukis.retrofitresponsewrapper.data.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class ApodDto(
    @Json(name = "title")
    val title: String? = "",

    @Json(name = "explanation")
    val description: String? = "",

    @Json(name = "media_type")
    val mediaType: String? = "",

    @Json(name = "url")
    val url: String? = "",

    @Json(name = "copyright")
    val copyright: String? = ""
)