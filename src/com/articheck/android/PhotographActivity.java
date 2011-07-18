package com.articheck.android;

import java.util.Locale;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PhotographActivity extends Activity
{
    final String HEADER_TAG = getClass().getName();
    private String photograph_id;
    private String condition_report_id;
    private Photograph photograph;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final String TAG = HEADER_TAG + "::onCreate";
        Log.d(TAG, String.format(Locale.US, "Entry. savedInstanceState: '%s'", savedInstanceState));
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photograph_activity);
        
        Bundle extras = this.getIntent().getExtras();
        assert(extras.containsKey("photograph_id"));
        assert(extras.containsKey("condition_report_id"));
        
        photograph_id = extras.getString("photograph_id");
        Log.d(TAG, String.format(Locale.US, "photograph_id is: '%s'", photograph_id));
        
        condition_report_id = extras.getString("condition_report_id");
        Log.d(TAG, String.format(Locale.US, "condition_report_id is: '%s'", condition_report_id));
        
        photograph = getPhotographManager().getPhotographByPhotographId(photograph_id);
        Log.d(TAG, String.format(Locale.US, "photograph is: '%s'", photograph));
        assert(photograph != null);
    } // protected void onCreate(Bundle savedInstanceState)
    
    public Photograph getPhotograph()
    {
        return photograph;
    } // public Photograph getPhotograph()
    
    /**
     * Get the PhotographManager instance for the application.
     * 
     * @return PhotographManager instance.
     */
    public PhotographManager getPhotographManager()
    {
        final String TAG = HEADER_TAG + "::getPhotographManager";
        Log.d(TAG, "Entry");        
        return ((ApplicationContext)getApplication()).getPhotographManager();
    } // public PhotographManager getPhotographManager()    
    
    public void returnToConditionReport()
    {
        final String TAG = HEADER_TAG + "::returnToConditionReport";
        Log.d(TAG, String.format(Locale.US, "Entry."));
        
        Log.d(TAG, "Finish activity");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("photograph_id", photograph_id);
        intent.putExtra("condition_report_id", condition_report_id);
        startActivity(intent);        
    } // public void returnToConditionReport()

} // public class CameraActivity
