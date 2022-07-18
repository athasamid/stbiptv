package com.synergics.stb.iptv.leanback

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.synergics.stb.iptv.leanback.models.TVItems
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class VideoPlayerActivity : FragmentActivity() {
    lateinit var tvNumber: TextView
    lateinit var tvInfo: TextView
    lateinit var linearLayout: LinearLayout
    lateinit var linearLayoutChannel: LinearLayout
    lateinit var recyclerView: RecyclerView
    lateinit var vlcVideoPlayer: VLCVideoLayout

    var mLibVlc: LibVLC? = null
    var mMediaPlayer: MediaPlayer? = null
    var number: String? = ""
    var mHeight = 0
    var mWidth = 0
    var playing: TVItems? = null
    var keys: MutableMap<Int, String> = HashMap()

    val USE_TEXTURE_VIEW = false
    val ENABLE_SUBTITLES = false
    val KEY_FILE_PATH = "KEY_FILE_PATH"
    var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    val adapter = AdapterChannel()


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_video_player)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        mHeight = displayMetrics.heightPixels
        mWidth = displayMetrics.widthPixels
        linearLayoutChannel = findViewById(R.id.layoutListChannel)
        tvNumber = findViewById(R.id.nomor)
        tvInfo = findViewById(R.id.info)
        linearLayout = findViewById(R.id.layoutInfo)
        vlcVideoPlayer = findViewById(R.id.vlc_player)

        bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutChannel)

        bottomSheetBehavior?.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    bottomSheetBehavior?.peekHeight = 0
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })

        onNewIntent(intent)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, BottomNavFragment())
                .commitNow()
        }
    }


    fun loadPlayer() {
        if (playing == null) return
        linearLayout.visibility = View.VISIBLE
        tvNumber.text = playing?.id.toString();
        tvInfo.visibility = View.VISIBLE
        tvInfo.text = playing?.nama
        Handler().postDelayed({
            linearLayout.visibility = View.GONE
            tvNumber.text = "";
            tvInfo.visibility = View.GONE
            tvInfo.text = ""
        }, 2000)

        mLibVlc = LibVLC(this, ArrayList<String>().apply {
            add("--no-drop-late-frames")
            add("--no-skip-frames")
            add("--rtsp-tcp")
            add("-vvv")
        })
        mMediaPlayer = MediaPlayer(mLibVlc)
        mMediaPlayer?.attachViews(vlcVideoPlayer, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW)

        try {
            val uri = Uri.parse(playing?.url)
            Media(mLibVlc, uri).apply {
                setHWDecoderEnabled(true, false);
                addOption(":network-caching=150");
                addOption(":clock-jitter=0");
                addOption(":clock-synchro=0");
                mMediaPlayer?.media = this
            }.release()

            mMediaPlayer?.play()
        } catch (e: IOException) {

        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        //Log.e("onKeyDown", keyCode.toString())
        when (keyCode) {
            in 7..16 -> {
                number += keys[keyCode]
                linearLayout.visibility = View.VISIBLE
                tvNumber.text = number
                Handler().postDelayed({
                    if (number?.isEmpty() == false) loadNumber(number)
                    number = ""
                    tvNumber.text = number
                    linearLayout.visibility = View.GONE
                }, 3000)
                return true
            }
            KeyEvent.KEYCODE_CHANNEL_UP -> {
                Toast.makeText(this@VideoPlayerActivity, "KEYCODE_CHANNEL_UP", Toast.LENGTH_SHORT)
                    .show()
                //loadNumber(String.valueOf(playing.getId() + 1));
                // Todo Key Up
                return true
            }
            KeyEvent.KEYCODE_CHANNEL_DOWN -> {
                // Todo Key Down
                Toast.makeText(this@VideoPlayerActivity, "KEYCODE_CHANNEL_DOWN", Toast.LENGTH_SHORT)
                    .show()
                //loadNumber(String.valueOf(playing.getId() - 1));
                return true
            }
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> {
                Toast.makeText(this@VideoPlayerActivity, "KEYCODE_DPAD_UP_DOWN", Toast.LENGTH_SHORT)
                    .show()
                bottomSheetBehavior?.peekHeight = 300
                bottomSheetBehavior?.state = if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
            }
            /*KeyEvent.KEYCODE_DPAD_LEFT -> {

            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {

            }*/
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun loadNumber(num: String?) {
        val temp: TVItems? = BaseApplication.database?.tvItemDao()?.getById(num?.toInt() ?: -1)
        if (temp != null) {
            updatePlaying(temp)
        }
    }

    fun updatePlaying(play: TVItems){
        this.playing = play
        mMediaPlayer?.stop()
        mMediaPlayer?.detachViews()
        mMediaPlayer?.release()
        mLibVlc?.release()
        loadPlayer()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        playing = Gson().fromJson(
            intent.getStringExtra(DetailsActivity.Companion.CHANNEL),
            TVItems::class.java
        )
        loadPlayer()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onStop() {
        super.onStop()
        mMediaPlayer?.stop()
        mMediaPlayer?.detachViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer?.release()
        mLibVlc?.release()
    }

    companion object {
        private val TAG = VideoPlayerActivity::class.java.simpleName
    }

    init {
        keys[7] = "0"
        keys[8] = "1"
        keys[9] = "2"
        keys[10] = "3"
        keys[11] = "4"
        keys[12] = "5"
        keys[13] = "6"
        keys[14] = "7"
        keys[15] = "8"
        keys[16] = "9"
    }
}