package com.github.hattamaulana.android.core.state

sealed class Resource<out T> {
    class Success<T>(val data: T) : Resource<T>()
    class Error(val message: String?, val isResponseError: Boolean = false) : Resource<Nothing>()
}
