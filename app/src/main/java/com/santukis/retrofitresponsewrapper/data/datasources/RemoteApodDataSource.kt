package com.santukis.retrofitresponsewrapper.data.datasources

import com.santukis.retrofitresponsewrapper.data.entities.ApodDto
import com.santukis.retrofitresponsewrapper.data.entities.ServerErrorDto
import com.santukis.retrofitresponsewrapper.data.remote.HttpClient
import com.santukis.retrofitresponsewrapper.data.remote.wrap
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RemoteApodDataSource(private val client: HttpClient): ApodDataSource {

    companion object {
        private const val DATE_FORMAT = "yyyy-MM-dd"
    }

    override suspend fun loadApod(date: Date): Result<ApodDto> =
        client.retrofit.wrap<ServerErrorDto, ApodDto> {
            client.apodApi.loadApod(fromDateToString(date))
        }

    override suspend fun loadApod2(date: Date): Result<ApodDto> =
        client.retrofit.wrap<List<ServerErrorDto>, ApodDto> {
            client.apodApi.loadApod2(fromDateToString(date))
        }

    private fun fromDateToString(date: Date?): String? {
        val desiredDate = date ?: Date()
        return createFormatter(DATE_FORMAT).format(desiredDate)
    }

    private fun createFormatter(pattern: String): SimpleDateFormat {
        return SimpleDateFormat(pattern, Locale.getDefault())
    }
}