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

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import java.util.*

/*
 * Details activity class that loads LeanbackDetailsFragment class
 */
class DetailsActivity : FragmentActivity() {
    private var number: String? = ""
    var keys: MutableMap<Int, String> = HashMap()

    /**
     * Called when the activity is first created.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode >= 7 && keyCode <= 16) {
            number += keys[keyCode]
            Toast.makeText(this, number, Toast.LENGTH_SHORT).show()
            Handler().postDelayed({ //loadNumber(number);
                number = ""

                (supportFragmentManager.findFragmentById(R.id.details_fragment) as VideoDetailsFragment).updateMovie()
            }, 3000)
            return true
        } else if (keyCode == KeyEvent.KEYCODE_CHANNEL_UP) {
            Toast.makeText(this, "KEYCODE_CHANNEL_UP", Toast.LENGTH_SHORT).show()
            // Todo Key Up
            return true
        } else if (keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN) {
            // Todo Key Down
            Toast.makeText(this, "KEYCODE_CHANNEL_DOWN", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        const val SHARED_ELEMENT_NAME = "hero"
        const val CHANNEL = "channel"
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