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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import android.app.Fragment;
import android.app.Activity;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class CameraFragment
extends Fragment
implements OnLongClickListener, Camera.PictureCallback, Camera.AutoFocusCallback
{
    final String HEADER_TAG = getClass().getName();
    final static String FRAGMENT_TAG = "fragment_camera";
    
    public static enum Request { TAKE_PICTURE };
    public static final ImmutableBiMap<Request, Integer> RequestMap = new ImmutableBiMap.Builder<Request, Integer>()
                          .put(Request.TAKE_PICTURE, 0)
                          .build();
    
    public static enum Result { PICTURE_SAVED };
    public static final ImmutableBiMap<Result, Integer> ResultMap = new ImmutableBiMap.Builder<Result, Integer>()
                          .put(Result.PICTURE_SAVED, 0)
                          .build();    
    
    private Preview mPreview;
    Camera mCamera;
    int mNumberOfCameras;
    int mCameraCurrentlyLocked;

    // The first rear facing camera
    int mDefaultCameraId;
    
    CameraActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        final String TAG = HEADER_TAG + "::onCreate";
        Log.d(TAG, "Entry.");                

        activity = (CameraActivity) this.getActivity();
        
        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        mPreview = new Preview(activity);
        mPreview.setLongClickable(true);
        mPreview.setOnLongClickListener(this);

        // Find the total number of cameras available
        mNumberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                mDefaultCameraId = i;
            }
        }
        setHasOptionsMenu(mNumberOfCameras > 1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Add an up arrow to the "home" button, indicating that the button will go "up"
        // one activity in the app's Activity heirarchy.
        // Calls to getActionBar() aren't guaranteed to return the ActionBar when called
        // from within the Fragment's onCreate method, because the Window's decor hasn't been
        // initialized yet.  Either call for the ActionBar reference in Activity.onCreate()
        // (after the setContentView(...) call), or in the Fragment's onActivityCreated method.
        ActionBar actionBar = activity.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return mPreview;
    }

    @Override
    public void onResume() {
        super.onResume();
        
        final String TAG = HEADER_TAG + "::onResume";
        Log.d(TAG, "Entry.");        

        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open(mDefaultCameraId);
        mCameraCurrentlyLocked = mDefaultCameraId;
        mPreview.setCamera(mCamera);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        
        final String TAG = HEADER_TAG + "::onPause";
        Log.d(TAG, "Entry.");

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null)
        {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        Log.d(TAG, "Exit.");
    } // public void onPause()

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mNumberOfCameras > 1) {
            // Inflate our menu which can gather user input for switching camera
            inflater.inflate(R.menu.camera_menu, menu);
        } else {
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.switch_cam:
            // Release this camera -> mCameraCurrentlyLocked
            if (mCamera != null) {
                mCamera.stopPreview();
                mPreview.setCamera(null);
                mCamera.release();
                mCamera = null;
            }

            // Acquire the next camera and request Preview to reconfigure
            // parameters.
            mCamera = Camera
                    .open((mCameraCurrentlyLocked + 1) % mNumberOfCameras);
            mCameraCurrentlyLocked = (mCameraCurrentlyLocked + 1)
                    % mNumberOfCameras;
            mPreview.switchCamera(mCamera);

            // Start the preview
            mCamera.startPreview();
            return true;
        case android.R.id.home:
            Intent intent = new Intent(this.getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public boolean onLongClick(View v)
    {
        final String TAG = HEADER_TAG + "::onLongClick";
        Log.d(TAG, String.format(Locale.US, "Entry. view: '%s'", v));
        boolean return_value = false;
        
        mCamera.autoFocus(this);        
        return_value = true;
        
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));
        return return_value;
    } // public boolean onLongClick(View v)
    
    public void onAutoFocus(boolean success, Camera camera)
    {
        final String TAG = getClass().getName() + "::onAutoFocus";
        Log.d(TAG, String.format(Locale.US, "Entry. success: '%s', camera: '%s'", success, camera));
        mCamera.takePicture(null, null, this);
    } // public void onAutoFocus(boolean success, Camera camera)
    
    static class SavePhotoTask extends AsyncTask<byte[], String, String>
    {
        private String filename;
        private PhotographManager photograph_manager;
        
        public SavePhotoTask(String filename, PhotographManager photograph_manager)
        {
            super();
            final String TAG = getClass().getName() + "::SavePhotoTask";
            Log.d(TAG, String.format(Locale.US, "Entry. Filename: '%s', photograph_manager: '%s'", filename, photograph_manager));            
            this.filename = filename;
            this.photograph_manager = photograph_manager;
        } // public SavePhotoTask(...)

        @Override
        protected String doInBackground(byte[]... jpeg)
        {
            final String TAG = getClass().getName() + "::doInBackground";
            Log.d(TAG, "Entry");
            photograph_manager.savePhotographToStorage(filename, jpeg[0]);
            Log.d(TAG, "Returning.");
            return(null);
        } // protected String doInBackground(byte[]... jpeg)
    } // class SavePhotoTask extends AsyncTask<byte[], String, String>

    public void onPictureTaken(byte[] data, Camera camera)
    {
        final String TAG = HEADER_TAG + "::onPictureTaken";
        Log.d(TAG, "Entry.");
        
        // ---------------------------------------------------------------------
        //  Save the image, then tell the parent activity to go back to the
        //  condition report activity.
        // ---------------------------------------------------------------------
        String filename = activity.getFilename();
        Log.d(TAG, String.format(Locale.US, "File is: '%s'", filename));        

        PhotographManager photograph_manager = ((ApplicationContext)activity.getApplication()).getPhotographManager();        
        new SavePhotoTask(filename, photograph_manager).execute(data);
        
        ((CameraActivity)getActivity()).returnToConditionReport();
        // ---------------------------------------------------------------------        
        
    } // public void onPictureTaken(byte[] data, Camera camera)

} // public class CameraFragment

// ----------------------------------------------------------------------

/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered
 * preview of the Camera to the surface. We need to center the SurfaceView
 * because not all devices have cameras that support preview sizes at the same
 * aspect ratio as the device's display.
 */
class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;

    Preview(Context context) {
        super(context);

        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters()
                    .getSupportedPreviewSizes();
            requestLayout();
        }
    }

    public void switchCamera(Camera camera) {
        setCamera(camera);
        try {
            camera.setPreviewDisplay(mHolder);
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        requestLayout();

        camera.setParameters(parameters);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width,
                    height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height
                        / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width
                        / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2, width,
                        (height + scaledChildHeight) / 2);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        parameters.setPictureFormat(PixelFormat.JPEG);
        parameters.setJpegQuality(75);
        requestLayout();

        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

}
