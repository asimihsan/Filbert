package com.articheck.android;

import android.app.Activity;
import android.os.Bundle;

/** Contain the list of all condition reports in the current exhibition and
 * the details of a given condition report.
 * 
 * @author Asim Ihsan
 *
 */
public class ExhibitionActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.AppTheme_Light);
        setContentView(R.layout.activity_exhibition);        
    }
}