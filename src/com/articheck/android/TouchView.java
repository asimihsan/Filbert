/*
 * Copyright (C) 2010 The Android Open Source Project
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
import java.util.concurrent.atomic.AtomicBoolean;

import com.articheck.android.activities.PhotographActivity;
import com.articheck.android.fragments.PhotographFragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class TouchView
extends View
{
    private final String HEADER_TAG = getClass().getName();
    private static final float MINIMUM_SCALE_FACTOR = 1.0f;
    private static final float MAXIMUM_SCALE_FACTOR = 2.0f;    
    
    private static final int INVALID_POINTER_ID = -1;
    
    private Drawable drawable;
    private Rect rect_drawable;
    private Rect rect_visible;
    private float mPosX;
    private float mPosY;
    
    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;
    
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.0f;
    
    private AtomicBoolean is_locked = new AtomicBoolean(false);
    private Handler mHandler;
    
    private Paint mPaint;
    private final Rect mRect = new Rect();
    private Canvas mCanvas;
    
    public TouchView(Context context) {
        this(context, null, 0);
    }
    
    public TouchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public TouchView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);        
        initialize(context);
    }
    
    private void initialize(Context context)
    {
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setARGB(255, 255, 255, 255);        
        
        mCanvas = new Canvas();
    } // private void initialize(Context context)
    
    public void setDrawable(Drawable drawable)
    {
        final String TAG = HEADER_TAG + "::setDrawable";        
        this.drawable = drawable;
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        rect_drawable = drawable.getBounds();
        Log.d(TAG, String.format("Bounds: '%s'", rect_drawable));
        
    } // public void setDrawable(Drawable drawable)
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parent_width = MeasureSpec.getSize(widthMeasureSpec);
        int parent_height = MeasureSpec.getSize(heightMeasureSpec);
        rect_visible = new Rect(0, 0, parent_width, parent_height);
    }    
    
    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        final String TAG = HEADER_TAG + "::onTouchEvent";
        //Log.d(TAG, String.format(Locale.US, "Entry. ev: '%s'", ev));
        
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);
        
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            {
                mHandler.sendEmptyMessage(PhotographFragment.MSG_TYPE_ACTION_DOWN);
                final float x = ev.getX();
                final float y = ev.getY();
                
                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = ev.getPointerId(0);
                break;
            }
            
            case MotionEvent.ACTION_MOVE:
            {
                mHandler.sendEmptyMessage(PhotographFragment.MSG_TYPE_ACTION_MOVE);
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);
    
                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress() && !is_locked.get())
                {
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;
                    mPosX += dx;
                    mPosY += dy;
                    invalidate();
                }
    
                mLastTouchX = x;
                mLastTouchY = y;
    
                break;
            }
            
            case MotionEvent.ACTION_UP:
            {
                mHandler.sendEmptyMessage(PhotographFragment.MSG_TYPE_ACTION_UP);
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            
            case MotionEvent.ACTION_CANCEL:
            {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            
            case MotionEvent.ACTION_POINTER_UP:
            {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) 
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }        
        return true;
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        
        final String TAG = HEADER_TAG + "::onDraw";
        //Log.d(TAG, String.format("Entry. mPoxX: '%s', mPosY: '%s', mScaleFactor: '%s'", mPosX, mPosY, mScaleFactor));        
        
        int origin_x = (int)mPosX;
        int origin_y = (int)mPosY;
        int proposed_dx = (int)(origin_x + (rect_drawable.width() * mScaleFactor));
        int proposed_dy = (int)(origin_y + (rect_drawable.height() * mScaleFactor));
        Rect rect_proposed = new Rect(origin_x, origin_y, proposed_dx, proposed_dy);
        //Log.d(TAG, String.format(Locale.US, "rect_proposed: '%s', rect_visible: '%s', rect_drawable: '%s'", rect_proposed, rect_visible, rect_drawable));
        if (rect_proposed.left > 0)
        {
            mPosX -= rect_proposed.left;
        }
        if (rect_proposed.top > 0)
        {
            mPosY -= rect_proposed.top;
        }
        if (rect_proposed.right < rect_visible.right)
        {
            mPosX += (rect_visible.right - rect_proposed.right);
        }
        if (rect_proposed.bottom < rect_visible.bottom)
        {
            mPosY += (rect_visible.bottom - rect_proposed.bottom);
        }                
       
        canvas.save();
        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor);
        drawable.draw(canvas);
        canvas.restore();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            if (!is_locked.get())
            {
                mScaleFactor *= detector.getScaleFactor();
                
                // Don't let the object get too small or too large.
                mScaleFactor = Math.max(MINIMUM_SCALE_FACTOR, Math.min(mScaleFactor, MAXIMUM_SCALE_FACTOR));

                invalidate();                
            }
            return true;            
        }
    }

    public void setHandler(Handler handler)
    {
        final String TAG = HEADER_TAG + "::setHandler";
        Log.d(TAG, "Entry.");
        
        this.mHandler = handler;        
    } // public void setHandler(Handler handler)
    
    public void setLockedToValue(boolean value)
    {
        final String TAG = HEADER_TAG + "::setLockedToValue";
        Log.d(TAG, String.format(Locale.US, "Entry. value: '%s'", value));
        is_locked.set(value);
    } // public void setLockedToValue(boolean value)
    
    public AtomicBoolean getIsLocked()
    {
        return is_locked;
    }
    
    private void drawPoint(float x, float y, float pressure, float width)
    {
        final String TAG = HEADER_TAG + "::drawPoint";
        Log.d(TAG, String.format(Locale.US, "Entry. x: '%s', y: '%s', pressure: '%s', width: '%s'",x, y, pressure, width));

        if (width < 1)
        {
            width = 1;
        }

        float radius = width / 2;
        int pressureLevel = (int)(pressure * 255);
        mPaint.setARGB(pressureLevel, 255, 255, 255);
        //mCanvas.drawCircle(x, y, radius, mPaint);
        mRect.set((int) (x - radius - 2), (int) (y - radius - 2),
                (int) (x + radius + 2), (int) (y + radius + 2));
        invalidate(mRect);
    }    
}
