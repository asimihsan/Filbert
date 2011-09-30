package com.articheck.android.fragments;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import com.articheck.android.ToolbarButton;
import com.articheck.android.TouchView;
import com.articheck.android.activities.PhotographActivity;
import com.articheck.android.Toolbar;
import com.articheck.android.objects.Photograph;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotographFragment
extends Fragment
{
    final String HEADER_TAG = getClass().getName();
    private PhotographActivity activity;    
    public final static String FRAGMENT_TAG = "fragment_photograph";    
    private TouchView touch_view;
	private Toolbar toolbar;
    
    public static final int MSG_TYPE_ACTION_DOWN =     0x01;
    public static final int MSG_TYPE_ACTION_UP   =     0x02;
    public static final int MSG_TYPE_ACTION_MOVE =     0x03;
    public static final int MSG_TYPE_LONG_PRESS_DOWN = 0x04;
    
    private static int MOVE_WHILE_DOWN_LIMIT = 5;
    private Handler mChildHandler = null;
    private ChildThread mChildThread;
    class ChildThread extends Thread
    {
        private final String TAG = HEADER_TAG + "::" + getClass().getName();
        private AtomicBoolean is_locked;
        private int move_counter = 0;
        private TouchView touch_view;
       
        public void setTouchView(TouchView touch_view)
        {
        	this.touch_view = touch_view;
        }
        
        public void setIsLocked(AtomicBoolean is_locked)
        {
            this.is_locked = is_locked;
        }
        
        public void run()
        {            
            this.setName("child");
            Looper.prepare();
            mChildHandler = new Handler()
            {
                public void handleMessage(Message msg)
                {
                    final String SUB_TAG = TAG + "::handleMessage";                    
                    switch(msg.what)
                    {                            
                        case MSG_TYPE_ACTION_DOWN:
                            Log.d(SUB_TAG, "MSG_TYPE_ACTION_DOWN");
                            move_counter = 0;
                            mChildHandler.sendEmptyMessageDelayed(MSG_TYPE_LONG_PRESS_DOWN, 1000);
                            break;
                        case MSG_TYPE_ACTION_UP:
                            Log.d(SUB_TAG, "MSG_TYPE_ACTION_UP");
                            move_counter = 0;                            
                            mChildHandler.removeMessages(MSG_TYPE_LONG_PRESS_DOWN);
                            break;
                        case MSG_TYPE_ACTION_MOVE:
                            move_counter += 1;
                            if (move_counter > MOVE_WHILE_DOWN_LIMIT)
                            {
                                //Log.d(SUB_TAG, "MSG_TYPE_ACTION_MOVE enough times that we're delaying the long press.");
                                mChildHandler.removeMessages(MSG_TYPE_LONG_PRESS_DOWN);
                                mChildHandler.sendEmptyMessageDelayed(MSG_TYPE_LONG_PRESS_DOWN, 1000);
                                move_counter = 0;
                            } // if (move_counter > MOVE_WHILE_DOWN_LIMIT)                            
                            break;
                        case MSG_TYPE_LONG_PRESS_DOWN:
                            Log.d(SUB_TAG, "MSG_TYPE_LONG_PRESS_DOWN");
                            break;                            
                    } // switch(msg.what)                    
                } // public void handleMessage(Message msg)
            }; // mChildHandler = new Handler()     
            this.touch_view.setPhotographFragmentHandler(mChildHandler);
            Looper.loop();
        } // public void run()
    } // class ChildThread extends Thread    
    
    public Handler getChildHandler()
    {
    	return mChildHandler;
    }

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
        // one activity in the app's Activity hierarchy.
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
        touch_view = new TouchView(activity);
        touch_view.setActivity(activity);
        return touch_view;
    }    
    
    public TouchView getTouchView()
    {
        return touch_view;
    } // public TouchView getTouchView()
    
    @Override
    public void onResume()
    {
        super.onResume();        
        final String TAG = HEADER_TAG + "::onResume";
        Log.d(TAG, "Entry.");
        Photograph photograph = activity.getPhotograph();
        touch_view.setDrawable(photograph.getDrawable());   
        touch_view.startChildHandler();
        if (mChildThread == null)
        {
        	Log.d(TAG, "mChildThread is null, so recreate it.");
            mChildThread = new ChildThread();
            mChildThread.setTouchView(touch_view);
            mChildThread.start();
        }        
        mChildThread.setIsLocked(touch_view.getIsLocked());
        
        toolbar = activity.getToolbar();
        toolbar.setTouchView(touch_view);
    } // public void onResume()
    
    @Override
    public void onPause()
    {
        super.onPause();
        
        final String TAG = HEADER_TAG + "::onPause";
        Log.d(TAG, "Entry.");
        
        try
        {
            mChildHandler.getLooper().quit();
        }
        catch (NullPointerException e)
        {
            // already been stopped, but calling quit() sets
            // the Looper's internal mQueue to null and
            // I can't see a way of querying mQueue's state.
        }        
        mChildHandler = null;
        
        touch_view.stopChildHandler();
    }

	public void setToolbar(Toolbar toolbar)
	{
		this.toolbar = toolbar;
	}
}


