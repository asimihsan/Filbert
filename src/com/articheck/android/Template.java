package com.articheck.android;

/**
 * @author ai
 *
 */
public class Template
{
    private String template_id;
    private String media_id;
    private String contents;             
    /**
     * @param template_id
     * @param media_id
     * @param contents
     */
    public Template(String template_id, 
                      String media_id,
                      String contents)
    {
        this.template_id = template_id ;
        this.media_id = media_id;
        this.contents = contents;
    } // public Template(...)     
    
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
} // public class Template
