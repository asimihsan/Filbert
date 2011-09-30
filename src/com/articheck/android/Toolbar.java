package com.articheck.android;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.articheck.android.fragments.PhotographFragment;
import com.articheck.android.utilities.ToolbarButtonNameComparator;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Toolbar
implements OnClickListener
{
    private final String HEADER_TAG = getClass().getName();    

    private BiMap<ToolbarButton, ImageButton> lookup_symbol_toolbar_button_to_image_button;
    private BiMap<ToolbarButton, ImageButton> lookup_color_toolbar_button_to_image_button;
    private BiMap<ToolbarButton, ImageButton> lookup_any_toolbar_button_to_image_button;
    
    int button_width;
    int button_height;
    private Activity activity;

	private TouchView touch_view;	

	private ToolbarButton last_clicked;
	private ToolbarButton current_color;
    
    public static final class Builder
    {
        // Required parameters        
        Integer button_width;
        Integer button_height;
        Activity activity;
        
        public Builder buttonWidth(int button_width)
        {
            this.button_width = button_width;
            return this;
        }
        public Builder buttonHeight(int button_height)
        {
            this.button_height = button_height;
            return this;
        }        
        public Builder activity(Activity activity)
        {
            this.activity = activity;
            return this;
        }
        public Toolbar build()
        {
            // -----------------------------------------------------------------
            //  Validate the builder's parameters.
            // -----------------------------------------------------------------
            if (button_width == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "button_width must not be null."));
            } // if (button_width == null)            
            if (button_height == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "button_height must not be null."));
            } // if (button_height == null)            
            if (activity == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "activity must not be null."));
            } // if (activity == null)            
            return new Toolbar(this);
            // -----------------------------------------------------------------
        } // public Toolbar build()
    } // public static final class Builder
    
    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                       .add("button_width", button_width)
                       .add("button_height", button_height)
                       .toString();
    } // public String toString()
    
    Toolbar(Builder builder)
    {
        this.button_height = builder.button_height;
        this.button_width = builder.button_width;
        this.activity = builder.activity;
        initialize();
    } // Toolbar(Builder builder)
    
    private void initialize()
    {        
        this.lookup_symbol_toolbar_button_to_image_button = HashBiMap.create();
        this.lookup_color_toolbar_button_to_image_button = HashBiMap.create();
        this.lookup_any_toolbar_button_to_image_button = HashBiMap.create();
        this.last_clicked = null;
    }
    
    public void addToolbarButton(ToolbarButton toolbar_button)
    {        
        // ---------------------------------------------------------------------
        //  Validate assumptions.
        // ---------------------------------------------------------------------        
        Preconditions.checkArgument((toolbar_button != null), "toolbar_button is null!");
        
        // Right now we can only handle color or symbol buttons. If this button
        // is neither then crash because this is very unexpected.
        Preconditions.checkArgument((toolbar_button.getType() == ToolbarButton.Type.COLOR) ||
                                    (toolbar_button.getType() == ToolbarButton.Type.SYMBOL),
                                    "ToolbarButton is not color or symbol!");
        
        // ---------------------------------------------------------------------
        
        // ---------------------------------------------------------------------
        //  Create a new ImageButton instance that wraps around the bitmap
        //  of the toolbar button.
        // ---------------------------------------------------------------------        
        ImageButton image_button = new ImageButton(activity);
        image_button.setAdjustViewBounds(true);
        image_button.setMaxHeight(button_height);
        image_button.setMaxWidth(button_width);
        image_button.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Bitmap bitmap = toolbar_button.getBitmapUpEnabled();
        image_button.setImageBitmap(bitmap);
        image_button.setOnClickListener(this);
        
        // ---------------------------------------------------------------------
        //  Populate the lookup between toolbar buttons and their image
        //  buttons.
        // ---------------------------------------------------------------------
        lookup_any_toolbar_button_to_image_button.put(toolbar_button, image_button);
        switch(toolbar_button.getType())
        {
            case COLOR:
                lookup_color_toolbar_button_to_image_button.put(toolbar_button, image_button);                
                break;
                
            case SYMBOL:
                lookup_symbol_toolbar_button_to_image_button.put(toolbar_button, image_button);
                break;
        } // switch(toolbar_button.getType())
        
        // ---------------------------------------------------------------------
    } // public void addToolbarButton(ToolbarButton toolbar_button)    
    
    public void setToolbarButtonByNameToUpEnabled(String name)
    {
        ToolbarButton toolbar_button = getToolbarButtonByName(name);        
        ImageButton existing_image_button = lookup_any_toolbar_button_to_image_button.get(toolbar_button);
        Bitmap bitmap_up_enabled = toolbar_button.getBitmapUpEnabled();
        existing_image_button.setImageBitmap(bitmap_up_enabled);
    } // public void setToolbarButtonByNameToUpEnabled(String name)
    
    public void setToolbarButtonByNameToDownEnabled(String name)
    {
        ToolbarButton toolbar_button = getToolbarButtonByName(name);
        ImageButton existing_image_button = lookup_any_toolbar_button_to_image_button.get(toolbar_button);
        Bitmap bitmap_down_enabled = toolbar_button.getBitmapDownEnabled();
        existing_image_button.setImageBitmap(bitmap_down_enabled);
    } // public void setToolbarButtonByNameToDownEnabled(String name)    
    
    public List<ToolbarButton> getToolbarButtons(ToolbarButton.Type type)
    {
        // ---------------------------------------------------------------------
        //  Validate assumptions.
        // ---------------------------------------------------------------------        
        Preconditions.checkArgument((type == ToolbarButton.Type.COLOR) ||
                                    (type == ToolbarButton.Type.SYMBOL),
                                    "type is not color or symbol!");
        // ---------------------------------------------------------------------
        
        List<ToolbarButton> result = null;
        switch(type)
        {
            case COLOR:
                result = Lists.newArrayList(lookup_color_toolbar_button_to_image_button.keySet());
                break;
            case SYMBOL:
                result = Lists.newArrayList(lookup_symbol_toolbar_button_to_image_button.keySet());
                break;
        } // switch(type)
        assert(result != null);
        Collections.sort(result, new ToolbarButtonNameComparator());        
        return Collections.unmodifiableList(result);
    } // public void getToolbarButtonsIterator()

    public ToolbarButton getToolbarButtonByName(String name)
    {
        ToolbarButton return_value = null;        
        for (ToolbarButton toolbar_button : lookup_any_toolbar_button_to_image_button.keySet())
        {
            if (Objects.equal(toolbar_button.getName(), name))
            {
                return_value = toolbar_button;
                break;
            } // if (Objects.equal(toolbar_button.getName(), name))
        } // for (ToolbarButton toolbar_button : toolbar_buttons)
        return return_value;        
    } // public ToolbarButton getToolbarButtonByName(String name)
    
    public ImageButton getImageButtonByToolbarButton(ToolbarButton toolbar_button)
    {
        ImageButton return_value = null;
        switch(toolbar_button.getType())
        {
            case COLOR:
                return_value = lookup_color_toolbar_button_to_image_button.get(toolbar_button);                
                break;
                
            case SYMBOL:
                return_value = lookup_symbol_toolbar_button_to_image_button.get(toolbar_button);
                break;
        } // switch(type)
        return return_value;
    } // public ImageButton getImageButtonByToolbarButton(ToolbarButton toolbar_button)
    
    public void disableAllToolbarButtons()
    {
        for (ImageButton image_button : lookup_any_toolbar_button_to_image_button.inverse().keySet())
        {
            image_button.setEnabled(false);
        } //for (ImageButton image_button : image_buttons)
    } // public void disableAllToolbarButtons()
    
    public void enableAllToolbarButtons()
    {
        for (ImageButton image_button : lookup_any_toolbar_button_to_image_button.inverse().keySet())
        {
            image_button.setEnabled(true);
        } //for (ImageButton image_button : image_buttons)
    } // public void enableAllToolbarButtons()
    
    public void setAllToolbarButtonsToUp()
    {        
        for (ToolbarButton toolbar_button : lookup_any_toolbar_button_to_image_button.keySet())
        {
            // !!AI TODO yucky, optimise me. Why do two lookups?            
            setToolbarButtonByNameToUpEnabled(toolbar_button.getName());
            toolbar_button.setToUp();
        }
    } // public void setAllToolbarButtonsToUp()

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * 
	 * We assume that the toolbar is only visible, and hence clickable, if the
	 * user has locked the photograph.
	 * 
	 * If we click on a symbol button whereas previously nothing has been
	 * done then depress the symbol button and allow the user to start drawing.
	 * 
	 * If we click on a symbol button while the user was drawing a symbol
	 * on the photograph, and the symbol button we've clicked is the same
	 * as the one being drawn, we de-select the symbol button and the user
	 * will no longer be able to edit the previous symbol.
	 * 
	 * If we click on a symbol button while the user was drawing a symbol
	 * on the photograph, and the symbol button we've clicked is different
	 * from the one being drawn, we de-select the old symbol button and select
	 * the new new symbol button.  The old symbol is no longer editable and
	 * the new symbol is now drawable on the photograph.
	 * 
	 * If we click on the color button and we're not currently drawing a symbol
	 * on the screen then cycle the currently active color and send
	 * a message to the TouchView telling it that this has happened.  It's up
	 * to the TouchView to decide whether to subsequently alter the color
	 * of a symbol if one is currently being drawn. 
	 * 
	 */
	public void onClick(View v)
	{
		final String TAG = HEADER_TAG + "::onClick";
		Log.d(TAG, String.format(Locale.US, "Entry. View: '%s'", v));		
		
		BiMap<ImageButton, ToolbarButton> image_buttons_to_any_toolbar_buttons = lookup_any_toolbar_button_to_image_button.inverse();		
		ToolbarButton toolbar_button = null;		
		if (image_buttons_to_any_toolbar_buttons.keySet().contains(v))
		{
		    toolbar_button = image_buttons_to_any_toolbar_buttons.get(v);
		}
		assert(toolbar_button != null);
		Log.d(TAG, String.format(Locale.US, "Clicked: '%s'", toolbar_button));				
		
		switch(toolbar_button.getType())
		{
		    case COLOR:
		        Log.d(TAG, "This is a color toolbar button.");
		        cycleCurrentColor();
		        updateColorButton();
		        break;
		        
		    case SYMBOL:
		        Log.d(TAG, "This is a symbol toolbar button.");
		        if (toolbar_button.isUp())
		        {
		            Log.d(TAG, "toolbar button is up, so set to down.");            
		            setToolbarButtonByNameToDownEnabled(toolbar_button.getName());
		            setLastClicked(toolbar_button);         
		        }
		        else
		        {
		            Log.d(TAG, "toolbar button is down, so set to up.");            
		            setToolbarButtonByNameToUpEnabled(toolbar_button.getName());
		            clearLastClicked();
		        } // if (toolbar_button.isUp())		        
		        break;
		        
		    default:
		        Log.e(TAG, String.format(Locale.US, "Unknown toolbar button type: '%s'", toolbar_button.getType()));
		        return;		            
		} // switch(toolbar_button.getType())
		
	} // public void onClick(View v)
	
	private void cycleCurrentColor()
	{
	    List<ToolbarButton> color_buttons = Lists.newArrayList(lookup_color_toolbar_button_to_image_button.keySet());
	    int location_of_color = color_buttons.indexOf(current_color);
	    int size = color_buttons.size();
	    int new_location = (location_of_color + 1) % size;
	    current_color = color_buttons.get(new_location);	    
	} // private void cycleCurrentColor()
	
	/**
	 * We assume the first button in the toolbar is the color button, remove
	 * it, and replace it with the image button corresponding to the new color.
	 * 
	 * !!AI This is the mother of all hacks; this better get covered in the
	 * technical documentation.
	 */
	private void updateColorButton()
	{
        LinearLayout button_layout = (LinearLayout)activity.findViewById(R.id.photograph_buttons);
        button_layout.removeViewAt(0);
        ImageButton new_color_button = lookup_color_toolbar_button_to_image_button.get(current_color);
        button_layout.addView(new_color_button, 0);       
        
        // Tell the touch view we've just selected a color button. If we're
        // editing a symbol at the time we'll change its color.
        Handler handler_touch_view = this.touch_view.getChildHandler();
        handler_touch_view.sendEmptyMessage(TouchView.MSG_TYPE_COLOR_TOOLBAR_BUTTON_JUST_CLICKED);        
	} // private void updateColorButton()
	
	public ToolbarButton getCurrentColor()
	{
	    return this.current_color;
	} // public Toolbarbutton getCurrentColor()
	
	public void setCurrentColor(ToolbarButton current_color)
	{
	    this.current_color = current_color;
	} // public void setCurrentColor(ToolbarButton current_color)
	
	public ToolbarButton getLastClicked()
	{
		return this.last_clicked;
	}
	
	public void clearLastClicked()
	{
	    if (this.last_clicked != null)
	    {
	        this.last_clicked.setToUp();
	        this.last_clicked = null;	        
	    }
	}
	
	public void setLastClicked(ToolbarButton clicked_toolbar_button)
	{
	    // ---------------------------------------------------------------------
	    // Check assumptions.
        // ---------------------------------------------------------------------	    
	    Preconditions.checkNotNull(clicked_toolbar_button);	    
        // ---------------------------------------------------------------------	    
	    
	    if (this.last_clicked != null)
	    {
	        setToolbarButtonByNameToUpEnabled(this.last_clicked.getName());
	        this.last_clicked.setToUp();
	    } // if (this.last_clicked != null)
	    this.last_clicked = clicked_toolbar_button;
	    clicked_toolbar_button.setToDown();
	    
	    // Tell the touch view we've just selected a button.  If we were drawing
	    // a symbol at the time we'll no longer edit this symbol, and we'll
	    // start drawing a new one instead.
	    Handler handler_touch_view = this.touch_view.getChildHandler();
	    handler_touch_view.sendEmptyMessage(TouchView.MSG_TYPE_SYMBOL_TOOLBAR_BUTTON_JUST_CLICKED);
	}

	public void setTouchView(TouchView touch_view)
	{
		final String TAG = HEADER_TAG + "::setTouchView";
		Log.d(TAG, String.format(Locale.US, "Entry. Old touch_view: '%s', new touch_view: '%s'", this.touch_view, touch_view));
		this.touch_view = touch_view;
		assert(this.touch_view != null);
	}
	
	public ToolbarButton getLastClickedToolbarButton()
	{
		return last_clicked;
	}
    
} // class Toolbar