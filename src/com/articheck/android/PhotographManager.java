package com.articheck.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * Responsible for photograph images associated with condition reports.  Handles
 * on-device storage, communication with remote server.  Uses the 
 * DatabaseManager for logical references to the photographs. * 
 * 
 * @author ai
 *
 */
public class PhotographManager
{
    private final String HEADER_TAG = getClass().getName();
    
    Context context;
    DatabaseManager db;
    
    PhotographManager(Context context, DatabaseManager db)
    {
        final String TAG = HEADER_TAG + "::PhotographManager";
        Log.d(TAG, "Entry.");
        
        this.context = context;
        this.db = db;
        initialize();                
    } // PhotographManager(Activity activity)
    
    private boolean isExternalStorageAvailable()
    {
        final String TAG = HEADER_TAG + "::isExternalStorageAvailable";
        Log.d(TAG, "Entry.");
        
        String state = Environment.getExternalStorageState();
        Log.d(TAG, String.format(Locale.US, "External storage stage: '%s'", state));
        
        boolean return_value = false;
        if (Environment.MEDIA_MOUNTED.equals(state) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            return_value = true;
        }
        // For whatever reason the media is not even read-only.
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));
        return return_value;
    } // private boolean isExternalStorageAvailable()
    
    private boolean isExternalStorageWritable()
    {
        final String TAG = HEADER_TAG + "::isExternalStorageWritable";
        Log.d(TAG, "Entry.");        
        String state = Environment.getExternalStorageState();
        Log.d(TAG, String.format(Locale.US, "External storage stage: '%s'", state));
        
        boolean return_value = false;
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            // We can read and write the media.
            return_value = true;
        }
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));
        return return_value;
    } // private boolean isExternalStorageAvailable()
    
    private File getPhotographsPath()
    {
        final String TAG = HEADER_TAG + "::getPhotographsPath";
        Log.d(TAG, "Entry.");        
        
        File root_path = context.getExternalFilesDir(null);
        Log.d(TAG, String.format(Locale.US, "Application external files dir: '%s'", root_path));       
        StringBuilder actual_path = new StringBuilder(root_path.getAbsolutePath());
        String separator = Character.toString(File.separatorChar);
        if (!actual_path.toString().endsWith(separator))
        {
            Log.d(TAG, "Path does not end with a path separator.");
            actual_path.append(separator);
        }
        actual_path.append("photographs");
        File actual_path_obj = new File(actual_path.toString());
        if (!actual_path_obj.exists())
        {
            Log.d(TAG, "Path does not exist.");
            actual_path_obj.mkdir();
        } // if (!actual_path_obj.exists())
        
        // ---------------------------------------------------------------------
        //  Check post-conditions.
        // ---------------------------------------------------------------------
        assert(actual_path_obj.exists());
        assert(actual_path_obj.isDirectory());
        // ---------------------------------------------------------------------        
        
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", actual_path_obj));
        return actual_path_obj;
    } // private File getPhotographsPath()
    
    /**
     * Save a photograph to the external storage.
     * 
     * @param identifier Unique identifier for this photograph, probably just
     * the primary key from the database.
     * 
     * @param extension Extension of the photograph, e.g. "jpg". 
     * 
     * @param contents Byte array of the complete contents of the photograph.
     * 
     * @return True if the photograph is saved successfully, false if not.
     */
    private boolean savePhotographToStorage(String identifier, String extension, byte[] contents)
    {
        final String TAG = HEADER_TAG + "::savePhotographToStorage";
        Log.d(TAG, String.format(Locale.US, "Entry. identifier: '%s', extension: '%s'", identifier, extension));
        
        if (!isExternalStorageWritable())
        {
            Log.e(TAG, "External storage is not writable");
            return false;
        }        
        
        File path = getPhotographsPath();
        assert(path.exists());
        assert(path.isDirectory());
        
        String filename = String.format(Locale.US, "%s.%s", identifier, extension);
        File file = new File(path, filename);
        Log.d(TAG, String.format(Locale.US, "Writing to file '%s'", file.getAbsolutePath()));

        FileOutputStream os;
        try
        {
            os = new FileOutputStream(file);
        }
        catch (FileNotFoundException e1)
        {
            Log.e(TAG, "File not found exception");
            return false;
        }
        try
        {
            os.write(contents);
            os.getFD().sync();
            os.close();
        }
        catch (IOException e)
        {
            Log.e(TAG, "IOException while writing to file.", e);
            return false;
        }        
        return true;        
    } // private boolean savePhotographToStorage(String identifier, byte[] contents)
    
    private void initialize()
    {
        final String TAG = HEADER_TAG + "::initialize";
        Log.d(TAG, "Entry.");        

        String filename = "hello_file";
        String extension = "jpg";
        String contents = "hello world!";
        savePhotographToStorage(filename, extension, contents.getBytes());
        
    } // private void initialize()
    
    public void close()
    {
        final String TAG = HEADER_TAG + "::close";
        Log.d(TAG, "Entry.");                
        
    } // public void close()
    
} // public class PhotographManager
