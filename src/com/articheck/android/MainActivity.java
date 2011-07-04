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
import android.os.Bundle;
import android.util.Log;
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
    private DatabaseManager db;
    private static final Integer db_version = 30;
    
    Button add_condition_report_button;
    Button delete_condition_report_button;
    
    String exhibition_id;
    String media_id;
    String lender_id;
    
    @Override
    protected void onPause() {
        final String TAG = getClass().getName() + "::onPause";
        Log.d(TAG, "Entry");        
        super.onPause();
        db.close();
    }

    @Override
    protected void onResume() {
        final String TAG = getClass().getName() + "::onResume";
        Log.d(TAG, "Entry");        
        super.onResume();
        
        // ---------------------------------------------------------------------
        //  Start up the database connection.
        // ---------------------------------------------------------------------
        Log.d(TAG, "Start up the database connection.");
        db = new DatabaseManager(ApplicationContext.getContext(), db_version);                       
        // ---------------------------------------------------------------------

        // ---------------------------------------------------------------------
        //  Set up click listeners for the add/delete buttons.
        // ---------------------------------------------------------------------        
        add_condition_report_button = (Button) findViewById(R.id.first_pane_button_add_condition_report);
        delete_condition_report_button = (Button) findViewById(R.id.first_pane_button_delete_condition_report);
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
            condition_reports = db.getConditionReportsByExhibitionId(exhibition_id);
        } catch (JSONException e1) {
            Log.e(TAG, "Exception while getting condition reports", e1);
            return;
        }
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ConditionReportsFragment fragment = (ConditionReportsFragment)fm.findFragmentByTag(ConditionReportsFragment.FRAGMENT_TAG);
        Log.d(TAG, "ConditionReportsFragment fragment: " + fragment);
        try {
            if (fragment == null)
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
                fragment.updateConditionReports(condition_reports, false);
            } // // if (fragment == null)
        } catch (JSONException e) {
            Log.e(TAG, "Exception on creating condition reports list.", e);
        } // try/catch
        // ---------------------------------------------------------------------        
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        final String TAG = getClass().getName() + "::onCreate";
        Log.d(TAG, "Entry");        
        super.onCreate(savedInstanceState);
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
        return db;
    } // public DatabaseManager getDatabaseManager()

    public void onClick(View v)
    {
        final String TAG = getClass().getName() + "::onClick";
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
                    condition_reports = db.getConditionReportsByExhibitionId(exhibition_id);
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
                db.addConditionReport(exhibition_id, media_id, lender_id, contents);
            }
            else if (v == delete_condition_report_button)
            {
                Log.d(TAG, "Clicked delete condition report button.");
                ConditionReport selected_condition_report = fragment.getSelectedConditionReport();
                db.deleteConditionReport(selected_condition_report);                
            } // if (type of button)            
            
            if ((v == add_condition_report_button) || (v == delete_condition_report_button))
            {
                Log.d(TAG, "Update the list of condition reports.");
                List<ConditionReport> condition_reports;
                try
                {
                    condition_reports = db.getConditionReportsByExhibitionId(exhibition_id);
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