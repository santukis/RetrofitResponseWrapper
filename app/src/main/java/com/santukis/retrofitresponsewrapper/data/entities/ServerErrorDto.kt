package com.santukis.retrofitresponsewrapper.data.entities


import com.google.gson.annotations.SerializedName

data class ServerErrorDto(
    @SerializedName("error")
    val error: ErrorDto? = null
)