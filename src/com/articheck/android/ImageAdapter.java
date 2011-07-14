package com.articheck.android;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter
{
    static double GOLDEN_RATIO = 1.6180339887498948482045868343656;
    private final String HEADER_TAG = getClass().getName();    
    
    private List<Photograph> photographs;
    private Context context;
    private int gallery_item_background;
    private int  width;
    private int height;
    
    public static class Builder
    {
        // Required parameters.
        private List<Photograph> photographs;
        private Context context;
        
        // Optional parameters.
        private Integer width;
        private Integer height;
        
        public Builder photographs(List<Photograph> photographs)
        {
            this.photographs = photographs;
            return this;
        } // public Builder photographs(List<Photograph> photographs)
        
        public Builder context(Context context)
        {
            this.context = context;
            return this;
        } // public Builder context(Context context)
        
        public Builder width(int width)
        {
            this.width = width;
            return this;
        }
        
        public Builder height(int height)
        {
            this.height = height;
            return this;
        }
        
        public ImageAdapter build()
        {            
            // -----------------------------------------------------------------
            //  Set up the width and height of the image view.  Use the
            //  golden ratio if only one dimension is specified, else
            //  use both width and height specifications.
            // -----------------------------------------------------------------            
            if ((width == null) && (height == null))
            {
                throw new IllegalStateException("Either width or height must be non-null.");
            } // if ((width == null) && (height == null))
            
            if ((width != null) && (height == null))
            {
                height = (int) (width / GOLDEN_RATIO);
            } // if ((width != null) && (height == null))
            else if ((width == null) && (height != null))
            {
                width = (int) (height * GOLDEN_RATIO);
            }
            assert(width != null);
            assert(height != null);
            // -----------------------------------------------------------------            
            
            return new ImageAdapter(this);
        } // public ImageAdapter build()
    } // public static class Builder
    
    private ImageAdapter(Builder builder)
    {
        this.photographs = builder.photographs;
        this.context = builder.context;
        this.width = builder.width;
        this.height = builder.height;
        initialize();
    }
    
    private void initialize()
    {
        TypedArray a = context.obtainStyledAttributes(R.styleable.Gallery);
        gallery_item_background = a.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0);
        a.recycle();
    }    

    public int getCount()
    {
        return photographs.size();
    }

    public Object getItem(int position)
    {
        return photographs.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final String TAG = HEADER_TAG + "::getView";
        Log.d(TAG, String.format(Locale.US, "Entry. position: '%s'", position));
        ImageView i;
        if (convertView == null)
        {
            Log.d(TAG, "Old view not present, so re-generate.");        
            i = new ImageView(context);
            i.setLayoutParams(new Gallery.LayoutParams(width, height));
            i.setScaleType(ImageView.ScaleType.FIT_CENTER);
            i.setBackgroundResource(gallery_item_background);            
        }
        else
        {
            Log.d(TAG, "Create view from old view.");
            i = (ImageView) convertView;
        } // if (convertView == null)

        Photograph photograph = photographs.get(position);
        Bitmap bitmap = photograph.getBitmap(width, height);
        Log.d(TAG, String.format(Locale.US, "Bitmap is: '%s', width: '%s', height: '%s'", bitmap, bitmap.getWidth(), bitmap.getHeight()));
        i.setImageBitmap(bitmap);
        
        return i;
    } // public View getView(int position, View convertView, ViewGroup parent)
    
} // public class ImageAdapter extends BaseAdapter
