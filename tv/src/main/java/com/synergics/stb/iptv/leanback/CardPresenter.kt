/*
 * Copyright (C) 2014 The Android Open Source Project
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

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.synergics.stb.iptv.leanback.etc.TextDrawable
import com.synergics.stb.iptv.leanback.models.TVItems

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
class CardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        Log.d(TAG, "onCreateViewHolder")
        sDefaultBackgroundColor = ContextCompat.getColor(parent.context, R.color.default_background)
        sSelectedBackgroundColor =
            ContextCompat.getColor(parent.context, R.color.selected_background)

        val cardView: ImageCardView = object : ImageCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }
        //cardView.width = Cardv
        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
        updateCardBackgroundColor(cardView, false)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val tv = item as TVItems
        val cardView = viewHolder.view as ImageCardView
        if (tv == null) return
        Log.d(TAG, "onBindViewHolder")
        cardView.titleText = tv.nama
        cardView.contentText = tv.group
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
        if (tv.icon != null && tv.icon?.isEmpty() == true) {
            Glide.with(viewHolder.view.context)
                .load(tv.icon)
                .into(cardView.mainImageView)
        } else {
            cardView.mainImageView.setImageDrawable(TextDrawable(tv.nama ?: tv.id.toString()))
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        Log.d(TAG, "onUnbindViewHolder")
        val cardView = viewHolder.view as ImageCardView
        // Remove references to images so that the garbage collector can free up memory
        cardView.badgeImage = null
        cardView.mainImage = null
    }

    companion object {
        private const val TAG = "CardPresenter"
        private const val CARD_WIDTH = 313
        private const val CARD_HEIGHT = 176
        private var sSelectedBackgroundColor = 0
        private var sDefaultBackgroundColor = 0
        private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
            val color = if (selected) sSelectedBackgroundColor else sDefaultBackgroundColor
            // Both background colors should be set because the view's background is temporarily visible
            // during animations.
            view.setBackgroundColor(color)
            view.findViewById<View>(R.id.info_field).setBackgroundColor(color)
        }
    }
}