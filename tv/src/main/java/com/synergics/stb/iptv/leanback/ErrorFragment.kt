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

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.ErrorFragment

/*
 * This class demonstrates how to extend ErrorFragment
 */
class ErrorFragment : ErrorFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        title = resources.getString(R.string.app_name)
    }

    fun setErrorContent() {
        imageDrawable = ContextCompat.getDrawable(activity, R.drawable.lb_ic_sad_cloud)
        message = resources.getString(R.string.error_fragment_message)
        setDefaultBackground(TRANSLUCENT)
        buttonText = resources.getString(R.string.dismiss_error)
        buttonClickListener = View.OnClickListener {
            fragmentManager.beginTransaction().remove(this@ErrorFragment).commit()
        }
    }

    companion object {
        private const val TAG = "ErrorFragment"
        private const val TRANSLUCENT = true
    }
}