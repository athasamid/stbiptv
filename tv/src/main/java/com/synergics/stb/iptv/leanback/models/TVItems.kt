package com.synergics.stb.iptv.leanback.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tv_items")
class TVItems {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    var nama: String? = null
    var icon: String? = null
    var group: String? = null
    var duration: String? = null
    var url: String? = null
    var categoryId = 0

    override fun toString(): String {
        return "id:$id,nama:$nama,icon:$icon,group:$group,duration:$duration,url:$url,categoryId:$categoryId"
    }
}