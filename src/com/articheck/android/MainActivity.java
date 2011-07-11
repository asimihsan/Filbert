package com.articheck.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import org.json.JSONException;

import com.articheck.android.ConditionReport;
import com.google.common.collect.Sets;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/** Contain the list of all condition reports in the current exhibition and
 * the details of a given condition report.
 * 
 * @author Asim Ihsan
 *
 */
public class MainActivity extends Activity implements OnClickListener {
    final String HEADER_TAG = getClass().getName();
    
    private static final Integer db_version = 32;
    
    private Button add_condition_report_button;
    private Button delete_condition_report_button;
    
    private String exhibition_id;
    private String media_id;
    private String lender_id;
    
    private String photograph_filename;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        final String TAG = HEADER_TAG + "::onCreateOptionsMenu";
        Log.d(TAG, String.format(Locale.US, "Entry. menu: '%s'", menu));
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    } // public boolean onCreateOptionsMenu(Menu menu)    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        final String TAG = HEADER_TAG + "::onOptionsItemSelected";
        Log.d(TAG, String.format(Locale.US, "Entry. item: '%s'", item));
        
        boolean return_value;        
        switch (item.getItemId())
        {
            // -----------------------------------------------------------------
            //  Only start the camera if a condition report is currently
            //  selected.
            // -----------------------------------------------------------------            
            case R.id.camera:              
                Log.d(TAG, "Camera menu item selected.");
                FragmentManager fm = getFragmentManager();
                ConditionReportsFragment fragment = (ConditionReportsFragment)fm.findFragmentByTag(ConditionReportsFragment.FRAGMENT_TAG);                
                if ((fragment != null) && (fragment.getSelectedConditionReport() != null))
                {
                    Log.d(TAG, "A condition report is currently selected.");
                    Intent intent = new Intent(this, CameraActivity.class);
                    intent.putExtra("filename", getPhotographManager().getTemporaryFilename().toString());
                    startActivity(intent);         
                } // if (fragment != null)
                return_value = true;                
                break;
            // -----------------------------------------------------------------                
             
            default:
                return_value = super.onOptionsItemSelected(item);
                break;
        } // switch (item.getItemId())
        
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));
        return return_value;
    } // public boolean onOptionsItemSelected(MenuItem item)
    
    /* 
     * References:
     * 
     * http://stackoverflow.com/questions/1198558/how-to-send-parameters-from-a-notification-click-to-an-activity
     * 
     * (non-Javadoc)
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        
        final String TAG = HEADER_TAG + "::onNewIntent";
        Log.d(TAG, String.format(Locale.US, "Entry. intent: '%s'", intent));        

        photograph_filename = null;
        if ((intent != null) &&
            (intent.getExtras() != null) &&
            (intent.getExtras().containsKey("photograph_filename")))
        {
            Log.d(TAG, "Updating photograph_filepath; hence previous activity was the camera.");
            photograph_filename = intent.getExtras().getString("photograph_filename");             
        }   
        Log.d(TAG, String.format(Locale.US, "Photograph filepath is: %s", photograph_filename));
        
    } // protected void onNewIntent(Intent intent)

    @Override
    protected void onPause() {
        final String TAG = HEADER_TAG + "::onPause";
        Log.d(TAG, "Entry");        
        super.onPause();        

        getDatabaseManager().close();
        getPhotographManager().close();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        final String TAG = HEADER_TAG + "::onResume";
        Log.d(TAG, "Entry");
        
        // ---------------------------------------------------------------------
        // !!AI TODO eventually uncomment this, as this warns against
        // any disk or network I/O in the GUI thread in logcat output.
        // ---------------------------------------------------------------------
        /*
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog().
            .build());
        */       
        // ---------------------------------------------------------------------        
        
        // ---------------------------------------------------------------------
        //  Start up the database connection.
        // ---------------------------------------------------------------------
        Log.d(TAG, "Start up the database connection.");
        DatabaseManager database_manager = new DatabaseManager(ApplicationContext.getContext(), db_version);
        ((ApplicationContext)getApplication()).setDatabaseManager(database_manager);        
        // ---------------------------------------------------------------------
        
        // ---------------------------------------------------------------------
        //  Start up the photograph manager.
        // ---------------------------------------------------------------------
        Log.d(TAG, "Start up the photograph manager.");
        PhotographManager photograph_manager = new PhotographManager(ApplicationContext.getContext(), database_manager);
        ((ApplicationContext)getApplication()).setPhotographManager(photograph_manager);
        // ---------------------------------------------------------------------
        
        // ---------------------------------------------------------------------
        //  If photograph_filepath is not null then the previous activity
        //  was CameraActivity and there is a new photograph we have to
        //  deal with. This means there must be a currently selected
        //  condition report.
        // ---------------------------------------------------------------------
        FragmentManager fm = getFragmentManager();
        ConditionReportsFragment condition_reports_fragment = (ConditionReportsFragment)fm.findFragmentByTag(ConditionReportsFragment.FRAGMENT_TAG);        
        if (photograph_filename != null)
        {
            Log.d(TAG, String.format(Locale.US, "New photograph at filename: '%s'", photograph_filename));
            assert(condition_reports_fragment != null);
            assert(condition_reports_fragment.getSelectedConditionReport() != null);
            
            ConditionReport selected_condition_report = condition_reports_fragment.getSelectedConditionReport();
            boolean return_code = getPhotographManager().addPhotograph(photograph_filename, selected_condition_report);
        } // if (photograph_filepath != null)
        // ---------------------------------------------------------------------        

        // ---------------------------------------------------------------------
        //  Set up click listeners for the add/delete buttons.
        // ---------------------------------------------------------------------        
        add_condition_report_button = (Button) findViewById(R.id.first_pane_button_add_condition_report);
        delete_condition_report_button = (Button) findViewById(R.id.first_pane_button_delete_condition_report);
        assert(add_condition_report_button != null);
        assert(delete_condition_report_button != null);
        add_condition_report_button.setOnClickListener(this);
        delete_condition_report_button.setOnClickListener(this);
        // ---------------------------------------------------------------------        
        
        // ---------------------------------------------------------------------
        //  Populate the first pane list, if it doesn't already exist.
        // ---------------------------------------------------------------------
        Log.d(TAG, "Populate the first pane.");
        
        // TODO connect me with a real exhibition ID selected from a list
        // of exhibition.
        exhibition_id = "1";
        media_id = "2";
        lender_id = "1";
        
        List<ConditionReport> condition_reports;
        try {
            Log.d(TAG, "Get condition reports by exhibition ID: " + exhibition_id);
            condition_reports = database_manager.getConditionReportsByExhibitionId(exhibition_id);
        } catch (JSONException e1) {
            Log.e(TAG, "Exception while getting condition reports", e1);
            return;
        }        
        Log.d(TAG, "ConditionReportsFragment fragment: " + condition_reports_fragment);
        FragmentTransaction ft = fm.beginTransaction();
        try {
            if (condition_reports_fragment == null)
            {                         
                ConditionReportsFragment new_fragment = new ConditionReportsFragment(condition_reports);                
                ft.add(R.id.first_pane_list, new_fragment, ConditionReportsFragment.FRAGMENT_TAG)
                  .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                  
                  // Don't want the first pane on the back-stack.  If the user
                  // backs out of the first pane then leave the activity.
                  //.addToBackStack(null)              
                  .commit();            
            }
            else
            {
                condition_reports_fragment.updateConditionReports(condition_reports, false);
            } // // if (fragment == null)
        } catch (JSONException e) {
            Log.e(TAG, "Exception on creating condition reports list.", e);
        } // try/catch
        // ---------------------------------------------------------------------
        
    } // protected void onResume()

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final String TAG = HEADER_TAG + "::onCreate";
        Log.d(TAG, String.format(Locale.US, "Entry. savedInstanceState: '%s'", savedInstanceState));        
        
        this.setTheme(R.style.AppTheme_Light);
        setContentView(R.layout.activity_main);        
    } // public void onCreate(Bundle savedInstanceState)
    
    /**
     * Get the DatabaseManager instance for the application.
     * 
     * @return DatabaseManager instance that can be used to execute database
     * queries.
     */
    public DatabaseManager getDatabaseManager()
    {
        final String TAG = HEADER_TAG + "::getDatabaseManager";
        Log.d(TAG, "Entry");
        return ((ApplicationContext)getApplication()).getDatabaseManager();
    } // public DatabaseManager getDatabaseManager()
    
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

    public void onClick(View v)
    {
        final String TAG = HEADER_TAG + "::onClick";
        Log.d(TAG, String.format(Locale.US, "Entry. View v: '%s'", v));
        
        FragmentManager fm = getFragmentManager();
        ConditionReportsFragment fragment = (ConditionReportsFragment)fm.findFragmentByTag(ConditionReportsFragment.FRAGMENT_TAG);
        if (fragment != null)
        {
            Log.d(TAG, "ConditionReportsFragment is not null");
            if (v == add_condition_report_button)
            {
                Log.d(TAG, "Clicked add condition report button.");
                List<ConditionReport> condition_reports;
                try
                {                    
                    condition_reports = getDatabaseManager().getConditionReportsByExhibitionId(exhibition_id);
                }
                catch (JSONException e)
                {
                    Log.e(TAG, "Exception while getting condition reports.", e);
                    return;
                }
                    
                Set<String> condition_report_titles = Sets.newHashSet();
                for (ConditionReport condition_report : condition_reports)
                {
                    condition_report_titles.add(condition_report.getTitle());
                } // for (ConditionReport condition_report : condition_report)
                
                // Find out what title we should be using
                int condition_report_number = 0;
                String contents;
                for (;;)
                {
                    Log.d(TAG, String.format(Locale.US, "Trying condition_report_number: '%s'", condition_report_number));
                    String new_condition_report_title = String.format(Locale.US, "New condition report %s", condition_report_number);
                    if (!condition_report_titles.contains(new_condition_report_title))
                    {
                        Log.d(TAG, String.format(Locale.US, "Condition report title found: '%s'", new_condition_report_title));
                        contents = ConditionReport.getEmptyConditionReportContents(new_condition_report_title);
                        break;
                    } // if (!condition_report_titles.contains(new_condition_report_title))
                    condition_report_number++;
                } // infinite for                     
                getDatabaseManager().addConditionReport(exhibition_id, media_id, lender_id, contents);
            }
            else if (v == delete_condition_report_button)
            {
                Log.d(TAG, "Clicked delete condition report button.");
                ConditionReport selected_condition_report = fragment.getSelectedConditionReport();
                getDatabaseManager().deleteConditionReport(selected_condition_report);                
            } // if (type of button)            
            
            if ((v == add_condition_report_button) || (v == delete_condition_report_button))
            {
                Log.d(TAG, "Update the list of condition reports.");
                List<ConditionReport> condition_reports;
                try
                {
                    condition_reports = getDatabaseManager().getConditionReportsByExhibitionId(exhibition_id);
                    fragment.updateConditionReports(condition_reports, true);                    
                }
                catch (JSONException e)
                {
                    Log.e(TAG, "Exception while updating condition report list.", e);
                    return;
                }
            } // if ((v == add_condition_report_button) || (v == delete_condition_report_button))
        } // if (fragment != null)
    } // public void onClick(View v)
} // public class MainActivity extends Activity implements OnClickListener