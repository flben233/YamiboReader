package org.shirakawatyu.yamibo.novel.ui.vm

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.shirakawatyu.yamibo.novel.bean.Favorite
import org.shirakawatyu.yamibo.novel.global.GlobalData
import org.shirakawatyu.yamibo.novel.global.YamiboRetrofit
import org.shirakawatyu.yamibo.novel.network.FavoriteApi
import org.shirakawatyu.yamibo.novel.ui.state.FavoriteState
import org.shirakawatyu.yamibo.novel.util.CookieUtil
import org.shirakawatyu.yamibo.novel.util.FavoriteUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.nio.charset.Charset

class FavoriteVM: ViewModel() {
    private val _uiState = MutableStateFlow(FavoriteState())
    val uiState = _uiState.asStateFlow()

    init {
        println("VM创建")
        FavoriteUtil.getFavorite {
            _uiState.value = FavoriteState(it)
        }
    }

    fun refreshList() {
        GlobalData.loading = true
        CookieUtil.getCookie {
            println(it)
            val favoriteApi = YamiboRetrofit.getInstance(it).create(FavoriteApi::class.java)
            favoriteApi.getFavoritePage().enqueue(object: Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val respHTML = response.body()?.string()
                    if (respHTML != null) {
                        val parse = Jsoup.parse(respHTML)
                        val favList = parse.getElementsByClass("sclist")
                        val objList = ArrayList<Favorite>()
                        favList.forEach { li ->
                            val title = li.text()
                            val url = li.child(1).attribute("href").value
                            println(url)
                            objList.add(Favorite(title, url))
                        }
                        FavoriteUtil.addFavorite(objList) {filteredList ->
                            _uiState.value = FavoriteState(filteredList)
                            GlobalData.loading = false
                        }
                    }
                    println(response.body()?.string())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    fun clickHandler(url: String, navController: NavController) {
        val urlEncoded = URLEncoder.encode(url, "utf-8")
        navController.navigate("ReaderPage/$urlEncoded")
    }
}