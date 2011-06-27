package com.articheck.android;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Objects;

import android.util.Log;

/**
 * @author ai
 *
 */
public class Template
{
    private String template_id;
    private String media_id;
    private String contents;
    private JSONObject decoded_contents;
    private Map<String, JSONArray> lookup_section;
    
    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                       .add("template_id", template_id)
                       .add("media_id", media_id)
                       .add("contents", contents)
                       .add("decoded_contents", decoded_contents)
                       .add("lookup_section", lookup_section)
                       .toString();
    } // public String toString()
    
    /**
     * @param template_id
     * @param media_id
     * @param contents
     * @throws JSONException 
     */
    public Template(String template_id, 
                      String media_id,
                      String contents) throws JSONException
    {
        this.template_id = template_id ;
        this.media_id = media_id;
        this.contents = contents;
        this.decoded_contents = new JSONObject(contents);
        this.lookup_section = getSectionContents(decoded_contents);
    } // public Template(...)
    
    private Map<String, JSONArray> getSectionContents(JSONObject decoded_contents) throws JSONException
    {
        final String TAG = getClass().getName() + "::getSectionContents";
        Log.d(TAG, "Entry");
        
        Map<String, JSONArray> result = new LinkedHashMap<String, JSONArray>();
        JSONArray json_sections = decoded_contents.getJSONArray("sections");        
        int size = json_sections.length();
        Log.d(TAG, "size: " + size);                
        for (int i = 0; i < size; ++i)
        {
            JSONObject element = json_sections.getJSONObject(i);
            String section_name = element.getString("name");
            JSONArray section_contents = element.getJSONArray("contents");
            result.put(section_name, section_contents);
        } // for (int i = 0; i < size; ++i)        
        return result;        
    } // private populateSectionContents()
    
    public List<String> getSectionNames()
    {
        final String TAG = getClass().getName() + "::getSectionNames";
        Log.d(TAG, "Entry");
        return new ArrayList<String>(lookup_section.keySet());
    } // public getSectionNames()
    
    public JSONArray getSection(String section_name) {
        final String TAG = getClass().getName() + "::getSection";
        Log.d(TAG, "Entry");        
        return lookup_section.get(section_name);
    } // public JSONArray getSection(String section_name)
    
    /**
     * @return Template ID.
     */
    public String getTemplateId() {
        return template_id;
    }
    /**
     * @return Media ID.
     */
    public String getMediaId() {
        return media_id;
    }
    /**
     * @return JSON string representing contents of the template.
     */
    public String getContents() {
        return contents;
    }
    
    /**
     * @return JSON object of the decoded JSON contents string.
     */
    public JSONObject getDecodedContents() {
        return decoded_contents;
    }

} // public class Template
