package org.shirakawatyu.yamibo.novel.network

import okhttp3.ResponseBody
import org.shirakawatyu.yamibo.novel.bean.Favorite
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface FavoriteApi {
    @GET("/home.php?mod=space&do=favorite&view=me&type=thread")
    fun getFavoritePage(): Call<ResponseBody>
}