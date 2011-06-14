package com.articheck.android;

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
    private String template_contents;
    
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
        return template_contents;
    }
    /**
     * @param condition_report_id
     * @param exhibition_id
     * @param media_id
     * @param lender_id
     * @param contents
     * @param template_contents 
     */
    public ConditionReport(String condition_report_id, 
                              String exhibition_id,
                              String media_id,
                              String lender_id,
                              String contents,
                              String template_contents)
    {
        this.condition_report_id = condition_report_id;
        this.exhibition_id = exhibition_id;
        this.media_id = media_id;
        this.lender_id = lender_id;
        this.contents = contents;
        this.template_contents = template_contents;
    } // public ConditionReport(...)        
} // public class ConditionReport    
    
