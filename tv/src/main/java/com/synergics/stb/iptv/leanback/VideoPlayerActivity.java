package com.synergics.stb.iptv.leanback;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pedro.vlc.VlcListener;
import com.synergics.stb.iptv.leanback.libraries.VlcVideoLibrary;
import com.synergics.stb.iptv.leanback.models.TVItems;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class VideoPlayerActivity extends AppCompatActivity implements VlcListener{
    private static final String TAG = VideoPlayerActivity.class.getSimpleName();

    // display surface
    private SurfaceView mSurface;
    private VlcVideoLibrary vlcVideoLibrary;


    TextView tvNumber, tvInfo;
    LinearLayout linearLayout;
    String number = "";
    int mHeight;
    int mWidth;

    TVItems playing;

    Map<Integer, String> keys = new HashMap<>();
    {
        keys.put(7,  "0");
        keys.put(8,  "1");
        keys.put(9,  "2");
        keys.put(10, "3");
        keys.put(11, "4");
        keys.put(12, "5");
        keys.put(13, "6");
        keys.put(14, "7");
        keys.put(15, "8");
        keys.put(16, "9");
    }

    private String[] options = new String[]{":fullscreen", "--aout=opensles", "--audio-time-stretch", "-vvv", "--http-reconnect", "--network-caching-"+10*1000};


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;

        mSurface = findViewById(R.id.videoView);

        tvNumber = findViewById(R.id.nomor);
        tvInfo = findViewById(R.id.info);
        linearLayout = findViewById(R.id.layoutInfo);

        vlcVideoLibrary = new VlcVideoLibrary(this, this, mSurface, mHeight, mWidth);
        vlcVideoLibrary.setOptions(Arrays.asList(options));

        onNewIntent(getIntent());
    }

    void loadPlayer(){
        if (playing == null)
            return;

        linearLayout.setVisibility(View.VISIBLE);
        //tvNumber.setText(playing.getId());
        tvInfo.setVisibility(View.VISIBLE);
        tvInfo.setText(playing.getNama());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                linearLayout.setVisibility(View.GONE);
                //tvNumber.setText("");
                tvInfo.setVisibility(View.GONE);
                tvInfo.setText("");
            }
        },2000);
        vlcVideoLibrary.play(playing.getUrl());

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode >= 7 && keyCode <= 16){
            number += keys.get(keyCode);
            linearLayout.setVisibility(View.VISIBLE);
            tvNumber.setText(number);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!number.isEmpty())
                        loadNumber(number);

                    number = "";
                    tvNumber.setText(number);
                    linearLayout.setVisibility(View.GONE);
                }
            }, 3000);
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_CHANNEL_UP){
            Toast.makeText(VideoPlayerActivity.this, "KEYCODE_CHANNEL_UP", Toast.LENGTH_SHORT).show();
            //loadNumber(String.valueOf(playing.getId() + 1));
            // Todo Key Up
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN){
            // Todo Key Down
            Toast.makeText(VideoPlayerActivity.this, "KEYCODE_CHANNEL_DOWN", Toast.LENGTH_SHORT).show();
            //loadNumber(String.valueOf(playing.getId() - 1));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void loadNumber(String num) {
        TVItems temp = BaseApplication.database.tvItemDao().getById(Integer.parseInt(num));
        if (temp != null){
            playing = temp;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        playing = new Gson().fromJson(intent.getStringExtra(DetailsActivity.CHANNEL), TVItems.class);

        loadPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //if (vlcVideoLibrary != null)
            //vlcVideoLibrary.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if (vlcVideoLibrary != null)
            //vlcVideoLibrary.stop();
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError() {

    }
}
