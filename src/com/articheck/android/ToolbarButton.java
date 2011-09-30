package com.articheck.android;

import java.util.Locale;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * @author ai
 *
 * Represents a button in the toolbar, along with the drawable that holds the
 * actual symbol that gets drawn onto the photograph.
 * 
 * !!AI TODO This class terrifies me. Why does the drawable for the symbol
 * live here? Please move it somewhere else.
 * 
 * !!AI TODO Either represent the drawables for the button state differently
 * or document thoroughly.
 *
 */
public class ToolbarButton
{
    
    /**
     * @author ai
     *
     * A toolbar button can either be a symbol or a color button.
     * 
     * A symbol button can be drawn onto a photograph, and hence requires
     * a symbol bitmap.
     * 
     * A color button cannot be drawn onto a photograph, and is treated
     * quite differently in the internals of Toolbar.  To determine these
     * differences do a search for ToolbarButton.Type.
     */
    public enum Type { SYMBOL, COLOR };
    
    private final String HEADER_TAG;    
    
    private String name;    
    
    private int drawable_up_enabled_id;    
    private int bitmap_up_enabled_width;
    private int bitmap_up_enabled_height;
    private int bitmap_up_enabled_resized_width;
    private int bitmap_up_enabled_resized_height;
    
    private int drawable_down_enabled_id;    
    private int bitmap_down_enabled_width;
    private int bitmap_down_enabled_height;
    private int bitmap_down_enabled_resized_width;
    private int bitmap_down_enabled_resized_height;    
    
    private int drawable_symbol_id;
    private int bitmap_symbol_width;
    private int bitmap_symbol_height;
    private int bitmap_symbol_resized_width;
    private int bitmap_symbol_resized_height;
    
    private Resources resources;    
    
    private boolean is_up;
    
    private Type type;
    
    public static final class Builder
    {
        // Required parameters
        private Integer drawable_up_enabled_id;
        private Integer bitmap_up_enabled_resized_width;
        private Integer bitmap_up_enabled_resized_height;
        
        private Integer drawable_down_enabled_id;
        private Integer bitmap_down_enabled_resized_width;
        private Integer bitmap_down_enabled_resized_height;        
        
        private Integer drawable_symbol_id;
        private Integer bitmap_symbol_resized_width;
        private Integer bitmap_symbol_resized_height;
        
        private String name;
        private Resources resources;
        private Type type;
        
        public Builder name(String name)
        {
            this.name = name;
            return this;
        } // public Builder name(String name)
        
        public Builder drawableUpEnabledId(int drawable_up_enabled_id)
        {
            this.drawable_up_enabled_id = drawable_up_enabled_id;
            return this;
        } // public Builder drawableUpEnabledId(int drawable_up_enabled_id)
        
        public Builder drawableDownEnabledId(int drawable_down_enabled_id)
        {
            this.drawable_down_enabled_id = drawable_down_enabled_id;
            return this;
        } // public Builder drawableDownEnabledId(int drawable_down_enabled_id)        
        
        
        public Builder drawableSymbolId(int drawable_symbol_id)
        {
            this.drawable_symbol_id = drawable_symbol_id;
            return this;
        } // public Builder drawableSymbolId(int drawable_symbol_id)     
        
        public Builder bitmapUpEnabledResizedWidth(int bitmap_up_enabled_resized_width)
        {
        	this.bitmap_up_enabled_resized_width = bitmap_up_enabled_resized_width;
        	return this;
        }
        
        public Builder bitmapUpEnabledResizedHeight(int bitmap_up_enabled_resized_height)
        {
        	this.bitmap_up_enabled_resized_height = bitmap_up_enabled_resized_height;
        	return this;
        }        
        
        public Builder bitmapDownEnabledResizedWidth(int bitmap_down_enabled_resized_width)
        {
            this.bitmap_down_enabled_resized_width = bitmap_down_enabled_resized_width;
            return this;
        }
        
        public Builder bitmapDownEnabledResizedHeight(int bitmap_down_enabled_resized_height)
        {
            this.bitmap_down_enabled_resized_height = bitmap_down_enabled_resized_height;
            return this;
        }        

        public Builder bitmapSymbolResizedWidth(int bitmap_symbol_resized_width)
        {
        	this.bitmap_symbol_resized_width = bitmap_symbol_resized_width;
        	return this;
        }
        
        public Builder bitmapSymbolResizedHeight(int bitmap_symbol_resized_height)
        {
        	this.bitmap_symbol_resized_height = bitmap_symbol_resized_height;
        	return this;
        }                
        
        public Builder resources(Resources resources)
        {
            this.resources = resources;
            return this;
        } // public Builder resources(Resources resources)
        
        public Builder type(Type type)
        {
            this.type = type;
            return this;
        } // public Builder type(Type type)
        
        public ToolbarButton build()
        {
            // -----------------------------------------------------------------
            //  Validate the parameters passed into the builder.
            // -----------------------------------------------------------------
            if (name == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "name must not be null."));
            } // if (name == null)
            if (drawable_up_enabled_id == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "drawable_up_enabled_id must not be null."));
            } // if (drawable_up_enabled_id == null)            
            if (drawable_down_enabled_id == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "drawable_down_enabled_id must not be null."));
            } // if (drawable_down_enabled_id == null)            
            if (resources == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "resources must not be null."));
            } // if (resources == null)
            if (bitmap_up_enabled_resized_height == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "bitmap_up_enabled_resized_height must not be null."));
            } // if (bitmap_up_enabled_resized_height == null)
            if (bitmap_up_enabled_resized_width == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "bitmap_up_enabled_resized_width must not be null."));
            } // if (bitmap_up_enabled_resized_width == null)            
            if (bitmap_down_enabled_resized_height == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "bitmap_down_enabled_resized_height must not be null."));
            } // if (bitmap_down_enabled_resized_height == null)
            if (bitmap_down_enabled_resized_width == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "bitmap_down_enabled_resized_width must not be null."));
            } // if (bitmap_down_enabled_resized_width == null)
            
            if (type.equals(null))
            {
                throw new IllegalStateException(String.format(Locale.US, "type must not be null."));
            } // if (type == null)
            
            // If this is a toolbar button for a symbol then we need to make
            // sure that the caller has passed in the information required to
            // draw the symbol on the photograph.
            if (type.equals(ToolbarButton.Type.SYMBOL))
            {
                if (drawable_symbol_id == null)
                {
                    throw new IllegalStateException(String.format(Locale.US, "drawable_symbol_id must not be null."));
                } // if (drawable_symbol_id == null)            
                if (bitmap_symbol_resized_height == null)
                {
                    throw new IllegalStateException(String.format(Locale.US, "bitmap_symbol_resized_height must not be null."));
                } // if (bitmap_symbol_resized_height == null)
                if (bitmap_symbol_resized_width == null)
                {
                    throw new IllegalStateException(String.format(Locale.US, "bitmap_symbol_resized_width must not be null."));
                } // if (bitmap_symbol_resized_width == null)                
            } // if (type == ToolbarButton.Type.SYMBOL)            
            // -----------------------------------------------------------------
            
            return new ToolbarButton(this);
        } // public ToolbarButton build()
    } // private static final class Builder
    
    ToolbarButton(Builder builder)
    {
        this.drawable_up_enabled_id = builder.drawable_up_enabled_id;
        this.bitmap_up_enabled_resized_height = builder.bitmap_up_enabled_resized_height;
        this.bitmap_up_enabled_resized_width = builder.bitmap_up_enabled_resized_width;
        
        this.drawable_down_enabled_id = builder.drawable_down_enabled_id;
        this.bitmap_down_enabled_resized_height = builder.bitmap_down_enabled_resized_height;
        this.bitmap_down_enabled_resized_width = builder.bitmap_down_enabled_resized_width;        

        this.type = builder.type;
        if (this.type == ToolbarButton.Type.SYMBOL)
        {
            this.drawable_symbol_id = builder.drawable_symbol_id;
            this.bitmap_symbol_resized_height = builder.bitmap_symbol_resized_height;
            this.bitmap_symbol_resized_width = builder.bitmap_symbol_resized_width;            
        } // if (this.type == ToolbarButton.Type.SYMBOL)
        
        this.name = builder.name;
        this.resources = builder.resources;
        
        this.is_up = true;
        
        HEADER_TAG = String.format(Locale.US, "%s [%s]", getClass().getName(), this.name);
        initialize();
    } // ToolbarButton(Builder builder)
    
    public int getBitmapSymbolWidth()
    {
        return this.bitmap_symbol_width;
    }
    
    public int getBitmapSymbolHeight()
    {
        return this.bitmap_symbol_height;
    }
    
    private void initialize()
    {
        final String TAG = HEADER_TAG + "::initialize()";
        Log.d(TAG, "Entry.");        
        
        BitmapFactory.Options bounds;
        
        // ---------------------------------------------------------------------
        //  Determine the dimensions of the button drawables.
        // ---------------------------------------------------------------------        
        bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, drawable_up_enabled_id, bounds);        
        bitmap_up_enabled_width = bounds.outWidth;
        bitmap_up_enabled_height = bounds.outHeight;
        Log.d(TAG, String.format(Locale.US, "bitmap_up_enabled. width: '%s', height: '%s'", bitmap_up_enabled_width, bitmap_up_enabled_height));
        
        bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, drawable_down_enabled_id, bounds);        
        bitmap_down_enabled_width = bounds.outWidth;
        bitmap_down_enabled_height = bounds.outHeight;
        Log.d(TAG, String.format(Locale.US, "bitmap_down_enabled. width: '%s', height: '%s'", bitmap_down_enabled_width, bitmap_down_enabled_height));        
        // ---------------------------------------------------------------------        
        
        // ---------------------------------------------------------------------
        //  Determine the dimensions of the button's symbol's drawable.
        // --------------------------------------------------------------------- 
        if (type.equals(ToolbarButton.Type.SYMBOL))
        {
            bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(resources, drawable_symbol_id, bounds);        
            bitmap_symbol_width = bounds.outWidth;
            bitmap_symbol_height = bounds.outHeight;
            Log.d(TAG, String.format(Locale.US, "bitmap_symbol. width: '%s', height: '%s'", bitmap_symbol_width, bitmap_symbol_height));            
        } // if (type == ToolbarButton.Type.SYMBOL)
        // ---------------------------------------------------------------------        
    } // private void initialize()
    
    public Bitmap getBitmapUpEnabled()
    {
        return getScaledBitmap(this.bitmap_up_enabled_resized_width,
                                this.bitmap_up_enabled_resized_height,
                                bitmap_up_enabled_width,
                                bitmap_up_enabled_height,
                                drawable_up_enabled_id);
    } // public Bitmap getBitmapUpEnabled()
    
    public Bitmap getBitmapDownEnabled()
    {
        return getScaledBitmap(this.bitmap_down_enabled_resized_width,
                                this.bitmap_down_enabled_resized_height,
                                bitmap_down_enabled_width,
                                bitmap_down_enabled_height,
                                drawable_down_enabled_id);
    } // public Bitmap getBitmapDownEnabled()    
    
    public Bitmap getBitmapSymbol(Float scale_factor)
    {
        //final String TAG = HEADER_TAG + "::getBitmapSymbol";
        //Log.d(TAG, String.format(Locale.US, "Entry. scale_factor: '%s'", scale_factor));
        Preconditions.checkState(type.equals(ToolbarButton.Type.SYMBOL), "ToolbarButton type is not symbol!");        
        
        int resized_width = this.bitmap_symbol_resized_width;
        int resized_height = this.bitmap_symbol_resized_height;
        if (scale_factor != null)
        {
            resized_width = (int) (resized_width * scale_factor);
            resized_height = (int) (resized_height * scale_factor);
        } // if (scale_factor != null)        
        return getScaledBitmap(resized_width,
                                resized_height,
                                bitmap_symbol_width,
                                bitmap_symbol_height,
                                drawable_symbol_id);
        // ---------------------------------------------------------------------        
    } // public Bitmap getBitmapUpEnabled(int final_width, int final_height)
    
    public Drawable getSymbolBitmapDrawable(Float scale_factor)
    {
        Preconditions.checkState(type.equals(ToolbarButton.Type.SYMBOL), "ToolbarButton type is not symbol!");
        return new BitmapDrawable(getBitmapSymbol(scale_factor));
    } // public Drawable getBitmapDrawable()    
    
    private Bitmap getScaledBitmap(int final_width,
                                      int final_height,
                                      int bitmap_width,
                                      int bitmap_height,
                                      int drawable_id)
    {
        BitmapFactory.Options resample = new BitmapFactory.Options();        
        int sample_size = 1;
        if ((final_width <= bitmap_width) && (final_height <= bitmap_height))
        {
            resample.inSampleSize = sample_size;
        }
        else
        {
            int sample_size_height = (final_height / bitmap_width);
            int sample_size_width = (final_width / bitmap_height);
            resample.inSampleSize = (sample_size_height > sample_size_width) ? sample_size_height : sample_size_width;           
        } // if ((final_width <= bitmap_up_enabled_width) && (final_height <= bitmap_up_enabled_height))        
        resample.inScaled = false;
        resample.inDither = false;        
        Bitmap resized_image = BitmapFactory.decodeResource(resources, drawable_id, resample);
        return resized_image;        
    } // private Bitmap getScaledBitmap(int final_width, int final_height, int bitmap_width, int bitmap_height, int drawable_id)
    
    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)                       
                       .add("name", name)
                       .add("drawable_up_enabled_id", drawable_up_enabled_id)
                       .add("drawable_down_enabled_id", drawable_down_enabled_id)
                       .add("type", type)
                       .toString();
    } // public String toString()
    
    @Override
    public int hashCode()
    {
        return Objects.hashCode(name,
                                 drawable_up_enabled_id,
                                 drawable_down_enabled_id,
                                 type);
    } // public int hashCode()
    
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        } // if (o == this)
        if (!(o instanceof ToolbarButton))
        {
            return false;
        } // if (!(o instanceof ToolbarButton))
        ToolbarButton tb = (ToolbarButton)o;
        boolean result = (Objects.equal(name, tb.name)
                           && Objects.equal(drawable_up_enabled_id, tb.drawable_up_enabled_id)
                           && Objects.equal(drawable_down_enabled_id, tb.drawable_down_enabled_id)
                           && Objects.equal(type, tb.type));
        return result;
    } // public boolean equals(Object o)    
    
    public String getName()
    {
        return name;
    } // public String getName()
    
    public void setToUp()
    {
        this.is_up = true;
    }
    
    public void setToDown()
    {
        this.is_up = false;
    }
    
    public boolean isUp()
    {
        return (this.is_up == true);
    }
    
    public boolean isDown()
    {
        return (this.is_up == false);
    }
    
    public ToolbarButton.Type getType()
    {
        return this.type;
    }

}
