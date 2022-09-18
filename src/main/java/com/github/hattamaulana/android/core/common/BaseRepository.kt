package com.github.hattamaulana.android.core.common

import android.content.Context
import com.github.hattamaulana.android.core.network.BaseResponse
import com.github.hattamaulana.android.core.network.SendRequestException
import com.github.hattamaulana.android.core.state.Resource
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import timber.log.Timber

abstract class BaseRepository(protected val context: Context) {

    protected abstract fun catchException(directHandling: Boolean)

    protected abstract suspend fun refreshToken()


    fun <T> getResponse(
        refreshTokenWhenExpired: Boolean = true,
        directHandling: Boolean = true,
        request: suspend () -> Response<T>,
    ): Flow<T> = flow {
        when (val result = sendRequest(refreshTokenWhenExpired, directHandling) { request() }) {
            is Resource.Error -> {
                if (result.isResponseError) {
                    throw SendRequestException(result.message)
                } else {
                    throw Exception(result.message)
                }
            }

            is Resource.Success -> emit(result.data)
        }
    }.flowOn(Dispatchers.IO)


    fun <T, R> getResponseWithMapper(
        request: suspend () -> Response<T>,
        mapper: (T) -> R,
        refreshTokenWhenExpired: Boolean = true,
        directHandling: Boolean = true,
    ): Flow<R> = flow {
        when (val result = sendRequest(refreshTokenWhenExpired, directHandling) { request() }) {
            is Resource.Error -> {
                if (result.isResponseError) {
                    throw SendRequestException(result.message)
                } else {
                    throw Exception(result.message)
                }
            }

            is Resource.Success -> emit(mapper(result.data))
        }
    }.flowOn(Dispatchers.IO)


    suspend fun <T> sendRequest(
        refreshTokenWhenExpired: Boolean = true,
        directHandling: Boolean = true,
        catchError: ((data: BaseResponse<*>) -> Resource<T>)? = null,
        call: suspend () -> Response<T>,
    ): Resource<T> {
        val response: Response<T>

        try {
            response = call.invoke()

            return when {
                response.isSuccessful -> {
                    val body = response.body()!!
                    Resource.Success(body)
                }

                response.code() == 401 && refreshTokenWhenExpired -> {
                    refreshToken()
                    Thread.sleep(1_000)
                    sendRequest(refreshTokenWhenExpired, directHandling, catchError, call)
                }

                else -> {
                    val errorBody = response.errorBody()
                    val data = Gson().fromJson(
                        errorBody?.string(),
                        BaseResponse::class.java
                    )

                    return if (catchError != null) {
                        catchError.invoke(data)
                    } else {
                        catchException(directHandling)
                        Resource.Error(
                            message = data.message,
                            isResponseError = true
                        )
                    }
                }
            }
        } catch (e: JsonSyntaxException) {
            catchException(directHandling)

            Timber.e(e.message)
            e.printStackTrace()

            return Resource.Error(
                message = "Terjadi kesalahan parsing data ketika error",
                isResponseError = false
            )
        } catch (e: Exception) {
            catchException(directHandling)

            Timber.e(e.message)
            e.printStackTrace()

            return Resource.Error(
                message = "Tidak ada Koneksi Internet",
                isResponseError = false
            )
        }
    }

    fun <T> forPrototyping(call: suspend () -> T) = flow {
        delay(3_000)
        emit(call())
    }.flowOn(Dispatchers.IO)
}