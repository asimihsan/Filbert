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

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
 
import com.articheck.android.activities.PhotographActivity;
import com.articheck.android.fragments.PhotographFragment;
import com.articheck.android.messages.MessageObjectPool;
import com.articheck.android.messages.SymbolMessageObject;
import com.articheck.android.utilities.Point;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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
import android.widget.Toast;

class DrawnSymbolManager
{
    private List<DrawnSymbol> drawn_symbols;
    private List<DrawnSymbol> unmodifiable_drawn_symbols;
    private DrawnSymbol last_drawn_symbol;
    
    DrawnSymbolManager()
    {
        initialize();
    } // DrawnSymbolManager()
    
    private void initialize()
    {
        drawn_symbols = Lists.newArrayList();
        unmodifiable_drawn_symbols = Collections.unmodifiableList(drawn_symbols);
        last_drawn_symbol = null;
    } // private void initialize()
    
    public DrawnSymbol getLastDrawnSymbol()
    {
    	return last_drawn_symbol;
    }
    
    public void addDrawnSymbol(DrawnSymbol drawn_symbol)
    {
        drawn_symbols.add(drawn_symbol);
        unmodifiable_drawn_symbols = Collections.unmodifiableList(drawn_symbols);
        last_drawn_symbol = drawn_symbols.get(drawn_symbols.size() - 1);
    } // public void addDrawnSymbol(DrawnSymbol drawn_symbol)
    
    public List<DrawnSymbol> getDrawnSymbols()
    {
        return unmodifiable_drawn_symbols;
    } // public List<DrawnSymbol> getDrawnSymbols()
    
} // class DrawnSymbolManager

class DrawnSymbol
{
    private ToolbarButton toolbar_button;
    private Drawable drawable;
    private int x_offset;
    private int y_offset;
    private float scale_factor;
    private boolean is_editable;
    
    private String color_name;
    private Integer color_value;
    private Map<String, Integer> lookup_color_name_to_color_value;
    
    DrawnSymbol(ToolbarButton toolbar_button,
                  Point location)
    {
        this.toolbar_button = toolbar_button;
        this.x_offset = location.getX();
        this.y_offset = location.getY();
        initialize();
    } // DrawnSymbol(Drawable drawable, int x_offset, int y_offset)

    private void initialize()
    {
        this.is_editable = false;
        this.scale_factor = 1.0f;        
        drawable = toolbar_button.getSymbolBitmapDrawable(scale_factor);
        
        // ---------------------------------------------------------------------
        //  !!AI Today is a good day to put together nasty, unarchitected
        //  hacks apparently.  This definitely belongs in the values
        //  resources file.
        // ---------------------------------------------------------------------        
        lookup_color_name_to_color_value = Maps.newHashMap();
        lookup_color_name_to_color_value.put("color_orange", 0xCCF89938);
        lookup_color_name_to_color_value.put("color_green", 0xCC32B44A);
        lookup_color_name_to_color_value.put("color_blue", 0xCC2C99CE);        
        // ---------------------------------------------------------------------

    } // private void initialize()
    
    public void setIsEditable(boolean is_editable)
    {
    	this.is_editable = is_editable;
    }
    
    public boolean getIsEditable()
    {
    	return this.is_editable;
    }
    
    public void setScaleFactor(float scale_factor)
    {
        if (scale_factor != this.scale_factor)
        {
            this.scale_factor = scale_factor;
            this.drawable = toolbar_button.getSymbolBitmapDrawable(scale_factor);            
        } // if (scale_factor != this.scale_factor)        
    } // public void setScaleFactor(float scale_factor)
    
    public float getScaleFactor()
    {
        return this.scale_factor;
    }
    
    public Drawable getDrawable()
    {
        return drawable;        
    } // public Drawable getDrawable()
    
    public int getXOffset()
    {
        return x_offset;
    } // public int getXOffset()
    
    public int getYOffset()
    {
        return y_offset;
    } // public int getYOffset()    
    
    public void setXOffset(int x_offset)
    {
    	this.x_offset = x_offset;
    }
    
    public void setYOffset(int y_offset)
    {
    	this.y_offset = y_offset;
    }
    
    public String getColorName()
    {
        return color_name;
    } // public String getColor()
    
    public void setColorName(String color)
    {
        this.color_name = color;
        setColorValue(lookup_color_name_to_color_value.get(color));
    } // public void setColor(String color_name)
    
    public void setColorValue(Integer value)
    {
        this.color_value = value;
    } // public void setColorValue(Integer value)
    
    public Integer getColorValue()
    {
        return this.color_value;
    } // public Integer getColorValue()
    
    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                       .add("toolbar_button", toolbar_button)
                       .add("drawable", drawable)
                       .add("x_offset", x_offset)
                       .add("y_offset", y_offset)
                       .add("scale_factor", scale_factor)
                       .add("is_editable", is_editable)
                       .add("color_name", color_name)
                       .toString();
    } // public String toString()
    
    @Override
    public int hashCode()
    {
        return Objects.hashCode(drawable,
                                 x_offset,
                                 y_offset);
    } // public int hashCode()
    
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        } // if (o == this)
        if (!(o instanceof DrawnSymbol))
        {
            return false;
        } // if (!(o instanceof DrawnSymbol))
        DrawnSymbol ds = (DrawnSymbol)o;
        boolean result = (Objects.equal(drawable, ds.drawable)
                           && Objects.equal(x_offset, ds.x_offset)
                           && Objects.equal(y_offset, ds.y_offset));
        return result;
    } // public boolean equals(Object o)
    
}

public class TouchView
extends View
{
    private final String HEADER_TAG = getClass().getName();
    private static final float MINIMUM_PHOTO_SCALE_FACTOR = 1.0f;
    private static final float MAXIMUM_PHOTO_SCALE_FACTOR = 2.0f;    
    private static final float MINIMUM_SYMBOL_SCALE_FACTOR = 0.2f;
    private static final float MAXIMUM_SYMBOL_SCALE_FACTOR = 2.0f;    
    
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
    private Handler mPhotographFragmentHandler;
    
    private Paint mPaint;
    private final Rect mRect = new Rect();
    private Canvas mCanvas;
    private PhotographActivity activity;
    
    private DrawnSymbolManager drawn_symbol_manager;
    
    public static final int MSG_TYPE_ADD_SYMBOL                               = 0x01;
    public static final int MSG_TYPE_SET_TO_LOCKED                            = 0x02;
    public static final int MSG_TYPE_SET_TO_UNLOCKED                          = 0x03;
    public static final int MSG_TYPE_MOVE_SYMBOL                              = 0x04;
    public static final int MSG_TYPE_SYMBOL_TOOLBAR_BUTTON_JUST_CLICKED       = 0x05;
    public static final int MSG_TYPE_COLOR_TOOLBAR_BUTTON_JUST_CLICKED        = 0x06;
    
    private Handler mChildHandler = null;
    private ChildThread mChildThread;
    class ChildThread extends Thread
    {
        private final String TAG = HEADER_TAG + "::" + getClass().getName();
        private AtomicBoolean is_locked;        
        private DrawnSymbolManager drawn_symbol_manager;
        
        ChildThread(AtomicBoolean is_locked,
                     DrawnSymbolManager drawn_symbol_manager)
        {
        	super();
        	this.is_locked = is_locked;        	
        	this.drawn_symbol_manager = drawn_symbol_manager;        	
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
                    Toolbar toolbar = activity.getToolbar();
                    
                    DrawnSymbol last_drawn_symbol;
                    SymbolMessageObject message_object;
                    String symbol_name;
                    Point location;
                    ApplicationContext application_context =  ((ApplicationContext)(activity.getApplicationContext()));
                    MessageObjectPool message_object_pool = application_context.getMessageObjectPool();
                    String color_name;
                    
                    switch(msg.what)
                    {                            
                        case MSG_TYPE_ADD_SYMBOL:
                            message_object = (SymbolMessageObject) msg.obj;
                            Log.d(SUB_TAG, String.format(Locale.US, "MSG_TYPE_ADD_SYMBOL. Message object: '%s'", message_object));
                            assert(message_object != null);
                            
                            symbol_name = message_object.getSymbolName();
                            location = message_object.getLocation();                            
                            ToolbarButton toolbar_button = toolbar.getToolbarButtonByName(symbol_name);
                            color_name = toolbar.getCurrentColor().getName();
                            addSymbolByToolbarButton(toolbar_button,
                                                     location,
                                                     color_name,
                                                     null);
                            forceInvalidate();
                            
                            message_object_pool.returnSymbolMessageObject(message_object);
                            break;
                            
                        case MSG_TYPE_MOVE_SYMBOL:
                            message_object = (SymbolMessageObject) msg.obj;
                            Log.d(SUB_TAG, String.format(Locale.US, "MSG_TYPE_MOVE_SYMBOL. Message object: '%s'", message_object));
                            assert(message_object != null);
                            
                            symbol_name = message_object.getSymbolName();
                            location = message_object.getLocation();
                            last_drawn_symbol = drawn_symbol_manager.getLastDrawnSymbol();
                            assert(last_drawn_symbol != null);
                            last_drawn_symbol.setXOffset(location.getX());
                            last_drawn_symbol.setYOffset(location.getY());                            
                            forceInvalidate();
                            
                            message_object_pool.returnSymbolMessageObject(message_object);
                        	break;
                            
                        case MSG_TYPE_SET_TO_LOCKED:
                        	Log.d(SUB_TAG, "MSG_TYPE_SET_TO_LOCKED");
                        	is_locked.set(true);
                        	toolbar.clearLastClicked();
                        	break;

                        case MSG_TYPE_SET_TO_UNLOCKED:
                        	Log.d(SUB_TAG, "MSG_TYPE_SET_TO_UNLOCKED");
                        	is_locked.set(false);
                        	toolbar.clearLastClicked();
                        	last_drawn_symbol = drawn_symbol_manager.getLastDrawnSymbol();
                        	Log.d(SUB_TAG, String.format(Locale.US, "last_drawn_symbol: '%s'", last_drawn_symbol));
                        	if (last_drawn_symbol != null)
                        	{
                        	    Log.d(SUB_TAG, "Set last drawn symbol to non-editable.");
                        	    last_drawn_symbol.setIsEditable(false);
                        	} // if (last_drawn_symbol != null)                        	
                        	break;                        	
                        	
                        case MSG_TYPE_SYMBOL_TOOLBAR_BUTTON_JUST_CLICKED:
                            Log.d(SUB_TAG, "MSG_TYPE_SYMBOL_TOOLBAR_BUTTON_JUST_CLICKED");  
                            last_drawn_symbol = drawn_symbol_manager.getLastDrawnSymbol();
                            if (last_drawn_symbol != null)
                            {
                                Log.d(SUB_TAG, "Set last drawn symbol to non-editable.");
                                last_drawn_symbol.setIsEditable(false);
                            } // if (last_drawn_symbol != null)
                            break;
                            
                        case MSG_TYPE_COLOR_TOOLBAR_BUTTON_JUST_CLICKED:
                            Log.d(SUB_TAG, "MSG_TYPE_SYMBOL_TOOLBAR_BUTTON_JUST_CLICKED");
                            color_name = toolbar.getCurrentColor().getName();
                            Log.d(SUB_TAG, String.format(Locale.US, "Current color_name: '%s'", color_name));
                            last_drawn_symbol = drawn_symbol_manager.getLastDrawnSymbol();
                            if ((last_drawn_symbol != null) && (last_drawn_symbol.getIsEditable()))
                            {
                                Log.d(SUB_TAG, "Last drawn symbol exists and is editable.");
                                last_drawn_symbol.setColorName(color_name);
                                forceInvalidate();
                            } // if ((last_drawn_symbol != null) && (last_drawn_symbol.getIsEditable()))
                            break;
                            
                    } // switch(msg.what)                    
                } // public void handleMessage(Message msg)
            }; // mChildHandler = new Handler()            
            Looper.loop();
        } // public void run()
    } // class ChildThread extends Thread        
    
    public Handler getChildHandler()
    {
    	return mChildHandler;
    }
    
    public void stopChildHandler()
    {
        try
        {
        	mChildHandler.getLooper().quit();
        }
        catch (NullPointerException e)
        {
        	
        }
    	this.mChildHandler = null;
    	this.mChildThread = null;
    }
    
    public void startChildHandler()
    {
        if (mChildThread == null)
        {
            mChildThread = new ChildThread(this.is_locked,
                                           this.drawn_symbol_manager);
            mChildThread.start();        	
        }    	
    }
    
    public TouchView(Context context)
    {
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
        drawn_symbol_manager = new DrawnSymbolManager();
        
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
        if (mScaleDetector.isInProgress())
        {
            //Log.d(TAG, "Scale in progress, so ignore other touch handling.");
            return true;
        } // if (mScaleDetector.isInProgress())
        
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            {
                mPhotographFragmentHandler.sendEmptyMessage(PhotographFragment.MSG_TYPE_ACTION_DOWN);
                final float x = ev.getX();
                final float y = ev.getY();
                
                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = ev.getPointerId(0);
                
                // -----------------------------------------------------------
                //	If the photograph is currently locked then touching
                //	the view may mean we're ready to draw a symbol. This is
                //	only true if the user has clicked a symbol on the toolbar.
                // -----------------------------------------------------------
                Toolbar toolbar = activity.getToolbar();
                ToolbarButton last_clicked_toolbar_button = toolbar.getLastClicked();
                if (is_locked.get() && last_clicked_toolbar_button != null)
                {
                	Log.d(TAG, String.format(Locale.US, "Locked and user has clicked toolbar button: '%s'", last_clicked_toolbar_button));
                	
                	// -------------------------------------------------------
                	//	At this stage we're locked and there's a toolbar
                	//  button selected.  However, if we've already drawn
                	//  an instance of this symbol onto the view we do not
                	//  want to create a new copy each time the user clicks!
                	//  Hence, check if the symbol is already drawn by
                	//	asking the DrawnSymbolManager.
                	//
                	//  If it already drawn, move the existing instance.
                	//  If it is not already drawn, create a new instance
                	//  anywhere on the photo.
                	// -------------------------------------------------------
                    String symbol_name = last_clicked_toolbar_button.getName();
                    assert(symbol_name != null);
                    Point location = new Point((int)mLastTouchX, (int)mLastTouchY);
                    
                    ApplicationContext application_context =  ((ApplicationContext)(activity.getApplicationContext()));
                    MessageObjectPool message_object_pool = application_context.getMessageObjectPool();                    
                    SymbolMessageObject message_object = message_object_pool.getSymbolMessageObject(symbol_name, location);                	
                	
                	DrawnSymbol last_drawn_symbol = drawn_symbol_manager.getLastDrawnSymbol();
                	Log.d(TAG, String.format(Locale.US, "last_drawn_symbol: '%s'", last_drawn_symbol));
                	
                	int message_id;                	
                	if ((last_drawn_symbol == null) || (last_drawn_symbol.getIsEditable() == false))
                	{
                		// Create new symbol.
                		Log.d(TAG, "No previously drawn symbols or last drawn symbol no longer editable.");                 		
                		message_id = TouchView.MSG_TYPE_ADD_SYMBOL;
                	}
                	else
                	{
                		// Move existing symbol.                	    
                		Log.d(TAG, "Previously drawn symbol is still editable.");
                		message_id = TouchView.MSG_TYPE_MOVE_SYMBOL;                        
                		
                	} // if ((last_drawn_symbol == null) || (last_drawn_symbol.getIsEditable() == false))
                	Message message = mChildHandler.obtainMessage(message_id, message_object);
                	mChildHandler.sendMessage(message);
                	// -------------------------------------------------------

                } // (is_locked.get() && last_clicked_toolbar_button != null)
                // -----------------------------------------------------------
                break;
            }
            
            case MotionEvent.ACTION_MOVE:
            {
                mPhotographFragmentHandler.sendEmptyMessage(PhotographFragment.MSG_TYPE_ACTION_MOVE);
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
                
                // -----------------------------------------------------------
                //  If the photograph is currently locked then touching
                //  the view may mean we're ready to move a symbol. This is
                //  only true if the user has drawn a symbol.
                // -----------------------------------------------------------
                Toolbar toolbar = activity.getToolbar();
                ToolbarButton last_clicked_toolbar_button = toolbar.getLastClicked();
                if (is_locked.get() && last_clicked_toolbar_button != null)
                {
                    Log.d(TAG, String.format(Locale.US, "Locked and user has clicked toolbar button: '%s'", last_clicked_toolbar_button));

                    String symbol_name = last_clicked_toolbar_button.getName();
                    assert(symbol_name != null);
                    Point location = new Point((int)mLastTouchX, (int)mLastTouchY);
                    
                    ApplicationContext application_context =  ((ApplicationContext)(activity.getApplicationContext()));
                    MessageObjectPool message_object_pool = application_context.getMessageObjectPool();                    
                    SymbolMessageObject message_object = message_object_pool.getSymbolMessageObject(symbol_name, location);                    
                    
                    DrawnSymbol last_drawn_symbol = drawn_symbol_manager.getLastDrawnSymbol();
                    Log.d(TAG, String.format(Locale.US, "last_drawn_symbol: '%s'", last_drawn_symbol));                   
                    
                    if ((last_drawn_symbol != null) && (last_drawn_symbol.getIsEditable() == true))
                    {
                        Log.d(TAG, "There is a previously drawn symbol and it is still editable..");                        
                        int message_id = TouchView.MSG_TYPE_MOVE_SYMBOL;
                        Message message = mChildHandler.obtainMessage(message_id, message_object);
                        mChildHandler.sendMessage(message);
                    } // if ((last_drawn_symbol == null) || (last_drawn_symbol.getIsEditable() == false))

                    // -------------------------------------------------------

                } // (is_locked.get() && last_clicked_toolbar_button != null)
                // -----------------------------------------------------------                    
    
                break;
            }
            
            case MotionEvent.ACTION_UP:
            {
                mPhotographFragmentHandler.sendEmptyMessage(PhotographFragment.MSG_TYPE_ACTION_UP);
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
        
        drawSymbols(canvas);
        
        canvas.restore();
    }

    private void drawSymbols(Canvas canvas)
    {
        // ---------------------------------------------------------------------
        //  For each drawn symbol draw it onto the canvas.
        // ---------------------------------------------------------------------
        for (DrawnSymbol drawn_symbol : drawn_symbol_manager.getDrawnSymbols())
        {
            Drawable symbol_drawable = drawn_symbol.getDrawable();            
            int x_offset = drawn_symbol.getXOffset();
            int y_offset = drawn_symbol.getYOffset();
            
            float scale_factor = drawn_symbol.getScaleFactor();
            int resized_width = (int) (symbol_drawable.getIntrinsicWidth() * scale_factor);
            int resized_height = (int) (symbol_drawable.getIntrinsicHeight() * scale_factor);
            
            symbol_drawable.setBounds(x_offset,
                                      y_offset,
                                      resized_width + x_offset,
                                      resized_height + y_offset);
            
            Integer color_value = drawn_symbol.getColorValue();
            if (color_value != null)                
            {
                symbol_drawable.setColorFilter(color_value, PorterDuff.Mode.MULTIPLY);    
            } // if (color_value != null)            
            symbol_drawable.draw(canvas);
        } // for (DrawnSymbol drawn_symbol : drawn_symbol_manager.getDrawnSymbols())
        // ---------------------------------------------------------------------       
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {            
            //final String TAG = HEADER_TAG + "::onScale";
            //Log.d(TAG, "Entry.");
            
            // -----------------------------------------------------------------
            //  When scaling on an unlocked photograph we scale the photograph.
            //  When scaling on a locked photograph, if there is a drawn
            //  symbol and it is still editable then we scale that symbol.
            // -----------------------------------------------------------------            
            if (!is_locked.get())
            {               
                // Scale photo.                
                mScaleFactor *= detector.getScaleFactor();
                
                // Don't let the object get too small or too large.
                mScaleFactor = Math.max(MINIMUM_PHOTO_SCALE_FACTOR, Math.min(mScaleFactor, MAXIMUM_PHOTO_SCALE_FACTOR));

                invalidate();                
            }
            else
            {
                // Scale symbol.
                //Log.d(TAG, "Scale symbol.");
                
                Toolbar toolbar = activity.getToolbar();
                ToolbarButton last_clicked_toolbar_button = toolbar.getLastClicked();
                if (last_clicked_toolbar_button != null)
                {
                    DrawnSymbol last_drawn_symbol = drawn_symbol_manager.getLastDrawnSymbol();                    
                    if ((last_drawn_symbol != null) && (last_drawn_symbol.getIsEditable() == true))
                    {
                        float current_scale_factor = last_drawn_symbol.getScaleFactor();
                        float new_scale_factor = current_scale_factor * detector.getScaleFactor();
                        new_scale_factor = Math.max(MINIMUM_SYMBOL_SCALE_FACTOR, Math.min(new_scale_factor, MAXIMUM_SYMBOL_SCALE_FACTOR));
                        last_drawn_symbol.setScaleFactor(new_scale_factor);
                        //Log.d(TAG, String.format(Locale.US, "last_drawn_symbol is now: '%s'", last_drawn_symbol));
                        forceInvalidate();
                    } // if ((last_drawn_symbol == null) || (last_drawn_symbol.getIsEditable() == false))
                } // if (last_clicked_toolbar_button != null)
                
            } // if (!is_locked.get())
            // -----------------------------------------------------------------            
            return true;            
        }
    }
    
    public void forceInvalidate()
    {
        postInvalidate();
    } // public void forceInvalidate()

    public void setPhotographFragmentHandler(Handler handler)
    {
        final String TAG = HEADER_TAG + "::setPhotographFragmentHandler";
        Log.d(TAG, "Entry.");
        
        this.mPhotographFragmentHandler = handler;        
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

    public void setActivity(PhotographActivity activity)
    {
        this.activity = activity;
    } // public void setActivity(PhotographActivity activity)   
    
    public void addSymbolByToolbarButton(ToolbarButton toolbar_button,
                                              Point location,
                                              String color_name,
                                              Float scale_factor)
    {                
        final String TAG = HEADER_TAG + "::addSymbolByToolbarButton";
        Log.d(TAG, String.format(Locale.US, "Entry. toolbar_button: '%s', location: '%s', scale_factor: '%s'", toolbar_button, location, scale_factor));        

        DrawnSymbol drawn_symbol = new DrawnSymbol(toolbar_button,
                                                   location);
        drawn_symbol.setIsEditable(true);
        drawn_symbol.setColorName(color_name);
        drawn_symbol_manager.addDrawnSymbol(drawn_symbol);
        Log.d(TAG, String.format(Locale.US, "Added drawn symbol '%s'.", drawn_symbol));
    } // public void addSymbolByToolbarButton(ToolbarButton toolbar_button)
    
    // ---------------------------------------------------------------------            
}
