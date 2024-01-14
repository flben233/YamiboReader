package org.shirakawatyu.yamibo.novel.network


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface PassageApi {
    @GET
    fun getPassage(@Url url: String, @Query("page") page: Int): Call<ResponseBody>
}