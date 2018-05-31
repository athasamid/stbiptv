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

package com.synergics.stb.iptv.leanback;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/*
 * Details activity class that loads LeanbackDetailsFragment class
 */
public class DetailsActivity extends Activity {
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String CHANNEL = "channel";

    private String number = "";

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
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode >= 7 && keyCode <= 16){
            number += keys.get(keyCode);
            Toast.makeText(this, number, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //loadNumber(number);
                    number = "";
                }
            }, 3000);
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_CHANNEL_UP){
            Toast.makeText(this, "KEYCODE_CHANNEL_UP", Toast.LENGTH_SHORT).show();
            // Todo Key Up
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN){
            // Todo Key Down
            Toast.makeText(this, "KEYCODE_CHANNEL_DOWN", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
