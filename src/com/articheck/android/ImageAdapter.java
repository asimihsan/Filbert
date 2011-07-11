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
    private final String HEADER_TAG = getClass().getName();
    
    private List<Photograph> photographs;
    private Context context;
    private int gallery_item_background;
    
    public static class Builder
    {
        private List<Photograph> photographs;
        private Context context;
        
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
        
        public ImageAdapter build()
        {
            return new ImageAdapter(this);
        } // public ImageAdapter build()
    } // public static class Builder
    
    private ImageAdapter(Builder builder)
    {
        this.photographs = builder.photographs;
        this.context = builder.context;
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
        return photographs.get(position).getBitmap();
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final String TAG = HEADER_TAG + "::getView";
        Log.d(TAG, String.format(Locale.US, "Entry. position: '%s'", position));
        if (convertView != null)
        {
            Log.d(TAG, "Old view is present, do not re-generate.");
            return convertView;
        } // if (convertView != null)
        
        ImageView i = new ImageView(context);
        Log.d(TAG, "Create view from scratch");
        Photograph photograph = photographs.get(position);
        Bitmap bitmap = photograph.getBitmap();
        Log.d(TAG, String.format(Locale.US, "Bitmap is: '%s', width: '%s', height: '%s'", bitmap, bitmap.getWidth(), bitmap.getHeight()));
        i.setImageBitmap(bitmap);
        i.setLayoutParams(new Gallery.LayoutParams(300, 200));
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        i.setBackgroundResource(gallery_item_background);            
        
        return i;
    } // public View getView(int position, View convertView, ViewGroup parent)
    
} // public class ImageAdapter extends BaseAdapter
