package com.articheck.android;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/** Contain the list of all condition reports in the current exhibition and
 * the details of a given condition report.
 * 
 * @author Asim Ihsan
 *
 */
public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        final String TAG = getClass().getName() + "::onCreate";
        Log.d(TAG, "Entry");        
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.AppTheme_Light);
        setContentView(R.layout.activity_main);        
        
        // ---------------------------------------------------------------------
        //  Populate the first pane.
        // ---------------------------------------------------------------------
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ConditionReportsFragment fragment = (ConditionReportsFragment)fm.findFragmentByTag(ConditionReportsFragment.FRAGMENT_TAG);
        if (fragment == null)
        {
            ConditionReportsFragment new_fragment = new ConditionReportsFragment();            
            ft.add(R.id.first_pane, new_fragment, ConditionReportsFragment.FRAGMENT_TAG)
              .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
              .addToBackStack(null)
              .commit();            
        } // if (fragment == null)
        // ---------------------------------------------------------------------
        
    } // public void onCreate(Bundle savedInstanceState)
}