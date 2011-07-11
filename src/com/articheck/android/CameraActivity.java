/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.articheck.android;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class CameraActivity extends Activity
{
    final String HEADER_TAG = getClass().getName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final String TAG = HEADER_TAG + "::onCreate";
        Log.d(TAG, String.format(Locale.US, "Entry. savedInstanceState: '%s'", savedInstanceState));
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
    } // protected void onCreate(Bundle savedInstanceState)

} // public class CameraActivity
