package com.articheck.android;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.articheck.android.DatabaseManager.ConditionReport;
import com.articheck.android.DatabaseManager.Exhibition;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/** Display all the condition reports within the current exhibition in a list.
 * On clicking the list we update ConditionReportDetailFragment.
 * 
 * @author Asim Ihsan
 *
 */
public class ConditionReportsFragment extends ListFragment
{
    final static String FRAGMENT_TAG = "fragment_condition_reports";
    private LinkedHashMap<Integer, ConditionReport> condition_report_lookup;
    
    public ConditionReportsFragment()
    {
        super();
    }
    
    /**
     * Set up the list of exhibitions we will use to populate the list.
     * 
     * @param condition_reports Container full of ConditionReport objects.
     */
    public ConditionReportsFragment(ArrayList<ConditionReport> condition_reports)
    {
        super();
        updateConditionReports(condition_reports, false);
    } // public ConditionReportsFragment(ArrayList<ConditionReport> condition_reports)    
    
    public void updateConditionReports(ArrayList<ConditionReport> condition_reports, Boolean update_ui)
    {
        final String TAG = getClass().getName() + "::updateConditionReports";
        Log.d(TAG, "entry");        
        
        condition_report_lookup = new LinkedHashMap<Integer, ConditionReport>();
        for (Integer i = 0; i < condition_reports.size(); i++)
        {
            condition_report_lookup.put(i, condition_reports.get(i));
        } // for (Integer i = 0; i < condition_reports.size(); i++)
        if (update_ui)
        {
            populateTitles();
        } // if (update_ui)
    }    
    
    @Override
    public void onResume()
    {
        super.onResume();
        populateTitles();
        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setCacheColorHint(Color.TRANSPARENT);        
    }
    
    private void populateTitles()
    {
        final String TAG = getClass().getName() + "::populateTitles";
        Log.d(TAG, "entry. condition_report_lookup: " + condition_report_lookup);
        ArrayList<String> condition_report_strings = new ArrayList<String>();
        for(Integer i = 0; i < condition_report_lookup.size(); i++)
        {
            condition_report_strings.add(condition_report_lookup.get(i).condition_report_id);
        } // for(Integer i = 0; i < exhibitions.size(); i++)
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                                                 R.layout.condition_report_list_item,
                                                 condition_report_strings));
    } // private void populateTitles()
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        updateConditionReportDetail(position);
    } // private void onListItemClick(ListView l, View v, int position, long id)
    
    private void updateConditionReportDetail(int position)
    {
        FragmentManager fm = getFragmentManager();
        ConditionReportDetailFragment fragment = (ConditionReportDetailFragment)fm.findFragmentByTag(ConditionReportDetailFragment.FRAGMENT_TAG);
        if (fragment != null)
        {
            Log.d(getClass().getName() + "::updateConditionReportDetail()", "Found ConditionReportDetailFragment.");
            fragment.updateContent("Condition report title " + position);
        }
        else
        {
            Log.d(getClass().getName() + "::updateConditionReportDetail()", "Could not find ConditionReportDetailFragment.");
            FragmentTransaction ft = fm.beginTransaction();
            
            // When you create the new fragment and associate it with second_pane
            // the FragmentTransaction will not immediately execute.  Hence
            // if you attempt to update the contents of ConditionReportDetailFragment
            // immediately it will fail because the fragment isn't created yet.
            //
            // Hence, pass the desired content into the constructor and
            // trust the fragment to update itself in good time.
            //
            // TODO pass in as a class, or even better a set of pointers
            // to use to dip into the database.
            String title = "Condition report title " + position;
            ConditionReportDetailFragment new_fragment = new ConditionReportDetailFragment(title);            
            ft.add(R.id.second_pane, new_fragment, ConditionReportDetailFragment.FRAGMENT_TAG)
              .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
              .addToBackStack(null)
              .commit();
        } // if (fragment != null)
        
    } // private void updateConditionReportDetail(int position)
        
} // public class ConditionReportsFragment extends ListFragment