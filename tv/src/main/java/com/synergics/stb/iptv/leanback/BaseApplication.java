package com.synergics.stb.iptv.leanback;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;

import com.synergics.stb.iptv.leanback.models.db.MyDatabase;

public class BaseApplication extends Application {
    public static final String DBNAME = "stb-iptv";
    public static BaseApplication instance;
    public static SharedPreferences sp;
    public static MyDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("pengaturan", MODE_PRIVATE);
        database = Room.databaseBuilder(this, MyDatabase.class, DBNAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        instance = this;
    }
}
