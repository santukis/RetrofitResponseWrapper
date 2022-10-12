package com.santukis.retrofitresponsewrapper.data.remote

import com.santukis.retrofitresponsewrapper.data.entities.ApodDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApodApi {

    @GET("planetary/apod")
    suspend fun loadApod(
        @Query("date") date: String?,
        @Query("api_key") apiKey: String? = "DEMO_KEY"
    ): ApodDto

    @GET("planetary/apod")
    suspend fun loadApod2(
        @Query("date") date: String?,
        @Query("api_key") apiKey: String? = "DEMO_KEY"
    ): ApodDto
}