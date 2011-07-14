package com.articheck.android;

import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.common.base.Objects;

/**
 * Photograph that is associated with a condition report.
 * 
 * @author ai
 *
 */
public class Photograph
{
    private final String HEADER_TAG = getClass().getName();
    
    private String photograph_id;
    private String condition_report_id;
    private String hash;
    private String local_path;    
    
    public static final class Builder
    {
        // Required parameters.
        private String photograph_id;
        private String condition_report_id;
        private String local_path;
        
        // Optional parameters.
        private String hash;
        
        public Builder photographId(String photograph_id)
        {
            this.photograph_id = photograph_id;
            return this;
        } // public Builder photographId(String photograph_id)
        
        public Builder conditionReportId(String condition_report_id)
        {
            this.condition_report_id = condition_report_id;
            return this;
        } // public Builder conditionReportId(String condition_report_id)
        
        public Builder localPath(String local_path)
        {
            this.local_path = local_path;
            return this;
        } // public Builder localPath(String local_path)
        
        public Builder hash(String hash)
        {
            this.hash = hash;
            return this;
        } // public Builder hash(String hash)
        
        public Photograph build()
        {
            // -----------------------------------------------------------------
            //  Validate required inputs are not null.
            // -----------------------------------------------------------------            
            if (photograph_id == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "photograph_id must not be null."));
            } // if (photograph_id == null)
            if (condition_report_id == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "condition_report_id must not be null."));
            } // if (condition_report_id == null)
            if (local_path == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "local_path must not be null."));
            } // if (local_path == null)
            // -----------------------------------------------------------------    
            
            return new Photograph(this);
        } // public Photograph build()       
        
    } // public static final class Builder
    
    private Photograph(Builder photograph)
    {
        this.photograph_id = photograph.photograph_id;
        this.condition_report_id = photograph.condition_report_id;
        this.hash = photograph.hash;
        this.local_path = photograph.local_path;
        initialize();
    } // private Photograph(Builder photograph)
    
    private void initialize()
    {
        final String TAG = HEADER_TAG + "::initialize";
        Log.d(TAG, "Entry.");        
        
        // ---------------------------------------------------------------------
        //  TODO Calculate the hash, if it isn't already calculated.
        // ---------------------------------------------------------------------        
        if (hash == null)
        {
            Log.d(TAG, "hash i null, so calculate it from scratch.");
            hash = "merry merry hash!";
        } // if (hash == null)
        // ---------------------------------------------------------------------        
    } // private void initialize()
    
    @Override 
    public String toString()
    {
        return Objects.toStringHelper(this)
                       .add("photograph_id", photograph_id)
                       .add("condition_report_id", condition_report_id)
                       .add("hash", hash)
                       .add("local_path", local_path)
                       .toString();        
    } // public String toString()
    
    @Override public int hashCode()
    {
        return Objects.hashCode(photograph_id,
                                 condition_report_id,
                                 hash,
                                 local_path);
    } // @Override public int hashCode()
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        } // if (o == this)
        if (!(o instanceof Photograph))
        {
            return false;
        } // if (!(o instanceof Photograph))
        Photograph photograph = (Photograph)o;
        boolean result = (Objects.equal(photograph_id, photograph.photograph_id) &&
                           Objects.equal(condition_report_id, photograph.condition_report_id) &&
                           Objects.equal(hash, photograph.hash) &&
                           Objects.equal(local_path, photograph.local_path));
        return result;
    } // public boolean equals(Object o)
    
    public String getPhotographId()
    {
        return photograph_id;
    }

    public String getConditionReportId()
    {
        return condition_report_id;
    }

    public String getHash()
    {
        return hash;
    }

    public String getLocalPath()
    {
        return local_path;
    }    
    
    /**
     * Get a bitmap from the photograph on the SD card. 
     * 
     * You must scale the image before attempting to load it into an ImageView
     * or else when hardware acceleration is enabled OpenGL will warn you
     * that the imqge doesn't fit.
     * 
     * Reference:
     * 
     * http://stackoverflow.com/questions/2641726/decoding-bitmaps-in-android-with-the-right-size
     * 
     * @param width
     * @param height
     * @return
     */
    public Bitmap getBitmap(int width, int height)
    {
        final String TAG = HEADER_TAG + "::getBitmap";
        Log.d(TAG, String.format(Locale.US, "Entry. width: '%s', height: '%s'", width, height));
        
        // ---------------------------------------------------------------------
        //  Get the dimensions without loading the image into memory.
        // ---------------------------------------------------------------------
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(local_path, bounds);
        if (bounds.outWidth == -1)
        {
            Log.e(TAG, String.format(Locale.US, "Error while decoding image for photograph: '%s'", this));
            return null;
        } // if (bounds.outWidth == -1)
        int real_width = bounds.outWidth;
        int real_height = bounds.outHeight;
        // ---------------------------------------------------------------------        

        BitmapFactory.Options resample = new BitmapFactory.Options();
        int sample_size = 1;
        if ((real_width <= width) && (real_height <= height))
        {
            resample.inSampleSize = sample_size;
        }
        else
        {
            int sample_size_height = (real_height / height);
            int sample_size_width = (real_width / width);
            resample.inSampleSize = (sample_size_height > sample_size_width) ? sample_size_height : sample_size_width;           
        } // if ((real_width <= width) && (real_height <= height))        
        Log.d(TAG, String.format(Locale.US, "Sample size is: '%s'", resample.inSampleSize));
        Bitmap return_value = BitmapFactory.decodeFile(local_path, resample);
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));            
        return return_value;
    } // public Bitmap getBitmap()    
    
    public Bitmap getBitmap()
    {
        final String TAG = HEADER_TAG + "::getBitmap";
        Log.d(TAG, "Entry.");
        
        // ---------------------------------------------------------------------
        //  TODO OpenGL library silently rejects attempts to load massive
        //  images, defensively downsize images straight from the camera for
        //  now, but eventually find out what exact size triggers the error.
        // ---------------------------------------------------------------------        
        BitmapFactory.Options resample = new BitmapFactory.Options();
        resample.inSampleSize = 2;
        Bitmap return_value = BitmapFactory.decodeFile(local_path, resample);
        // ---------------------------------------------------------------------        
        
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));            
        return return_value;        
    } // public Bitmap getBitmap()
    
} // public class Photograph
