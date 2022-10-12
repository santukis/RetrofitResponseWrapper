package com.santukis.retrofitresponsewrapper.data.datasources

import com.santukis.retrofitresponsewrapper.data.entities.ApodDto
import java.util.*

interface ApodDataSource {

    suspend fun loadApod(date: Date): Result<ApodDto>

    suspend fun loadApod2(date: Date): Result<ApodDto>
}