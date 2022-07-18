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

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.synergics.stb.iptv.leanback.models.Movie

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {
    override fun onBindDescription(viewHolder: ViewHolder, item: Any) {
        val movie = item as Movie
        if (movie != null) {
            viewHolder.title.text = movie.title
            viewHolder.subtitle.text = movie.studio
            viewHolder.body.text = movie.description
        }
    }
}