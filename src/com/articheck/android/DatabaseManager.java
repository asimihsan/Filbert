/*

TODO:

-   Docs for getReadableDatabase() say that it may take a long time
    to get a result.  In fact for all queries this is true.  Set up
    thread/AsyncTask/whatever to get this done.

*/

package com.articheck.android;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {
    private final String TAG = this.getClass().getSimpleName(); 
    private static final String DATABASE_NAME = "articheck_db";
    
    // -------------------------------------------------------------------------
    //  CREATE/DROP statements.
    // -------------------------------------------------------------------------
    private static final String CREATE_EXHIBITION_TABLE =
        "CREATE TABLE exhibition" +
        "  (exhibition_id TEXT PRIMARY KEY," +
        "   exhibition_name TEXT NOT NULL);";
    private static final String DROP_EXHIBITION_TABLE =
        "DROP table exhibition;";

    private static final String CREATE_MEDIA_TABLE =
        "CREATE TABLE media" +
        "  (media_id TEXT PRIMARY KEY," +
        "   media_name TEXT NOT NULL);";
    private static final String DROP_MEDIA_TABLE =
        "DROP table media;";
    
    private static final String CREATE_LENDER_TABLE =
        "CREATE TABLE lender" +
        "  (lender_id TEXT PRIMARY KEY," +
        "   lender_name TEXT NOT NULL);";
    private static final String DROP_LENDER_TABLE =
        "DROP table lender;";        
    
    private static final String CREATE_CONDITION_REPORT_TABLE =
        "CREATE TABLE condition_report" +
        "  (condition_report_id TEXT PRIMARY KEY," +
        "   exhibition_id TEXT NOT NULL," +
        "   media_id TEXT NOT NULL," +
        "   lender_id TEXT NOT NULL," +
        "   contents TEXT);";
    private static final String DROP_CONDITION_REPORT_TABLE =
        "DROP table condition_report;";
    
    private static final String CREATE_TEMPLATE_TABLE =
        "CREATE TABLE template" +
        "  (template_id TEXT PRIMARY KEY," +
        "   media_id TEXT NOT NULL," +
        "   contents TEXT NOT NULL);";
    private static final String DROP_TEMPLATE_TABLE =
        "DROP table template;";    
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    //  Statement keys.
    // -------------------------------------------------------------------------
    private static final String EXHIBITION_ID = "exhibition_id";
    private static final String EXHIBITION_NAME = "exhibition_name";
    private static final String MEDIA_ID = "media_id";
    private static final String MEDIA_NAME = "media_name";
    private static final String LENDER_ID = "lender_id";
    private static final String LENDER_NAME = "lender_name";
    private static final String CONDITION_REPORT_ID = "condition_report_id";
    private static final String TEMPLATE_ID = "template_id";
    private static final String CONTENTS = "contents";
    
    private static final String EXHIBITION_TABLE = "exhibition";
    private static final String MEDIA_TABLE = "media";
    private static final String LENDER_TABLE = "lender";
    private static final String CONDITION_REPORT_TABLE = "condition_report";
    private static final String TEMPLATE_TABLE = "template";
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    //  SELECT statements and lookups to their compiled versions. 
    // -------------------------------------------------------------------------
    private static final String GET_EXHIBITIONS =
        "SELECT exhibition_id, exhibition_name FROM exhibition ORDER BY exhibition_name;";    
    private static final String GET_CONDITION_REPORTS =
        "SELECT condition_report_id, exhibition_id, media_id, lender_id, contents " +
        "FROM condition_report WHERE exhibition_id = ?;";    
    private static final String GET_TEMPLATE = 
        "SELECT template_id, media_id, contents FROM template WHERE media_id = ?;";       

    // -------------------------------------------------------------------------    

    /**
     * Constructor
     * 
     * @param context Context owning the manager.
     */
    public DatabaseManager(Context context, Integer version)
    {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String TAG = getClass().getName() + "::onCreate";
        Log.d(TAG, "Entry.");
        Log.d(TAG, "CREATE_EXHIBITION_TABLE");
        db.execSQL(CREATE_EXHIBITION_TABLE);
        Log.d(TAG, "CREATE_MEDIA_TABLE");
        db.execSQL(CREATE_MEDIA_TABLE);
        Log.d(TAG, "CREATE_LENDER_TABLE");
        db.execSQL(CREATE_LENDER_TABLE);
        Log.d(TAG, "CREATE_CONDITION_REPORT_TABLE");
        db.execSQL(CREATE_CONDITION_REPORT_TABLE);
        Log.d(TAG, "CREATE_TEMPLATE_TABLE");
        db.execSQL(CREATE_TEMPLATE_TABLE);        
        Log.d(TAG, "Call populateDummyValues()");
        populateDummyValues(db);
    } // public void onCreate(SQLiteDatabase db)

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        final String TAG = getClass().getName() + "::onUpgrade";
        Log.d(TAG, "Entry.");
        String[] statements = {DROP_EXHIBITION_TABLE,
                               DROP_MEDIA_TABLE,
                               DROP_LENDER_TABLE,
                               DROP_CONDITION_REPORT_TABLE,
                               DROP_TEMPLATE_TABLE};
        for(String statement : statements)
        {
            Log.d(TAG, "Executing: " + statement);
            try {
                db.execSQL(statement);
            } catch (SQLException e) {
                Log.e(TAG, "SQLException", e);
            } // try
        } // for(String statement : statements)
        onCreate(db);
    } // public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)    
    
    @Override
    public void onOpen(SQLiteDatabase db) { 
        super.onOpen(db);
        final String TAG = getClass().getName() + "::onOpen";
        Log.d(TAG, "Entry.");
    } // public void onOpen(SQLiteDatabase db)

    public List<Exhibition> getExhibitions()    
    {        
        final String TAG = getClass().getName() + "::getExhibitions";
        Log.d(TAG, "Entry.");
        Cursor cursor = getReadableDatabase().rawQuery(GET_EXHIBITIONS, null);
        List<Exhibition> result = new ArrayList<Exhibition>();
        Log.d(TAG, "Number of results: " + cursor.getCount());
        while (cursor.moveToNext())
        {
            String exhibiton_id = cursor.getString(0);
            String exhibiton_name = cursor.getString(1);            
            result.add(new Exhibition(exhibiton_id, exhibiton_name));
        } // while (!cursor.moveToNext())
        cursor.close();        
        return result;
    } // public void getExhibitionNames()
    
    public List<ConditionReport> getConditionReportsByExhibitionId(String exhibition_id) throws JSONException
    {
        final String TAG = getClass().getName() + "::getConditionReportsByExhibitionId";
        Log.d(TAG, "Entry.");
        
        Map<String, Template> media_id_to_template = new LinkedHashMap<String, Template>();
        Template template;
        
        String[] args = {exhibition_id};
        Cursor cursor = getReadableDatabase().rawQuery(GET_CONDITION_REPORTS, args);
        List<ConditionReport> result = new ArrayList<ConditionReport>();
        Log.d(TAG, "Number of results: " + cursor.getCount());                 
        while (cursor.moveToNext())
        {
            String condition_report_id = cursor.getString(0);
            String returned_exhibition_id = cursor.getString(1);
            String media_id = cursor.getString(2);
            String lender_id = cursor.getString(3);
            String contents = cursor.getString(4);
            
            // Lookup the template by media_id, hitting the cache first just
            // in case we've seen this media_id before.
            if (media_id_to_template.containsKey(media_id))
            {
                template = media_id_to_template.get(media_id);
            } 
            else 
            {
                template = getTemplateByMediaId(media_id); 
                media_id_to_template.put(media_id, template);
            } // if (media_id_to_template.containsKey(media_id))
            
            result.add(new ConditionReport(condition_report_id,
                                           returned_exhibition_id,
                                           media_id,
                                           lender_id,
                                           contents,
                                           template));
        } // while (!cursor.moveToNext())
        cursor.close();        
        return result;        
    } // public ArrayList<ConditionReport> getConditionReportsByExhibitionId(String exhibition_id)    
    
    public Template getTemplateByMediaId(String media_id) throws JSONException
    {
        final String TAG = getClass().getName() + "::getTemplateByMediaId";
        Log.d(TAG, "Entry.");
        String[] args = {media_id};
        Cursor cursor = getReadableDatabase().rawQuery(GET_TEMPLATE, args);        
        Log.d(TAG, "Number of results: " + cursor.getCount());
        cursor.moveToNext();
        String template_id = cursor.getString(0);
        String returned_media_id = cursor.getString(1);
        String contents = cursor.getString(2);
        Template result = new Template(template_id, returned_media_id, contents);
        cursor.close();        
        return result;                
    } // private Template getTemplateByMediaId(String media_id)

    private void populateDummyValues(SQLiteDatabase db)
    {
        final String TAG = getClass().getName() + "::populateDummyValues";
        Log.d(TAG, "Entry.");     
        ContentValues cv = new ContentValues();
        
        Log.d(TAG, "Insert lenders.");
        cv.put(LENDER_ID, "1");
        cv.put(LENDER_NAME, "Sepentine");
        db.insert(LENDER_TABLE, LENDER_ID, cv);
        cv.put(LENDER_ID, "2");
        cv.put(LENDER_NAME, "Tate");
        db.insert(LENDER_TABLE, LENDER_ID, cv);
        cv.put(LENDER_ID, "3");
        cv.put(LENDER_NAME, "National Portrait Gallery");
        db.insert(LENDER_TABLE, LENDER_ID, cv);
        
        Log.d(TAG, "Insert media.");
        cv.clear();
        cv.put(MEDIA_ID, "1");
        cv.put(MEDIA_NAME, "Paper");
        db.insert(MEDIA_TABLE, MEDIA_ID, cv);
        cv.put(MEDIA_ID, "2");
        cv.put(MEDIA_NAME, "Painting");
        db.insert(MEDIA_TABLE, MEDIA_ID, cv);
        cv.put(MEDIA_ID, "3");
        cv.put(MEDIA_NAME, "Sculpture");
        db.insert(MEDIA_TABLE, MEDIA_ID, cv);
        cv.put(MEDIA_ID, "4");
        cv.put(MEDIA_NAME, "Time-based Media");
        db.insert(MEDIA_TABLE, MEDIA_ID, cv);         
        
        Log.d(TAG, "Insert exhibitions.");
        cv.clear();
        cv.put(EXHIBITION_ID, "1");
        cv.put(EXHIBITION_NAME, "Picasso");
        db.insert(EXHIBITION_TABLE, EXHIBITION_ID, cv);
        cv.put(EXHIBITION_ID, "2");
        cv.put(EXHIBITION_NAME, "Great sculpture");
        db.insert(EXHIBITION_TABLE, EXHIBITION_ID, cv);
        cv.put(EXHIBITION_ID, "3");
        cv.put(EXHIBITION_NAME, "A bit of everything");
        db.insert(EXHIBITION_TABLE, EXHIBITION_ID, cv);
        
        Log.d(TAG, "Insert condition reports.");
        cv.clear();
        cv.put(CONDITION_REPORT_ID, "1");
        cv.put(EXHIBITION_ID, "1");
        cv.put(MEDIA_ID, "2");
        cv.put(LENDER_ID, "3");
        cv.put(CONTENTS, "{'Title':        'Famous piece number 1'," +
        		         " 'Artist':       'Artist 1'," +
        		         " 'Catalogue number': 'X-535-1'}");
        db.insert(CONDITION_REPORT_TABLE, CONTENTS, cv);
        
        cv.put(CONDITION_REPORT_ID, "2");
        cv.put(EXHIBITION_ID, "1");
        cv.put(MEDIA_ID, "2");
        cv.put(LENDER_ID, "1");
        cv.put(CONTENTS, "{'Title':        'Famous piece number 2'," +
                         " 'Artist':       'Artist 2'," +
                         " 'Catalogue number': 'X-535-2'}");
        db.insert(CONDITION_REPORT_TABLE, CONTENTS, cv);
        
        Log.d(TAG, "Insert templates.");
        try {
            cv.clear();        
            cv.put(TEMPLATE_ID, "1");
            cv.put(MEDIA_ID, "2");
            
            JSONObject json_contents = new JSONObject()
            .put("sections",
                    (new JSONArray())
                        .put((new JSONObject())
                            .put("name", "Basic info")
                            .put("contents", (new JSONArray())
                                                  .put((new JSONObject())
                                                      .put("type", "text")
                                                      .put("name", "Catalogue number"))
                                                  .put((new JSONObject())
                                                      .put("type", "text")
                                                      .put("name", "Artist"))
                                                  .put((new JSONObject())
                                                      .put("type", "text")
                                                      .put("name", "Title"))))
                        .put((new JSONObject())
                            .put("name", "Painting support")
                            .put("contents", (new JSONArray())
                                                  .put((new JSONObject())
                                                      .put("type", "radio")
                                                      .put("name", "Type")
                                                      .put("values", (new JSONArray())
                                                                         .put("Canvas")
                                                                         .put("Panel")
                                                                         .put("Other")))
                                                  .put((new JSONObject())
                                                      .put("type", "check")
                                                      .put("name", "Surface plane")
                                                      .put("values", (new JSONArray())
                                                                         .put("Free of distortions")
                                                                         .put("Localised distortions")
                                                                         .put("Corner distortions")
                                                                         .put("General undulations/warping")
                                                                         .put("Distortions due to cupping")))
                                                  .put((new JSONObject())
                                                      .put("type", "radio")
                                                      .put("name", "Tension")
                                                      .put("values", (new JSONArray())
                                                                         .put("Tight")
                                                                         .put("Adequate")
                                                                         .put("Slack")
                                                                         .put("Canvas can contact cross-bars")))
                                                  .put((new JSONObject())
                                                      .put("type", "radio")
                                                      .put("name", "Tears/Splits")
                                                      .put("values", (new JSONArray())
                                                                         .put("None apparent")
                                                                         .put("Yes")))
                                                  .put((new JSONObject())
                                                      .put("type", "check")
                                                      .put("name", "Losses")
                                                      .put("values", (new JSONArray())
                                                                         .put("None apparent")
                                                                         .put("None recent")
                                                                         .put("Yes")))
                                                  .put((new JSONObject())
                                                      .put("type", "radio")
                                                      .put("name", "Accessory Support")
                                                      .put("values", (new JSONArray())
                                                                         .put("Stretcher")
                                                                         .put("Other")))
                                                  .put((new JSONObject())
                                                      .put("type", "radio")
                                                      .put("name", "Lining")
                                                      .put("values", (new JSONArray())
                                                                         .put("None")
                                                                         .put("Stretcher-bar lining")
                                                                         .put("Yes")))))
                        .put((new JSONObject())
                            .put("name", "Paint films")
                            .put("contents", (new JSONArray())
                                                  .put((new JSONObject())
                                                      .put("type", "radio")
                                                      .put("name", "Type")
                                                      .put("values", (new JSONArray())
                                                                         .put("Oil")
                                                                         .put("Acrylic")
                                                                         .put("Other")))
                                                  .put((new JSONObject())
                                                      .put("type", "check")
                                                      .put("name", "Crackle patterns")
                                                      .put("values", (new JSONArray())
                                                                         .put("None")
                                                                         .put("Localised only")
                                                                         .put("General brittle fracture network")
                                                                         .put("Drying cracks")
                                                                         .put("Bar marks")
                                                                         .put("Raised edges")))
                                                  .put((new JSONObject())
                                                      .put("type", "radio")
                                                      .put("name", "Cleavage/Flaking")
                                                      .put("values", (new JSONArray())
                                                                         .put("None apparent")
                                                                         .put("Yes")))
                                                  .put((new JSONObject())
                                                      .put("type", "check")
                                                      .put("name", "Losses")
                                                      .put("values", (new JSONArray())
                                                                         .put("None apparent")
                                                                         .put("None recent")
                                                                         .put("Yes")))
                                                  .put((new JSONObject())
                                                      .put("type", "radio")
                                                      .put("name", "Other damages")
                                                      .put("values", (new JSONArray())
                                                                         .put("None apparent")
                                                                         .put("None recent")
                                                                         .put("Yes")
                                                                         .put("Scratches/abrasions"))))));
            cv.put(CONTENTS, json_contents.toString());
            Log.d(TAG, "template: " + cv.getAsString(CONTENTS));
            db.insert(TEMPLATE_TABLE, CONTENTS, cv);
        } catch (JSONException e) {
            Log.e(TAG, "Exception during template building", e);
        }
        
        
        
        
    } // private void populateDummyValues()   

    
} // public class DatabaseManager extends SQLiteOpenHelper
