package com.articheck.android;

import java.util.ArrayList;
import java.util.Vector;

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
    private Integer db_version = 6;
    
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
        ArrayList<Exhibition> exhibitions = db.getExhibitions();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ExhibitionsFragment fragment = (ExhibitionsFragment)fm.findFragmentByTag(ExhibitionsFragment.FRAGMENT_TAG);
        Log.d(TAG, "ExhibitionsFragment fragment: " + fragment);
        if (fragment == null)
        {             
            ExhibitionsFragment new_fragment = new ExhibitionsFragment(exhibitions);            
            ft.add(R.id.first_pane, new_fragment, ExhibitionsFragment.FRAGMENT_TAG)
              .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
              
              // Don't want the first pane on the back-stack.  If the user
              // backs out of the first pane then leave the activity.
              //.addToBackStack(null)
              
              .commit();            
        }
        else
        {
            fragment.updateExhibitions(exhibitions, false);
        } // // if (fragment == null)
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