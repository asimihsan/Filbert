package com.articheck.android;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        
        this.decoded_contents = new JSONObject(contents);
        this.title = decoded_contents.getString("Title");
        
    } // public ConditionReport(...)        
    
    public JSONArray getTemplateSection(String section_name) {
        return template.getSection(section_name);
    }
    
    public String getTitle() {
        return title;        
    }
    
    public JSONObject getDecodedContents()
    {
        return decoded_contents;
    } // public JSONObject getDecodedContents()
    
} // public class ConditionReport    
    
