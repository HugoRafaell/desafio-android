package com.hugo.gitapp.data.remote

import com.hugo.gitapp.data.entities.ResponsePull
import com.hugo.gitapp.data.entities.ResponseRepository
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.GET
import retrofit2.http.Query

interface APIRequest {

    @GET("search/repositories?q=language:Java&sort=stars")
    suspend fun getRepositories(@Query("page") page: String): Response<ResponseRepository>

    @GET("repos/{creator}/{repository}/pulls?state=all")
    suspend fun getPulls(@Path("creator") creator: String,
                         @Path("repository") repository: String,
                         @Query("page") page: String): Response<MutableList<ResponsePull>>
}