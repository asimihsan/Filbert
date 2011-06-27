package com.articheck.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.articheck.android.utilities.Json.ConditionReportContentsJsonWrapper;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * @author ai 
 *
 */
public class ConditionReport {
    
    private String condition_report_id;
    private String exhibition_id;
    private String media_id;
    private String lender_id;
    private String contents;             
    private Template template;
    
    private ConditionReportContentsJsonWrapper contents_wrapper;
    private String title;
    private ImmutableSet<String> contents_section_names;
    
    public String getConditionReportId() {
        return condition_report_id;
    }
    public String getExhibitionId() {
        return exhibition_id;
    }
    public String getMediaId() {
        return media_id;
    }
    public String getLenderId() {
        return lender_id;
    }
    public String getContents()
    {
        final String TAG = getClass().getName() + "::getContents";
        JSONObject json_object;
        try
        {
            json_object = getDecodedContents();
        }
        catch (JSONException e)
        {
            Log.e(TAG, "Exception while getting contents.", e);
            return null;
        }
        contents = json_object.toString();
        return contents;
    }
    public String getTemplateContents() {
        return template.getContents();
    }
    /**
     * @param condition_report_id
     * @param exhibition_id
     * @param media_id
     * @param lender_id
     * @param contents
     * @param template 
     * @throws JSONException 
     */
    public ConditionReport(String condition_report_id, 
                              String exhibition_id,
                              String media_id,
                              String lender_id,
                              String contents,
                              Template template) throws JSONException
    {
        this.condition_report_id = condition_report_id;
        this.exhibition_id = exhibition_id;
        this.media_id = media_id;
        this.lender_id = lender_id;
        this.contents = contents;
        this.template = template;
        initialize();        
    } // public ConditionReport(...)
    
    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                       .add("condition_report_id", condition_report_id)
                       .add("exhibition_id", exhibition_id)
                       .add("media_id", media_id)
                       .add("lender_id", lender_id)
                       .add("contents", getContents())
                       .add("template", template)
                       .add("contents_wrapper", contents_wrapper)
                       .add("title", title)
                       .add("contents_section_names", contents_section_names)
                       .toString();
    }
    
    private void initialize() throws JSONException
    {
        JSONObject decoded_contents = new JSONObject(contents);
        contents_wrapper = new ConditionReportContentsJsonWrapper(decoded_contents);        
    } // private updateInteralState()
    
    public JSONArray getTemplateSection(String section_name) {
        return template.getSection(section_name);
    }
    
    public List<String> getTemplateSectionNames() 
    {
        return template.getSectionNames();
    } // public List<String> getTemplateSectionNames()
    
    public String getTitle() {
        return contents_wrapper.getTitle();        
    }
    
    public JSONObject getDecodedContents() throws JSONException
    {
        return contents_wrapper.getJsonObject();
    } // public JSONObject getDecodedContents()
    
    public boolean isSectionInContents(String section_name)
    {
        return contents_section_names.contains(section_name);
    } // public boolean isSectionInContents(String section_name)
    
    public String getValueFromSectionNameAndFieldName(String section_name, String field_name)
    {
        return contents_wrapper.getValueFromSectionNameAndFieldName(section_name, field_name);
    }
    
    public List<String> getValuesFromSectionNameAndFieldName(String section_name, String field_name)
    {
        return contents_wrapper.getValuesFromSectionNameAndFieldName(section_name, field_name);
    }    
    
    public boolean setValue(String section_name, String field_name, String value)
    {
        return contents_wrapper.setValue(section_name, field_name, value);
    }
    
    public boolean setValue(String section_name, String field_name, List<String> values)
    {
        return contents_wrapper.setValue(section_name, field_name, values);
    }    
    
} // public class ConditionReport    
    
