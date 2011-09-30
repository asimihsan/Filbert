package com.articheck.android.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.articheck.android.ApplicationContext;
import com.articheck.android.R;
import com.articheck.android.R.layout;
import com.articheck.android.Toolbar;
import com.articheck.android.ToolbarButton;
import com.articheck.android.TouchView;
import com.articheck.android.fragments.ConditionReportsFragment;
import com.articheck.android.fragments.PhotographFragment;
import com.articheck.android.managers.PhotographManager;
import com.articheck.android.objects.Photograph;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PhotographActivity
extends Activity
{
    final String HEADER_TAG = getClass().getName();
    private String photograph_id;
    private String condition_report_id;
    private Photograph photograph;    
    private Toolbar toolbar;
    private AtomicBoolean is_photograph_locked = new AtomicBoolean(false);
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final String TAG = HEADER_TAG + "::onCreate";
        Log.d(TAG, String.format(Locale.US, "Entry. savedInstanceState: '%s'", savedInstanceState));
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photograph_activity);
        
        Bundle extras = this.getIntent().getExtras();
        assert(extras.containsKey("photograph_id"));
        assert(extras.containsKey("condition_report_id"));
        
        photograph_id = extras.getString("photograph_id");
        Log.d(TAG, String.format(Locale.US, "photograph_id is: '%s'", photograph_id));
        
        condition_report_id = extras.getString("condition_report_id");
        Log.d(TAG, String.format(Locale.US, "condition_report_id is: '%s'", condition_report_id));
        
        photograph = getPhotographManager().getPhotographByPhotographId(photograph_id);
        Log.d(TAG, String.format(Locale.US, "photograph is: '%s'", photograph));
        assert(photograph != null);        
    } // protected void onCreate(Bundle savedInstanceState)
    
    @Override
    protected void onResume()
    {
        final String TAG = HEADER_TAG + "::onResume";
        Log.d(TAG, "Entry.");
        
        super.onResume();
        Resources resources = getResources();
        boolean initializing_from_scratch = false;
        
        // ---------------------------------------------------------------------
        //	Alter the visibility of the buttons on the photograph depending
        //	on whether we've locked the photo or not.
        // ---------------------------------------------------------------------
        View v = findViewById(R.id.photograph_buttons);
        if (is_photograph_locked.get())
        {
        	Log.d(TAG, "Photograph is locked, so toolbar is visible.");
        	v.setVisibility(View.VISIBLE);
        }
        else
        {
        	Log.d(TAG, "Photograph is unlocked, so toolbar is invisible.");
        	v.setVisibility(View.INVISIBLE);        	
        }
        // ---------------------------------------------------------------------        
        
        // ---------------------------------------------------------------------
        //  Based on DPI density of the screen determine the dimensions of the
        //  button toolbar we're about to draw.
        // ---------------------------------------------------------------------        
        float scale = resources.getDisplayMetrics().density;
        int button_width = (int) (150 * scale);
        int button_height = (int) (150 * scale);
        int symbol_width = (int) (250 * scale);
        int symbol_height = (int) (250 * scale);
        // ---------------------------------------------------------------------        
        
        // ---------------------------------------------------------------------
        //  Load the toolbar and its buttons.
        // ---------------------------------------------------------------------
        if (toolbar == null)
        {
            Log.d(TAG, "Toolbar is null, initialize it.");
            initializing_from_scratch = true;
            toolbar = new Toolbar.Builder()
                                 .buttonWidth(button_width)
                                 .buttonHeight(button_height)
                                 .activity(this)
                                 .build();
            ToolbarButton toolbar_button;
            
            // -----------------------------------------------------------------
            //  Add the color buttons.
            // -----------------------------------------------------------------
            toolbar_button = new ToolbarButton.Builder()
                                              .name("color_blue")
                                              .drawableUpEnabledId(R.drawable.button_color_blue_up_enabled)
                                              .drawableDownEnabledId(R.drawable.button_color_blue_down_enabled)
                                              .bitmapUpEnabledResizedHeight(button_height)
                                              .bitmapUpEnabledResizedWidth(button_width)
                                              .bitmapDownEnabledResizedHeight(button_height)
                                              .bitmapDownEnabledResizedWidth(button_width)
                                              .resources(resources)
                                              .type(ToolbarButton.Type.COLOR)
                                              .build();
            toolbar.addToolbarButton(toolbar_button);
            toolbar_button = new ToolbarButton.Builder()
                                              .name("color_orange")
                                              .drawableUpEnabledId(R.drawable.button_color_orange_up_enabled)
                                              .drawableDownEnabledId(R.drawable.button_color_orange_down_enabled)
                                              .bitmapUpEnabledResizedHeight(button_height)
                                              .bitmapUpEnabledResizedWidth(button_width)
                                              .bitmapDownEnabledResizedHeight(button_height)
                                              .bitmapDownEnabledResizedWidth(button_width)
                                              .resources(resources)
                                              .type(ToolbarButton.Type.COLOR)
                                            .build();
            toolbar.addToolbarButton(toolbar_button);
            toolbar_button = new ToolbarButton.Builder()
                                              .name("color_green")
                                              .drawableUpEnabledId(R.drawable.button_color_green_up_enabled)
                                              .drawableDownEnabledId(R.drawable.button_color_green_down_enabled)
                                              .bitmapUpEnabledResizedHeight(button_height)
                                              .bitmapUpEnabledResizedWidth(button_width)
                                              .bitmapDownEnabledResizedHeight(button_height)
                                              .bitmapDownEnabledResizedWidth(button_width)
                                              .resources(resources)
                                              .type(ToolbarButton.Type.COLOR)
                                              .build();         
            toolbar.addToolbarButton(toolbar_button);
            // -----------------------------------------------------------------            
            
            // -----------------------------------------------------------------
            //  Add the symbol buttons.
            // -----------------------------------------------------------------            
            toolbar_button = new ToolbarButton.Builder()
                                              .name("stain")
                                              .drawableUpEnabledId(R.drawable.button_stain_up_enabled)
                                              .drawableDownEnabledId(R.drawable.button_stain_down_enabled)
                                              .drawableSymbolId(R.drawable.symbol_stain)
                                              .bitmapUpEnabledResizedHeight(button_height)
                                              .bitmapUpEnabledResizedWidth(button_width)
                                              .bitmapDownEnabledResizedHeight(button_height)
                                              .bitmapDownEnabledResizedWidth(button_width)                                              
                                              .bitmapSymbolResizedHeight(symbol_height)
                                              .bitmapSymbolResizedWidth(symbol_width)
                                              .resources(resources)
                                              .type(ToolbarButton.Type.SYMBOL)
                                              .build();
            toolbar.addToolbarButton(toolbar_button);
            toolbar_button = new ToolbarButton.Builder()
                                              .name("pinhole")
                                              .drawableUpEnabledId(R.drawable.button_pinhole_up_enabled)
                                              .drawableDownEnabledId(R.drawable.button_pinhole_down_enabled)
                                              .drawableSymbolId(R.drawable.symbol_pinhole)
                                              .bitmapUpEnabledResizedHeight(button_height)
                                              .bitmapUpEnabledResizedWidth(button_width)
                                              .bitmapDownEnabledResizedHeight(button_height)
                                              .bitmapDownEnabledResizedWidth(button_width)                                              
                                              .bitmapSymbolResizedHeight(symbol_height)
                                              .bitmapSymbolResizedWidth(symbol_width)                                              
                                              .resources(resources)
                                              .type(ToolbarButton.Type.SYMBOL)
                                              .build();
            toolbar.addToolbarButton(toolbar_button);
            toolbar_button = new ToolbarButton.Builder()
                                              .name("accretion")
                                              .drawableUpEnabledId(R.drawable.button_accretion_up_enabled)
                                              .drawableDownEnabledId(R.drawable.button_accretion_down_enabled)
                                              .drawableSymbolId(R.drawable.symbol_accretion)
                                              .bitmapUpEnabledResizedHeight(button_height)
                                              .bitmapUpEnabledResizedWidth(button_width)
                                              .bitmapDownEnabledResizedHeight(button_height)
                                              .bitmapDownEnabledResizedWidth(button_width)                                              
                                              .bitmapSymbolResizedHeight(symbol_height)
                                              .bitmapSymbolResizedWidth(symbol_width)                                              
                                              .resources(resources)
                                              .type(ToolbarButton.Type.SYMBOL)
                                              .build();
            toolbar.addToolbarButton(toolbar_button);
            toolbar_button = new ToolbarButton.Builder()
                                              .name("tear")
                                              .drawableUpEnabledId(R.drawable.button_tear_up_enabled)
                                              .drawableDownEnabledId(R.drawable.button_tear_down_enabled)
                                              .drawableSymbolId(R.drawable.symbol_tear)
                                              .bitmapUpEnabledResizedHeight(button_height)
                                              .bitmapUpEnabledResizedWidth(button_width)
                                              .bitmapDownEnabledResizedHeight(button_height)
                                              .bitmapDownEnabledResizedWidth(button_width)                                              
                                              .bitmapSymbolResizedHeight(symbol_height)
                                              .bitmapSymbolResizedWidth(symbol_width)                                              
                                              .resources(resources)
                                              .type(ToolbarButton.Type.SYMBOL)
                                              .build();
            toolbar.addToolbarButton(toolbar_button);  
            // -----------------------------------------------------------------
            
        } // if (toolbar == null)
        
        // ---------------------------------------------------------------------
        
        // ---------------------------------------------------------------------
        //  Set up the button toolbar on the left.
        //
        //  First add the color button, which is rather special, and then
        //  add the symbol buttons.  We will only add one color button, as
        //  clicking on it will reveal the other avaiable colors.
        // ---------------------------------------------------------------------
        if (initializing_from_scratch)
        {
            Log.d(TAG, "Set up the layout for the toolbar.");
            LinearLayout button_layout = (LinearLayout)findViewById(R.id.photograph_buttons);
            ImageButton image_button;
            
            // Add a random color button.
            List<ToolbarButton> all_color_buttons = toolbar.getToolbarButtons(ToolbarButton.Type.COLOR);
            assert(all_color_buttons.size() > 0);
            ToolbarButton random_color_button = all_color_buttons.get(0);
            image_button = toolbar.getImageButtonByToolbarButton(random_color_button);
            assert(image_button != null);
            button_layout.addView(image_button);        
            toolbar.setCurrentColor(random_color_button);
            
            // Add all the symbol buttons.
            for (ToolbarButton toolbar_button : toolbar.getToolbarButtons(ToolbarButton.Type.SYMBOL))
            {
                Log.d(TAG, String.format(Locale.US, "Adding view for toolbar button: '%s'", toolbar_button));                
                image_button = toolbar.getImageButtonByToolbarButton(toolbar_button);
                assert(image_button != null);
                button_layout.addView(image_button);
            } // for (ToolbarButton toolbar_button : toolbar.getToolbarButtons())
            
            toolbar.enableAllToolbarButtons();         
            toolbar.setAllToolbarButtonsToUp();
        } // if (initializing_from_scratch)
        // ---------------------------------------------------------------------
        
    } // protected void onResume()
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photograph_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	final String TAG = HEADER_TAG + "::onOptionsItemSelected";
    	Log.d(TAG, String.format(Locale.US, "Entry. item: '%s'", item));
        switch (item.getItemId())
        {
	        case R.id.toggle_lock:
	        	Log.d(TAG, "Action bar -> toggle lock");
	        	CharSequence toast_text;
	            View v = findViewById(R.id.photograph_buttons);
	        	
	        	// -----------------------------------------------------------
	        	//	Get the TouchView under the PhotographFragment, so that
	        	//	we can notify it about the change in lock status.
	        	// -----------------------------------------------------------	        	
	            FragmentManager fm = getFragmentManager();
	            PhotographFragment photograph_fragment = (PhotographFragment)fm.findFragmentById(R.id.photograph_fragment);
	            Log.d(TAG, String.format(Locale.US, "photograph_fragment: '%s'", photograph_fragment));
	            assert(photograph_fragment != null);
	            TouchView touch_view = photograph_fragment.getTouchView();
	            Log.d(TAG, String.format(Locale.US, "touch_view: '%s'", touch_view));
	            assert(touch_view != null);
	            Handler touch_view_handler = touch_view.getChildHandler();
	            assert(touch_view_handler != null);
	        	// -----------------------------------------------------------	            
	        	
	            // -----------------------------------------------------------
	            //	Toggle the locked status, set up some variables
	            //  required for the subsequent toast.
	            // -----------------------------------------------------------
	        	if (is_photograph_locked.get())
	        	{
	        		Log.d(TAG, "Photograph is currently locked, so unlock.");
	        		is_photograph_locked.set(false);
	        		item.setIcon(R.drawable.icon_unlocked);
	        	    item.setTitle("Lock photo");	
	        	    toast_text = "You have unlocked the photograph";
	        	    touch_view_handler.sendEmptyMessage(touch_view.MSG_TYPE_SET_TO_UNLOCKED);
	        	    v.setVisibility(View.INVISIBLE);
	        	    
	        	    // As we're becoming unlocked set all the toolbar buttons
	        	    // to up again, so that when we re-lock all the buttons
	        	    // are available for use.
	        	    toolbar.setAllToolbarButtonsToUp();
	        	}
	        	else
	        	{
	        		Log.d(TAG, "Photograph is currently unlocked, so lock.");
	        		is_photograph_locked.set(true);
	        		item.setIcon(R.drawable.icon_locked);
	        		item.setTitle("Unlock photo");
	        		toast_text = "You have locked the photograph";
	        		touch_view_handler.sendEmptyMessage(touch_view.MSG_TYPE_SET_TO_LOCKED);
	        		v.setVisibility(View.VISIBLE);	        		
	        	}
	        	// -----------------------------------------------------------
	        	
	        	// -----------------------------------------------------------
	        	//	Show a toast telling the user what they've done.
	        	// -----------------------------------------------------------
	        	Context context = getApplicationContext();
	        	int duration = Toast.LENGTH_SHORT;
	        	Toast toast = Toast.makeText(context, toast_text, duration);
	        	toast.show();
	        	// -----------------------------------------------------------
	        		        	
	            return true;
	
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }

    public Photograph getPhotograph()
    {
        return photograph;
    } // public Photograph getPhotograph()
    
    /**
     * Get the PhotographManager instance for the application.
     * 
     * @return PhotographManager instance.
     */
    public PhotographManager getPhotographManager()
    {
        final String TAG = HEADER_TAG + "::getPhotographManager";
        Log.d(TAG, "Entry");        
        return ((ApplicationContext)getApplication()).getPhotographManager();
    } // public PhotographManager getPhotographManager()    
    
    public void returnToConditionReport()
    {
        final String TAG = HEADER_TAG + "::returnToConditionReport";
        Log.d(TAG, String.format(Locale.US, "Entry."));
        
        Log.d(TAG, "Finish activity");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("photograph_id", photograph_id);
        intent.putExtra("condition_report_id", condition_report_id);
        startActivity(intent);        
    } // public void returnToConditionReport()
    
    public Toolbar getToolbar()
    {
        return toolbar;
    } // public Toolbar getToolbar()

} // public class CameraActivity
