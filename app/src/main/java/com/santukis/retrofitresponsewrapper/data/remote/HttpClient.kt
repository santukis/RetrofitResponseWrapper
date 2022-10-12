package com.santukis.retrofitresponsewrapper.data.remote

import com.google.gson.GsonBuilder
import com.santukis.retrofitresponsewrapper.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

class HttpClient(host: String) {

    private val client: OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)
            .writeTimeout(30L, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE
            })
            .build()

    private val moshiConverter = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val gsonConverter = GsonBuilder()
        .create()

    val retrofit: Retrofit =
        Retrofit.Builder()
            .client(client)
            .baseUrl(host)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshiConverter))
            .addConverterFactory(GsonConverterFactory.create(gsonConverter))
            .build()


    val apodApi: ApodApi = retrofit.create(ApodApi::class.java)
}

inline fun <reified Error, Success> Retrofit.wrap(block: () -> Success): Result<Success> =
    try {
        Result.success(block())

    } catch (exception: Exception) {
        if (exception is HttpException) {
            exception.response()?.errorBody()?.takeIf { it.contentLength() > 0 }?.let { errorBody ->
                converterFactories().firstNotNullOfOrNull { converterFactory ->
                    val type = typeOf<Error>()

                    converterFactory.responseBodyConverter(
                        type.javaType,
                        type.annotations.toTypedArray(),
                        this
                    )?.convert(errorBody)?.let {
                        Result.failure(CustomException(it))
                    }

                } ?: Result.failure(exception)

            } ?: Result.failure(exception)

        } else {
            Result.failure(exception)
        }
    }

data class CustomException(val error: Any): Exception()