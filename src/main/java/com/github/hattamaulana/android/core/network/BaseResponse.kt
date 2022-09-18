package com.github.hattamaulana.android.core.network

import com.google.gson.annotations.SerializedName

class BaseResponse<T>(
    @SerializedName("data")
    val data: T,

    @SerializedName("message")
    val message: String,

    @SerializedName("status_code")
    val statusCode: Int
)
