package com.articheck.android.fragments;

import java.util.Locale;

import com.articheck.android.TouchView;
import com.articheck.android.activities.PhotographActivity;
import com.articheck.android.objects.Photograph;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PhotographFragment extends Fragment
{
    final String HEADER_TAG = getClass().getName();
    private PhotographActivity activity;    
    final static String FRAGMENT_TAG = "fragment_photograph";    
    private TouchView view;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final String TAG = "::onCreate";
        Log.d(TAG, "Entry.");
        activity = (PhotographActivity) this.getActivity();
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        final String TAG = "::onActivityCreated";
        Log.d(TAG, "Entry.");        
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final String TAG = "::onCreateView";
        Log.d(TAG, "Entry.");        
        view = new TouchView(activity);
        return view;
    }    
    
    @Override
    public void onResume()
    {
        super.onResume();        
        final String TAG = HEADER_TAG + "::onResume";
        Log.d(TAG, "Entry.");
        Photograph photograph = activity.getPhotograph();
        view.setDrawable(photograph.getDrawable());
    } // public void onResume()
    
    @Override
    public void onPause()
    {
        super.onPause();
        
        final String TAG = HEADER_TAG + "::onPause";
        Log.d(TAG, "Entry.");        
    }    
}
