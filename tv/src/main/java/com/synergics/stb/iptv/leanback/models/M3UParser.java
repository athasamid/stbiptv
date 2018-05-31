package com.synergics.stb.iptv.leanback.models;

import android.content.Context;
import android.util.Log;

import com.synergics.stb.iptv.leanback.BaseApplication;
import com.synergics.stb.iptv.leanback.etc.Const;
import com.synergics.stb.iptv.leanback.models.db.MyDatabase;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class M3UParser {
    private static final String EXT_M3U = "#EXTM3U";
    private static final String EXT_INF = "#EXTINF:";
    private static final String EXT_PLAYLIST_NAME = "#PLAYLIST";
    private static final String EXT_LOGO = "tvg-logo";
    private static final String EXT_GROUP = "group-title";
    private static final String EXT_URL = "http://";

    public static String convertStreamToString(java.io.InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }

    public static List<TVItems> parseRaw(Context context, int raw) throws FileNotFoundException {
        List<TVItems> temp = new ArrayList<>();
        List<TVCategories> kategori = new ArrayList<>();
        InputStream inputStream = context.getResources().openRawResource(raw);
        String stream = convertStreamToString(inputStream);
        String linesArray[] = stream.split(EXT_INF);
        for (int i = 0; i < linesArray.length; i++) {
            String currLine = linesArray[i];
            //Log.e("CurLine", currLine);
            final TVItems playlistItem = new TVItems();
            String[] dataArray = currLine.split(",");
            if (dataArray[0].contains(EXT_LOGO)) {
                String duration = dataArray[0].substring(0, dataArray[0].indexOf(EXT_LOGO)).replace(":", "").replace("\n", "");
                String icon = dataArray[0].contains(EXT_GROUP) ? dataArray[0].substring(dataArray[0].indexOf(EXT_LOGO)+EXT_LOGO.length(), dataArray[0].indexOf(EXT_GROUP)).replace("=", "").replace("\n","").replace("\"", "") : dataArray[0].substring(dataArray[0].indexOf(EXT_LOGO) + EXT_LOGO.length()).replace("=", "").replace("\"", "").replace("\n", "");
                playlistItem.setDuration(duration);
                playlistItem.setIcon(icon);
            } else {
                String duration = dataArray[0].replace(":", "").replace("\n", "");
                playlistItem.setDuration(duration);
                playlistItem.setIcon("");
            }

            if (dataArray[0].contains(EXT_GROUP)){
                String group = dataArray[0].substring(dataArray[0].indexOf(EXT_GROUP)+ EXT_GROUP.length(), dataArray[0].length()).replace("=", "").replace("\"", "").replace("\n", "");
                playlistItem.setGroup(group.isEmpty() ? "Tidak Diketahui": group);
            } else {
                playlistItem.setGroup("Tidak Diketahui");
            }

            try {
                String url = dataArray[1].substring(dataArray[1].indexOf(EXT_URL)).replace("\n", "").replace("\r", "");
                String name = dataArray[1].substring(0, dataArray[1].indexOf(EXT_URL)).replace("\n", "");
                playlistItem.setNama(name);
                //Log.e("nama", name);
                playlistItem.setUrl(url);
            } catch (Exception fdfd) {
                Log.e("Google", "Error: " + fdfd.fillInStackTrace());
            }

            TVCategories search = BaseApplication.database.tvCategoriesDao().getByNama(playlistItem.getGroup());

            if (search == null){
                long id = BaseApplication.database.tvCategoriesDao().insert(new TVCategories(playlistItem.getGroup()));
                playlistItem.setCategoryId((int) id);
            } else {
                playlistItem.setCategoryId(search.getId());
            }

            if (playlistItem.getUrl() != null && !playlistItem.getUrl().isEmpty()){
                temp.add(playlistItem);
            }
        }

        if (temp.size() > 0)
            BaseApplication.database.tvItemDao().insert(temp);

        return temp;
    }
}
