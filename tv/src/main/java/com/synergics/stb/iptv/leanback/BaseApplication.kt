package com.synergics.stb.iptv.leanback

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import com.synergics.stb.iptv.leanback.models.db.MyDatabase

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        sp = getSharedPreferences("pengaturan", MODE_PRIVATE)
        database =
            Room.databaseBuilder(this, MyDatabase::class.java, DBNAME).allowMainThreadQueries()
                .fallbackToDestructiveMigration().build()
        instance = this
    }

    companion object {
        const val DBNAME = "stb-iptv"
        var instance: BaseApplication? = null
        var sp: SharedPreferences? = null
        var database: MyDatabase? = null
    }
}