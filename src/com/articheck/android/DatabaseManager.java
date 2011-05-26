/*

TODO:

-   Docs for getReadableDatabase() say that it may take a long time
    to get a result.  In fact for all queries this is true.  Set up
    thread/AsyncTask/whatever to get this done.

*/

package com.articheck.android;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
    private static final String CONTENTS = "contents";
    
    private static final String EXHIBITION_TABLE = "exhibition";
    private static final String MEDIA_TABLE = "media";
    private static final String LENDER_TABLE = "lender";
    private static final String CONDITION_REPORT_TABLE = "condition_report";
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    //  SELECT statements. 
    // -------------------------------------------------------------------------
    private static final String GET_EXHIBITIONS =
        "SELECT exhibition_id, exhibition_name FROM exhibition ORDER BY exhibition_name;";
    
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
        Log.d(TAG, "Call populateDummyValues()");
        populateDummyValues(db);
    } // public void onCreate(SQLiteDatabase db)

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        final String TAG = getClass().getName() + "::onUpgrade";
        Log.d(TAG, "Entry.");
        db.execSQL(DROP_EXHIBITION_TABLE);
        db.execSQL(DROP_MEDIA_TABLE);
        db.execSQL(DROP_LENDER_TABLE);
        db.execSQL(DROP_CONDITION_REPORT_TABLE);
        onCreate(db);
    } // public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    
    public static class Exhibition
    {
        public String id;
        public String name;        
        public Exhibition(String id, String name)
        {
            this.id = id;
            this.name = name;
        } // public Exhibition(String id, String name)
        
    } // public static class Exhibition
    
    public ArrayList<Exhibition> getExhibitions()    
    {        
        final String TAG = getClass().getName() + "::getExhibitions";
        Log.d(TAG, "Entry.");
        Cursor cursor = getReadableDatabase().rawQuery(GET_EXHIBITIONS, null);
        ArrayList<Exhibition> result = new ArrayList<Exhibition>();
        Log.d(TAG, "Number of results: " + cursor.getCount());
        while (cursor.moveToNext())
        {
            String id = cursor.getString(0);
            String name = cursor.getString(1);            
            result.add(new Exhibition(id, name));
        } // while (!cursor.moveToNext())
        cursor.close();        
        return result;
    } // public void getExhibitionNames()

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
        cv.put(CONTENTS, "test contents");
        db.insert(CONDITION_REPORT_TABLE, CONTENTS, cv);
        
        cv.put(CONDITION_REPORT_ID, "2");
        cv.put(EXHIBITION_ID, "1");
        cv.put(MEDIA_ID, "2");
        cv.put(LENDER_ID, "1");
        cv.put(CONTENTS, "test contents");
        db.insert(CONDITION_REPORT_TABLE, CONTENTS, cv);
        
    } // private void populateDummyValues()
    
} // public class DatabaseManager extends SQLiteOpenHelper
