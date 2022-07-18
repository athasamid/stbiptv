/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.synergics.stb.iptv.leanback

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.synergics.stb.iptv.leanback.etc.Const
import com.synergics.stb.iptv.leanback.models.M3UParser
import com.synergics.stb.iptv.leanback.models.Movie
import com.synergics.stb.iptv.leanback.models.TVItems
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.*


class MainFragment : BrowseSupportFragment() {
    private val mHandler = Handler()
    private var mDefaultBackground: Drawable? = null
    private var mMetrics: DisplayMetrics? = null
    private var mBackgroundTimer: Timer? = null
    private var mBackgroundUri: String? = null
    private var mBackgroundManager: BackgroundManager? = null

    private var et: EditText? = null
    private var path: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareBackgroundManager()
        setupUIElements()
        loads()
        setupEventListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != mBackgroundTimer) {
            mBackgroundTimer?.cancel()
        }
    }

    private fun loads() {
        if (!Const.firstRun()) {
            AlertDialog.Builder(requireContext(), R.style.Theme_AppCompat)
                .setCancelable(false)
                .setItems(arrayOf("Choose m3u File", "m3u from url", "code")){ p, i ->
                    when(i){
                        0 -> dialogLoadFromFile()
                        1 -> dialogLoadFromUrl()
                        2 -> dialogLoadFromCode()
                    }
                }
                .show()
        }
        loadRows()
    }

    private fun dialogLoadFromFile() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_load_from_file, null, false)
        et = view.findViewById<EditText>(R.id.tvInput)
        val btn = view.findViewById<Button>(R.id.btnSave)

        val dialog = AlertDialog.Builder(requireContext(), R.style.Theme_AppCompat)
            .setView(view)
            .setCancelable(false)
            .create()

        et?.setOnClickListener {
            val registerResult = registerForActivityResult(ActivityResultContracts.GetContent()){uri: Uri? ->
                path = uri?.path
                if (path != null) {
                    val file = File(path!!)
                    val filename = file.name
                    et?.setText(filename)
                }
            }
            registerResult.launch("*/*")


        }

        btn.setOnClickListener {
            if (path != null){
                val file = File(path!!)
                M3UParser().insertToDb(file, true)
                Const.firstRun(true)
                loadRows()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun dialogLoadFromUrl() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_load_from_url, null, false)
        et = view.findViewById(R.id.tvInput)
        val btn = view.findViewById<Button>(R.id.btnSave)
        val info = view.findViewById<TextView>(R.id.info)

        val dialog = AlertDialog.Builder(requireContext(), R.style.Theme_AppCompat)
            .setCancelable(false)
            .setView(view)
            .create()

        btn.setOnClickListener {
            download(et?.text.toString(), info, btn, dialog)

        }

        dialog.show()
    }

    fun download(url: String, info: TextView, btn: Button, dialog: AlertDialog){
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
        val randomString = (1..10).map { charset.random() }.joinToString("");
        val filename = "$randomString.m3u"
        val dirpath = context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.path
        info.text = "Downloading file"
        btn.isEnabled = false

        AndroidNetworking.download(url, dirpath, filename)
            .setPriority(Priority.MEDIUM)
            .setTag("Downloading..")
            .build()
            .setDownloadProgressListener { bytesDownloaded, totalBytes ->
                info.text = "Downloading "+(((bytesDownloaded*100)/totalBytes).toInt())+"%";
            }
            .startDownload(object: DownloadListener {
                override fun onDownloadComplete() {
                    val file = File(dirpath, filename)
                    M3UParser().insertToDb(file, true)
                    Const.firstRun(true)
                    dialog.dismiss()
                    loadRows()
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(requireContext(), anError?.message ?: "Something wrong happend", Toast.LENGTH_LONG).show()
                }

            })
    }

    private fun dialogLoadFromCode() {}

    private fun loadRows() {
        val list = BaseApplication.database?.tvCategoriesDao()?.all()
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CardPresenter()
        for (category in list ?: mutableListOf()) {
            val listRowAdapter = ArrayObjectAdapter(cardPresenter)
            category?.tv = BaseApplication.database?.tvItemDao()?.getByCategory(category?.id ?: -1)
            for (tv in category?.tv ?: mutableListOf()) {
                listRowAdapter.add(tv)
            }
            val header = HeaderItem(category?.id?.toLong() ?: 0L, category?.nama ?: "Undefined")
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }

        adapter = rowsAdapter
    }

    private fun prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(activity)
        mBackgroundManager?.attach(activity?.window)
        mDefaultBackground = ContextCompat.getDrawable(requireContext(), R.drawable.default_background)
        mMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(mMetrics)
    }

    private fun setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
        title = getString(R.string.browse_title) // Badge, when set, takes precedent
        // over title
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // set fastLane (or headers) background color
        brandColor = ContextCompat.getColor(
            requireContext(),
            R.color.fastlane_background
        )
        // set search icon color
        searchAffordanceColor = ContextCompat.getColor(
            requireContext(),
            R.color.search_opaque
        )
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            Toast.makeText(activity, "Implement your own in-app search", Toast.LENGTH_LONG)
                .show()
        }
        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }

    private fun updateBackground(uri: String?) {
        val width = mMetrics!!.widthPixels
        val height = mMetrics!!.heightPixels
        Glide.with(requireContext())
            .load(uri)
            .into(object : SimpleTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    mBackgroundManager?.drawable = resource
                }
            })
        mBackgroundTimer!!.cancel()
    }

    private fun startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer!!.cancel()
        }
        mBackgroundTimer = Timer()
        mBackgroundTimer?.schedule(UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY.toLong())
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?, item: Any,
            rowViewHolder: RowPresenter.ViewHolder, row: Row
        ) {
            if (item is TVItems) {
                Log.d(TAG, "Item: $item")
                if (itemViewHolder == null)
                    return

                val intent = Intent(activity, VideoPlayerActivity::class.java)
                intent.putExtra(DetailsActivity.CHANNEL, Gson().toJson(item))
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    (itemViewHolder?.view as ImageCardView).mainImageView,
                    DetailsActivity.SHARED_ELEMENT_NAME
                )
                    .toBundle()
                activity?.startActivity(intent, bundle)
            } else if (item is String) {
                if (item.contains(getString(R.string.error_fragment))) {
                    val intent = Intent(activity, BrowseErrorActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(activity, item, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder?,
            row: Row?
        ) {
            if (item is Movie) {
                mBackgroundUri = item.backgroundImageUrl
                startBackgroundTimer()
            }
        }
    }

    private inner class UpdateBackgroundTask : TimerTask() {
        override fun run() {
            mHandler.post { updateBackground(mBackgroundUri) }
        }
    }

    private inner class GridItemPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val view = TextView(parent.context)
            view.layoutParams = ViewGroup.LayoutParams(
                GRID_ITEM_WIDTH,
                GRID_ITEM_HEIGHT
            )
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.default_background)
            )
            view.setTextColor(Color.WHITE)
            view.gravity = Gravity.CENTER
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            (viewHolder.view as TextView).text = item as String
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
    }

    companion object {
        private const val TAG = "MainFragment"
        private const val BACKGROUND_UPDATE_DELAY = 300
        private const val GRID_ITEM_WIDTH = 200
        private const val GRID_ITEM_HEIGHT = 200
        private const val NUM_ROWS = 6
        private const val NUM_COLS = 15
    }

    class DownloadFileFromURL : AsyncTask<String, String, String> (){
        override fun onPreExecute() {

        }

        override fun doInBackground(vararg params: String?): String? {
            var path: String? = null
            var count = 0
            try {
                val url = URL(params[0])
                val connection = url.openConnection()

                connection.connect()

                val lenghtOfFile = connection.contentLength
                val input = BufferedInputStream(url.openStream(), 8192)

                val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
                val randomString = (1..10).map { charset.random() }.joinToString("");

                val output = FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/"+randomString+".m3u")

                val data = ByteArray(1024)

                var total: Long = 0

                while (input.read(data).also { count = it } != -1) {
                    total += count
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (total * 100 / lenghtOfFile) as Int)

                    // writing data to file
                    output.write(data, 0, count)
                }

                // flushing output
                output.flush()

                // closing streams
                output.close()
                input.close()

                path = Environment.getExternalStorageDirectory().toString()+"/"+randomString+".m3u"
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return path
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }
    }
}