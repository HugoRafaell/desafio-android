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

    @GET("repos/{criador}/{repositorio}/pulls")
    suspend fun getPulls(@Path("criador") criador: String,
                              @Path("repositorio") repositorio: String): Response<List<ResponsePull>>
}