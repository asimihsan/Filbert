/*

TODO:

-   Docs for getReadableDatabase() say that it may take a long time
    to get a result.  In fact for all queries this is true.  Set up
    thread/AsyncTask/whatever to get this done.

*/

package com.articheck.android;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {
    private final String HEADER_TAG = this.getClass().getSimpleName(); 
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
    
    private static final String CREATE_PHOTOGRAPH_TABLE =
        "CREATE TABLE photograph" +
        "  (photograph_id TEXT PRIMARY KEY," +
        "   condition_report_id TEXT NOT NULL," +
        "   hash TEXT NOT NULL," +
        "   local_path TEXT NOT NULL);";
    private static final String DROP_PHOTOGRAPH_TABLE =
        "DROP table photograph;";    
    
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
    private static final String PHOTOGRAPH_ID = "photograph_id";
    private static final String HASH = "hash";
    private static final String LOCAL_PATH = "local_path";
    
    private static final String EXHIBITION_TABLE = "exhibition";
    private static final String MEDIA_TABLE = "media";
    private static final String LENDER_TABLE = "lender";
    private static final String CONDITION_REPORT_TABLE = "condition_report";
    private static final String TEMPLATE_TABLE = "template";
    private static final String PHOTOGRAPH_TABLE = "photograph";
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
    private static final String GET_PHOTOGRAPHS = 
        "SELECT photograph_id, condition_report_id, hash, local_path FROM photograph WHERE condition_report_id = ?;";        
    private static final String GET_PHOTOGRAPH_BY_PHOTOGRAPH_ID = 
        "SELECT photograph_id, condition_report_id, hash, local_path FROM photograph WHERE photograph_id = ?;";    

    // -------------------------------------------------------------------------
    
    Context context;

    /**
     * Constructor
     * 
     * @param context Context owning the manager.
     */
    public DatabaseManager(Context context, Integer version)
    {
        super(context, DATABASE_NAME, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String TAG = HEADER_TAG + "::onCreate";
        Log.d(TAG, "Entry.");
        String[] statements = {CREATE_EXHIBITION_TABLE,
                               CREATE_MEDIA_TABLE,
                               CREATE_LENDER_TABLE,
                               CREATE_CONDITION_REPORT_TABLE,
                               CREATE_TEMPLATE_TABLE,
                               CREATE_PHOTOGRAPH_TABLE};
        for (String statement : statements)
        {
            Log.d(TAG, String.format(Locale.US, "Executing: '%s'", statement));
            try
            {
                db.execSQL(statement);
            }
            catch (SQLException e)
            {
                Log.e(TAG, "SQLException", e);
            } // try
        } // for(String statement : statements)        
        populateDummyValues(db);
    } // public void onCreate(SQLiteDatabase db)

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        final String TAG = HEADER_TAG + "::onUpgrade";
        Log.d(TAG, "Entry.");
        String[] statements = {DROP_EXHIBITION_TABLE,
                               DROP_MEDIA_TABLE,
                               DROP_LENDER_TABLE,
                               DROP_CONDITION_REPORT_TABLE,
                               DROP_TEMPLATE_TABLE,
                               DROP_PHOTOGRAPH_TABLE};
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
        
        // ---------------------------------------------------------------------
        //  Be cheeky and delete all the photographs from the data directory.
        // ---------------------------------------------------------------------
        String state = Environment.getExternalStorageState();
        Log.d(TAG, String.format(Locale.US, "External storage stage: '%s'", state));       

        if (Environment.MEDIA_MOUNTED.equals(state))
        {        
            Log.d(TAG, "Storage is mounted and available");
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
            
            FilenameFilter filter = new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    return name.endsWith(".jpg");
                } // public boolean accept(File dir, String name)
            }; // FilenameFilter filter = new FilenameFilter()
            for (File file : actual_path_obj.listFiles(filter))
            {
                Log.d(TAG, String.format(Locale.US, "Deleting: '%s'", file));
                file.delete();
            } // for (File file : actual_path_obj.listFiles(filter))            
        } // if (Environment.MEDIA_MOUNTED.equals(state))
        
        // ---------------------------------------------------------------------
        
    } // public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)    
    
    @Override
    public void onOpen(SQLiteDatabase db) { 
        super.onOpen(db);
        final String TAG = HEADER_TAG  + "::onOpen";
        Log.d(TAG, "Entry.");
    } // public void onOpen(SQLiteDatabase db)

    public List<Exhibition> getExhibitions()    
    {        
        final String TAG = HEADER_TAG  + "::getExhibitions";
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
        final String TAG = HEADER_TAG + "::getConditionReportsByExhibitionId";
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
    
    /**
     * Get the Template that corresponds to a particular media.
     * 
     * We only return one Template, but we don't enforce such a relationship
     * in the SQL scheme because there may be more than one template per media
     * in the database (in the future may be allowed to update the templates
     * but still want to keep old ones on hand).
     * 
     * @param media_id String Used to uniquely identify the media.
     * @return Template instances corresponding to the template.
     * @throws JSONException
     */
    public Template getTemplateByMediaId(String media_id) throws JSONException
    {
        final String TAG = HEADER_TAG + "::getTemplateByMediaId";
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
        final String TAG = HEADER_TAG + "::populateDummyValues";
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
        try
        {
            cv.put(CONTENTS, (new JSONObject())
                    .put("Basic info", (new JSONObject())                    
                            .put("Title", "Famous piece number 1") 
                            .put("Artist", "Artist 1")
                            .put("Catalogue number", "X-535-1"))
                    .put("Painting support", (new JSONObject())                    
                        .put("Type", "Canvas") 
                        .put("Surface plane", (new JSONArray())
                                .put("Free of distortions"))
                        .put("Tension", "Tight")
                        .put("Tears/Splits", "None apparent")
                        .put("Losses", (new JSONArray())
                                .put("None apparent"))
                        .put("Accessory support", "Stretcher")
                        .put("Lining", "None"))
                    .put("Paint films", (new JSONObject())
                        .put("Type", "Oil")
                        .put("Crackle patterns", (new JSONArray())
                                .put("None"))
                        .put("Cleavage/Flaking", "None apparent")
                        .put("Losses", (new JSONArray())
                                .put("None apparent"))
                        .put("Other damages", "None apparent"))
                    .toString());            
        }
        catch (JSONException e)
        {
            Log.e(TAG, "Exception during template building", e);
        }

        db.insert(CONDITION_REPORT_TABLE, CONTENTS, cv);
        
        cv.put(CONDITION_REPORT_ID, "2");
        cv.put(EXHIBITION_ID, "1");
        cv.put(MEDIA_ID, "2");
        cv.put(LENDER_ID, "1");
        try
        {
            cv.put(CONTENTS, (new JSONObject())
                    .put("Basic info", (new JSONObject())                    
                        .put("Title", "Famous piece number 2") 
                        .put("Artist", "Artist 2")
                        .put("Catalogue number", "X-535-2"))
                    .put("Painting support", (new JSONObject())                    
                        .put("Type", "Other") 
                        .put("Surface plane", (new JSONArray())
                                .put("Free of distortions")
                                .put("Corner distortions"))
                        .put("Tears/Splits", "Yes"))                      
                    .toString());            
        }
        catch (JSONException e)
        {
            Log.e(TAG, "Exception during template building", e);
        }
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
                                                      .put("name", "Accessory support")
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
    
    public long addPhotograph(Photograph photograph)
    {
        final String TAG = HEADER_TAG + "::addPhotograph";
        Log.d(TAG, String.format(Locale.US, "Entry. photograph: '%s'", photograph));        
        ContentValues cv = new ContentValues();
        cv.put(PHOTOGRAPH_ID, photograph.getPhotographId());
        cv.put(CONDITION_REPORT_ID, photograph.getConditionReportId());
        cv.put(HASH, photograph.getHash());
        cv.put(LOCAL_PATH, photograph.getLocalPath());
        
        Log.d(TAG, "Getting writeable database...");
        SQLiteDatabase db = getWritableDatabase();
        Log.d(TAG, "Got writeable database.");
        long return_code = db.insertWithOnConflict(PHOTOGRAPH_TABLE, CONTENTS, cv, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d(TAG, String.format(Locale.US, "Insert return code: '%s'", return_code));        
        db.close();
        Log.d(TAG, "Closed database.");
        return return_code;        
    } // public void addPhotograph()
    
    public List<Photograph> getPhotographsByConditionReportId(String condition_report_id)
    {
        final String TAG = HEADER_TAG + "::getPhotographsByConditionReportId";
        Log.d(TAG, String.format(Locale.US, "Entry. condition_report_id: '%s'", condition_report_id));
        
        String[] args = {condition_report_id};
        Cursor cursor = getReadableDatabase().rawQuery(GET_PHOTOGRAPHS, args);
        List<Photograph> result = Lists.newArrayList();
        Log.d(TAG, "Number of results: " + cursor.getCount());                 
        while (cursor.moveToNext())
        {
            String photograph_id = cursor.getString(0);
            String returned_condition_report_id = cursor.getString(1);
            String hash = cursor.getString(2);
            String local_path = cursor.getString(3);            
            result.add(new Photograph.Builder()
                                     .photographId(photograph_id)
                                     .conditionReportId(returned_condition_report_id)
                                     .hash(hash)
                                     .localPath(local_path)
                                     .build());
        } // while (!cursor.moveToNext())
        cursor.close();        
        return result;        
    } // public ArrayList<ConditionReport> getConditionReportsByExhibitionId(String exhibition_id)
    
    public Photograph getPhotographsByPhotographId(String photograph_id)
    {
        final String TAG = HEADER_TAG + "::getPhotographsByPhotographId";
        Log.d(TAG, String.format(Locale.US, "Entry. photograph_id: '%s'", photograph_id));
        
        String[] args = {photograph_id};
        Cursor cursor = getReadableDatabase().rawQuery(GET_PHOTOGRAPH_BY_PHOTOGRAPH_ID, args);
        Photograph result = null;
        Log.d(TAG, "Number of results: " + cursor.getCount());
        while (cursor.moveToNext())
        {
            String returned_photograph_id = cursor.getString(0);
            String condition_report_id = cursor.getString(1);
            String hash = cursor.getString(2);
            String local_path = cursor.getString(3);
            result = new Photograph.Builder()
                                   .photographId(returned_photograph_id)
                                   .conditionReportId(condition_report_id)
                                   .hash(hash)
                                   .localPath(local_path)
                                   .build();            
        } // while (!cursor.moveToNext())
        cursor.close();        
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", result));
        return result;        
    } // public Photograph getPhotographsByPhotographId(String photograph_id)            

    public String addConditionReport(String exhibition_id,
                                       String media_id,
                                       String lender_id,
                                       String contents)
    {
        final String TAG = HEADER_TAG + "::addConditionReport";
        Log.d(TAG, String.format(Locale.US, "Entry. exhibition_id: '%s'," +
        		                            "       media_id: '%s'," +
        		                            "       lender_id: '%s'," +
        		                            "       contents: '%s'",
        		                            exhibition_id,
        		                            media_id,
        		                            lender_id,
        		                            contents));        
        ContentValues cv = new ContentValues();
        String new_uuid = UUID.randomUUID().toString();
        Log.d(TAG, String.format(Locale.US, "condition_report ID is: '%s'", new_uuid));
        cv.put(CONDITION_REPORT_ID, new_uuid);
        cv.put(EXHIBITION_ID, exhibition_id);
        cv.put(MEDIA_ID, media_id);
        cv.put(LENDER_ID, lender_id);
        cv.put(CONTENTS, contents);
        
        Log.d(TAG, "Getting writeable database...");
        SQLiteDatabase db = getWritableDatabase();
        Log.d(TAG, "Got writeable database.");
        long return_code = db.insertWithOnConflict(CONDITION_REPORT_TABLE, CONTENTS, cv, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d(TAG, String.format(Locale.US, "Insert return code: '%s'", return_code));        
        db.close();
        Log.d(TAG, "Closed database.");
        return new_uuid;        
    } //     public void addConditionReport()
    
    /**
     * Save a condition report to the database.
     * 
     * @param condition_report ConditionReport instance corresponding to the
     * condition report you want to save to the database.
     */
    public long saveConditionReport(ConditionReport condition_report)
    {
        final String TAG = HEADER_TAG + "::saveConditionReportToDatabase";
        Log.d(TAG, String.format(Locale.US, "Entry. condition_report: '%s'", condition_report));        

        ContentValues cv = new ContentValues();
        cv.put(CONDITION_REPORT_ID, condition_report.getConditionReportId());
        cv.put(EXHIBITION_ID, condition_report.getExhibitionId());
        cv.put(MEDIA_ID, condition_report.getMediaId());
        cv.put(LENDER_ID, condition_report.getLenderId());
        cv.put(CONTENTS, condition_report.getContents());
        
        Log.d(TAG, "Getting writeable database...");
        SQLiteDatabase db = getWritableDatabase();
        Log.d(TAG, "Got writeable database.");
        long return_code = db.insertWithOnConflict(CONDITION_REPORT_TABLE, CONTENTS, cv, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d(TAG, String.format(Locale.US, "Insert return code: '%s'", return_code));        
        db.close();
        Log.d(TAG, "Closed database.");
        return return_code;
    } // public long saveConditionReportToDatabase(ConditionReport condition_report)
    
    public void deleteConditionReport(ConditionReport condition_report)
    {
        final String TAG = HEADER_TAG + "::deleteConditionReport";
        Log.d(TAG, String.format(Locale.US, "Entry. condition_report: '%s'", condition_report));        

        Log.d(TAG, "Getting writeable database...");
        SQLiteDatabase db = getWritableDatabase();
        Log.d(TAG, "Got writeable database.");
        
        db.delete(CONDITION_REPORT_TABLE,
                  "condition_report_id = ?",
                  new String[] {condition_report.getConditionReportId()});
        
        db.close();
        Log.d(TAG, "Closed database.");        
    } // public void deleteConditionReport(ConditionReport condition_report)
    
    public String getUuid()
    {
        return UUID.randomUUID().toString();
    }
   
} // public class DatabaseManager extends SQLiteOpenHelper
