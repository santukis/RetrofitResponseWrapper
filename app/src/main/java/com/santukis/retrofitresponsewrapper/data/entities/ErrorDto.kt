package com.santukis.retrofitresponsewrapper.data.entities


import com.google.gson.annotations.SerializedName

data class ErrorDto(
    @SerializedName("code")
    val code: String? = null,

    @SerializedName("message")
    val message: String? = null
)