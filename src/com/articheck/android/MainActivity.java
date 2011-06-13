package com.articheck.android;

import java.util.ArrayList;
import java.util.Vector;

import org.json.JSONException;

import com.articheck.android.DatabaseManager.ConditionReport;
import com.articheck.android.DatabaseManager.Exhibition;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

/** Contain the list of all condition reports in the current exhibition and
 * the details of a given condition report.
 * 
 * @author Asim Ihsan
 *
 */
public class MainActivity extends Activity {
    private DatabaseManager db;
    private static final Integer db_version = 18;
    
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
        //  Populate the first pane, if it doesn't already exist.
        // ---------------------------------------------------------------------
        Log.d(TAG, "Populate the first pane.");
        
        // TODO connect me with a real exhibition ID selected from a list
        // of exhibition.
        String exhibition_id = "1";
        ArrayList<ConditionReport> condition_reports = db.getConditionReportsByExhibitionId(exhibition_id);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ConditionReportsFragment fragment = (ConditionReportsFragment)fm.findFragmentByTag(ConditionReportsFragment.FRAGMENT_TAG);
        Log.d(TAG, "ConditionReportsFragment fragment: " + fragment);
        try {
            if (fragment == null)
            {                         
                ConditionReportsFragment new_fragment = new ConditionReportsFragment(condition_reports);                
                ft.add(R.id.first_pane, new_fragment, ConditionReportsFragment.FRAGMENT_TAG)
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
}