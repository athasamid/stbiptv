package com.synergics.stb.iptv.leanback.models.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.synergics.stb.iptv.leanback.models.TVCategories
import com.synergics.stb.iptv.leanback.models.TVItems

@Database(entities = [TVCategories::class, TVItems::class], version = 4, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract fun tvItemDao(): TVItemDao
    abstract fun tvCategoriesDao(): TVCategoriesDao
}