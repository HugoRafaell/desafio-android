package com.hugo.gitapp.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugo.gitapp.data.entities.ResponseRepository
import com.hugo.gitapp.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val retrofitClient: RetrofitClient): ViewModel() {

    companion object {
        const val NOTIFICATION_GET_REPOSITORIES_FAILED = 1
        const val NOTIFICATION_GET_NEW_REPOSITORIES_FAILED = 2
        val repositories = MutableLiveData<ResponseRepository>()
        val newRepositories = MutableLiveData<ResponseRepository>()
    }
    val notification = MutableLiveData<Int>()

    var page = 1
    var mIsLoading = false
    var mIsLastPage = false
    val pageSize = 30

    fun getRepositories() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = retrofitClient.getService()
                    .getRepositories("1")
                Log.d("MainViewModel", Thread.currentThread().name)
                withContext(Dispatchers.Main) {
                    if (result.isSuccessful) {
                        Log.d("MainViewModel", Thread.currentThread().name)
                        repositories.value = result.body()
                    } else {
                        notification.value = NOTIFICATION_GET_REPOSITORIES_FAILED
                        Log.e("MainViewModel", "get values failed: \n " +
                                "${result.errorBody()?.toString()}")
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    notification.value = NOTIFICATION_GET_REPOSITORIES_FAILED
                }
                Log.e("MainViewModel", "get values failed: \n ${ex.message}")
            }
        }
    }

    fun loadingMore() {
        mIsLoading = true
        page++
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = retrofitClient.getService()
                    .getRepositories(page.toString())
                Log.e("MainViewModel", Thread.currentThread().name)
                withContext(Dispatchers.Main) {
                    if (result.isSuccessful) {
                        Log.e("MainViewModel", Thread.currentThread().name)
                        newRepositories.value = result.body()
                        mIsLoading = false
                    } else {
                        notification.value = NOTIFICATION_GET_NEW_REPOSITORIES_FAILED
                        Log.e("MainViewModel", "get values failed: \n " +
                                "${result.errorBody()?.toString()}")
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    notification.value = NOTIFICATION_GET_NEW_REPOSITORIES_FAILED
                }
                Log.e("MainViewModel", "get values failed: \n ${ex.message}")
            }
        }
    }

}