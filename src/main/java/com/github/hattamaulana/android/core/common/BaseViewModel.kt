package com.github.hattamaulana.android.core.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.hattamaulana.android.core.network.SendRequestException
import com.github.hattamaulana.android.core.state.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {
    private val queue = arrayListOf<String>()

    private val _state = MutableLiveData<ViewState>()
    private val _error = MutableLiveData<String?>()

    val state: LiveData<ViewState> = _state
    val error: LiveData<String?> = _error

    protected fun changeViewState(ViewState: ViewState) {
        _state.postValue(ViewState)
    }

    fun <T> Flow<T>.send(
        action: (suspend (value: T) -> Unit)? = null,
    ) = viewModelScope.launch(Dispatchers.IO) {
        onStart {
            remove()
            viewModelScope.launch(Dispatchers.Main) {
                changeViewState(ViewState.LOADING)
                _error.value = null
            }
        }.catch {
            viewModelScope.launch(Dispatchers.Main) {
                if (queue.isEmpty()) changeViewState(ViewState.ERROR)
            }

            when (it) {
                is SendRequestException -> viewModelScope.launch(Dispatchers.Main) {
                    _error.value = it.message
                }

                is Exception -> viewModelScope.launch(Dispatchers.Main) {
                    _error.value = it.message

                    Timber.e(it.message)
                    it.printStackTrace()
                }
            }
        }.collect {
            viewModelScope.launch(Dispatchers.Main) {
                if (queue.isEmpty()) changeViewState(ViewState.SUCCESS)

                remove()
                action?.invoke(it)
            }
        }
    }

    fun addQueue(vararg process: String) = queue.addAll(process)

    private fun remove() {
        val new = queue.filterIndexed { index, _ -> index > 0 }.toTypedArray()

        queue.clear()
        queue.addAll(new)
    }
}
