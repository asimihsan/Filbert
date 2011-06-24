package com.articheck.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    
    private JSONObject decoded_contents;
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
    public String getContents() {
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
        updateInternalState();        
    } // public ConditionReport(...)
    
    private void updateInternalState() throws JSONException
    {
        this.decoded_contents = new JSONObject(contents);
        JSONObject basic_info = decoded_contents.getJSONObject("Basic info"); 
        this.title = basic_info.getString("Title");
        
        JSONArray section_objects = decoded_contents.names();
        int section_names_size = section_objects.length();
        List<String> section_names = new ArrayList<String>(section_names_size);        
        for (int i = 0; i < section_names_size; i++)
        {
            section_names.add(section_objects.getString(i));
        } // for (int i = 0; i < section_names_size; i++)
        this.contents_section_names = new ImmutableSet.Builder<String>()
                                                       .addAll(section_names)
                                                       .build();        
    } // private updateInteralState()
    
    public JSONArray getTemplateSection(String section_name) {
        return template.getSection(section_name);
    }
    
    public List<String> getTemplateSectionNames() 
    {
        return template.getSectionNames();
    } // public List<String> getTemplateSectionNames()
    
    public String getTitle() {
        return title;        
    }
    
    public JSONObject getDecodedContents()
    {
        return decoded_contents;
    } // public JSONObject getDecodedContents()
    
    public boolean isSectionInContents(String section_name)
    {
        return contents_section_names.contains(section_name);
    } // public boolean isSectionInContents(String section_name)
    
} // public class ConditionReport    
    
