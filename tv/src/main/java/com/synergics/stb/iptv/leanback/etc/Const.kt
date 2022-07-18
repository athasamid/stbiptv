package com.synergics.stb.iptv.leanback.etc

import com.synergics.stb.iptv.leanback.BaseApplication
import com.synergics.stb.iptv.leanback.models.TVCategories

object Const {
    fun firstRun(firstrun: Boolean) {
        BaseApplication.Companion.sp!!.edit().putBoolean("first-run", firstrun).apply()
    }

    fun firstRun(): Boolean {
        return BaseApplication.Companion.sp!!.getBoolean("first-run", false)
    }

    fun search(list: List<TVCategories>, filter: String): TVCategories? {
        var output: TVCategories? = null
        for (tv in list) {
            if (tv.nama != null && tv.nama == filter) output = tv
        }
        return output
    }
}