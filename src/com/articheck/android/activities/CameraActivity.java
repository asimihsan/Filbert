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

package com.articheck.android.activities;

import java.io.File;
import java.util.Locale;

import com.articheck.android.R;
import com.articheck.android.R.layout;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class CameraActivity extends Activity
{
    final String HEADER_TAG = getClass().getName();
    private String filename;
    private String condition_report_id;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final String TAG = HEADER_TAG + "::onCreate";
        Log.d(TAG, String.format(Locale.US, "Entry. savedInstanceState: '%s'", savedInstanceState));
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        
        Bundle extras = this.getIntent().getExtras();
        assert(extras.containsKey("filename"));
        assert(extras.containsKey("condition_report_id"));
        
        filename = extras.getString("filename");
        Log.d(TAG, String.format(Locale.US, "filename is: '%s'", filename));
        
        condition_report_id = extras.getString("condition_report_id");
        Log.d(TAG, String.format(Locale.US, "condition_report_id is: '%s'", condition_report_id));
    } // protected void onCreate(Bundle savedInstanceState)
    
    public String getFilename()
    {
        return filename;
    } // public File getFile()
    
    public void returnToConditionReport()
    {
        final String TAG = HEADER_TAG + "::returnToConditionReport";
        Log.d(TAG, String.format(Locale.US, "Entry."));
        
        Log.d(TAG, "Finish activity");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("photograph_filename", filename);
        intent.putExtra("condition_report_id", condition_report_id);
        startActivity(intent);        
    } // public void returnToConditionReport()

} // public class CameraActivity
