package org.shirakawatyu.yamibo.novel.ui.state

import org.shirakawatyu.yamibo.novel.bean.Favorite

data class FavoriteState (
    var favoriteList: List<Favorite> = listOf()
)