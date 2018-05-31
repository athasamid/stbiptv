package com.synergics.stb.iptv.leanback.etc;

import com.synergics.stb.iptv.leanback.BaseApplication;
import com.synergics.stb.iptv.leanback.models.TVCategories;

import java.util.List;

public class Const {
    public static void firstRun(boolean firstrun){
        BaseApplication.sp.edit().putBoolean("first-run", firstrun).apply();
    }

    public static boolean firstRun(){
        return BaseApplication.sp.getBoolean("first-run", false);
    }

    public static TVCategories search(List<TVCategories> list, String filter){
        TVCategories output = null;
        for (TVCategories tv: list) {
            if (tv.getNama() != null && tv.getNama().equals(filter))
                output = tv;
        }

        return output;
    }
}
