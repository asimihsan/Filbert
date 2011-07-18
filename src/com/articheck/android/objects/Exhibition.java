package com.articheck.android.objects;

/**
 * @author ai
 *
 */
public class Exhibition
{
    private String exhibition_id;
    private String exhibition_name;        

    public Exhibition(String exhibition_id, String exhibition_name)
    {
        this.exhibition_id = exhibition_id;
        this.exhibition_name = exhibition_name;
    } // public Exhibition(String exhibition_id, String exhibition_name)
    
    public String getExhibitionId() {
        return exhibition_id;
    }
    public String getExhibitionName() {
        return exhibition_name;
    }       
    
} // public static class Exhibition


