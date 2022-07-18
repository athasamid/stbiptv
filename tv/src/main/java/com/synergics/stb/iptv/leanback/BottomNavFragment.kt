package com.synergics.stb.iptv.leanback

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.google.gson.Gson
import com.synergics.stb.iptv.leanback.models.Movie
import com.synergics.stb.iptv.leanback.models.TVItems

class BottomNavFragment: BrowseSupportFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadRows()
        setupEventListeners()
    }

    fun loadRows(){
        val list = BaseApplication.database?.tvItemDao()?.all
        val cardPresenter = CardPresenter()
        val rowAdaper = ArrayObjectAdapter(ListRowPresenter())
        val listRowAdapter = ArrayObjectAdapter(cardPresenter)
        if (list != null){
            for (tv in list){
                listRowAdapter.add(tv)
            }
        }
        val header = HeaderItem(0L, "")
        rowAdaper.add(ListRow(header, listRowAdapter))

        adapter = rowAdaper
    }

    fun setupEventListeners(){
        setOnSearchClickedListener {
            Toast.makeText(activity, "Implement your own in-app search", Toast.LENGTH_LONG)
                .show()
        }
        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?, item: Any,
            rowViewHolder: RowPresenter.ViewHolder, row: Row
        ) {
            if (item is TVItems) {
                //Log.d(TAG, "Item: $item")
                if (itemViewHolder == null)
                    return

                (activity as VideoPlayerActivity).updatePlaying(item)
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
                /*mBackgroundUri = item.backgroundImageUrl
                startBackgroundTimer()*/
            }
        }
    }

}