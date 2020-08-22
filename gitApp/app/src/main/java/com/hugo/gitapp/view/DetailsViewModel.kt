package com.hugo.gitapp.view

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugo.gitapp.data.entities.ResponsePull
import com.hugo.gitapp.presentation.activity.DetailsActivity
import com.hugo.gitapp.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsViewModel(private val retrofitClient: RetrofitClient): ViewModel() {

    companion object {
        const val NOTIFICATION_GET_PR_FAILED = 1
        const val NOTIFICATION_GET_NEW_PR_FAILED = 2
        val pulls = MutableLiveData<MutableList<ResponsePull>>()
        val newPulls = MutableLiveData<MutableList<ResponsePull>>()
    }
    val notification = MutableLiveData<Int>()

    var page = 1
    var mIsLoading = false
    var mIsLastPage = false
    val pageSize = 30

    fun getPrs(
        creator: String,
        repository: String,
        detailsActivity: DetailsActivity
    ) {
        detailsActivity.progressLoading()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = retrofitClient.getService()
                    .getPulls(creator, repository,"1")
                Log.d("DetailsViewModel", Thread.currentThread().name)
                withContext(Dispatchers.Main) {
                    if (result.isSuccessful) {
                        Log.d("DetailsViewModel", Thread.currentThread().name)
                        pulls.value = result.body()
                    } else {
                        notification.value =
                            NOTIFICATION_GET_PR_FAILED
                        Log.e("DetailsViewModel", "get values failed: \n " +
                                "${result.errorBody()?.toString()}")
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    notification.value =
                        NOTIFICATION_GET_PR_FAILED
                }
                Log.e("DetailsViewModel", "get values failed: \n ${ex.message}")
            }
        }
    }

    fun loadingMore(creator: String, repository: String) {
        mIsLoading = true
        page++
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = retrofitClient.getService()
                    .getPulls(creator, repository, page.toString())
                Log.e("DetailsViewModel", Thread.currentThread().name)
                withContext(Dispatchers.Main) {
                    if (result.isSuccessful) {
                        Log.e("DetailsViewModel", Thread.currentThread().name)
                        newPulls.value = result.body()
                        mIsLoading = false
                    } else {
                        notification.value =
                            NOTIFICATION_GET_NEW_PR_FAILED
                        Log.e("DetailsViewModel", "get values failed: \n " +
                                "${result.errorBody()?.toString()}")
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    notification.value =
                        NOTIFICATION_GET_NEW_PR_FAILED
                }
                Log.e("DetailsViewModel", "get values failed: \n ${ex.message}")
            }
        }
    }
}