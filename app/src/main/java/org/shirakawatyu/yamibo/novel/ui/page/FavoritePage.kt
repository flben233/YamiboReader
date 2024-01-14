package org.shirakawatyu.yamibo.novel.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.shirakawatyu.yamibo.novel.item.FavoriteItem
import org.shirakawatyu.yamibo.novel.ui.vm.FavoriteVM
import org.shirakawatyu.yamibo.novel.ui.widget.TopBar


@Composable
fun FavoritePage(
    favoriteVM: FavoriteVM = viewModel(),
    navController: NavController
) {
    val uiState by favoriteVM.uiState.collectAsState()

    Column {
        TopBar(title = "收藏") {
            Button(onClick = { favoriteVM.refreshList() }) {
                Icon(Icons.Filled.Refresh, "")
            }
        }
        LazyColumn(Modifier.padding(0.dp, 3.dp)) {
            uiState.favoriteList.forEach {
                item {
                    FavoriteItem(it.title, it.lastView, it.lastPage) {
                        favoriteVM.clickHandler(it.url, navController)
                    }
                }
            }
        }
    }
}