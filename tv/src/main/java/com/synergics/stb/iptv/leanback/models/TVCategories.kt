package com.synergics.stb.iptv.leanback.models

import androidx.room.*
import com.synergics.stb.iptv.leanback.BaseApplication

@Entity(tableName = "tv_categories")
class TVCategories {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    var nama: String? = null

    @Ignore
    var tv: List<TVItems>? = null

    constructor() {}

    @Ignore
    constructor(nama: String?) {
        this.nama = nama
    }

    @Ignore
    constructor(tv: List<TVItems>?) {
        this.tv = tv
    }

    @Ignore
    constructor(nama: String?, tv: List<TVItems>?) {
        this.nama = nama
        this.tv = tv
    }

    override fun toString(): String {
        return "id:$id, nama:$nama, tv=$tv"
    }
}