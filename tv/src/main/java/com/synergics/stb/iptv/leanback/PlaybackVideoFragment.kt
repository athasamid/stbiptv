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

import android.net.Uri
import android.os.Bundle
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import com.google.gson.Gson
import com.synergics.stb.iptv.leanback.models.TVItems

/**
 * Handles video playback with media controls.
 */
class PlaybackVideoFragment : VideoSupportFragment() {
    private var mTransportControlGlue: PlaybackTransportControlGlue<MediaPlayerAdapter>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val movie = Gson().fromJson(
            activity?.intent?.getStringExtra(DetailsActivity.CHANNEL),
            TVItems::class.java
        )
        val glueHost = VideoSupportFragmentGlueHost(this@PlaybackVideoFragment)
        val playerAdapter = MediaPlayerAdapter(activity)
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)
        mTransportControlGlue = PlaybackTransportControlGlue(activity, playerAdapter)
        mTransportControlGlue?.host = glueHost
        mTransportControlGlue?.title = movie.nama
        mTransportControlGlue?.subtitle = movie.group
        mTransportControlGlue?.playWhenPrepared()
        playerAdapter.setDataSource(Uri.parse(movie.url))
    }

    override fun onPause() {
        super.onPause()
        if (mTransportControlGlue != null) {
            mTransportControlGlue?.pause()
        }
    }
}