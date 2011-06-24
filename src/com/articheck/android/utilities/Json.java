package com.articheck.android.utilities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

/**
 * @author ai
 *
 */
public class Json
{
    /**
     * Convert a JSONArray of String objects to a List<String>.  Does not
     * throw JSONException because such exceptions should be impossible.
     * 
     * @param json_array JSONArray of String objects.
     * @return A list of strings corresponding to the contents of the
     * JSONArray.
     */
    public static List<String> JsonArrayToList(JSONArray json_array)
    {   
        final String TAG = Json.class.getName() + "::JsonArrayToList";
        Log.d(TAG, "Entry");
        
        int size = json_array.length();
        List<String> return_value = new ArrayList<String>(size);
        for (int i = 0; i < size; i++)
        {
            try {
                String value = json_array.getString(i);
                Log.d(TAG, String.format("Value at index '%s' is '%s'", i, value));
                return_value.add(value);
            } catch (JSONException e) {
                Log.e(TAG, String.format("Exception while going through JSONArray at index %s.", i), e);
            }
        } // for (int i = 0; i < size; i++)
        return return_value;
    } // public static List<String> JsonArrayToList(JSONArray json_array)
    
} // public class Json
