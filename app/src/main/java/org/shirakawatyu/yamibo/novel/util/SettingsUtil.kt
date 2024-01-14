package org.shirakawatyu.yamibo.novel.util

import androidx.datastore.preferences.core.stringPreferencesKey
import com.alibaba.fastjson2.JSON
import org.shirakawatyu.yamibo.novel.bean.ReaderSettings

class SettingsUtil {
    companion object {
        private val key = stringPreferencesKey("settings")

        fun saveSettings(settings: ReaderSettings) {
            DataStoreUtil.addData(JSON.toJSONString(settings), key)
        }

        fun getSettings(callback: (settings: ReaderSettings) -> Unit) {
            DataStoreUtil.getData(key, callback = {
                callback(JSON.parseObject(it, ReaderSettings::class.java))
            })
        }
    }
}