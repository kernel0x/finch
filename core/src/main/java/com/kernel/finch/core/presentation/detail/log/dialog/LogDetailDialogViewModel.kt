package com.kernel.finch.core.presentation.detail.log.dialog

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kernel.finch.FinchCore
import com.kernel.finch.core.util.extension.createAndShareFile
import kotlinx.coroutines.launch

internal class LogDetailDialogViewModel : ViewModel() {

    private val _isShareButtonEnabled = MutableLiveData(true)
    val isShareButtonEnabled: LiveData<Boolean> = _isShareButtonEnabled

    fun shareLogs(activity: Activity?, text: CharSequence, timestamp: Long?) {
        if (_isShareButtonEnabled.value == true) {
            viewModelScope.launch {
                _isShareButtonEnabled.postValue(false)
                activity?.createAndShareFile(
                    "${
                        FinchCore.implementation.configuration.logFileNameFormatter(
                            timestamp ?: 0L
                        )
                    }.txt", text.toString()
                )
                _isShareButtonEnabled.postValue(true)
            }
        }
    }
}
