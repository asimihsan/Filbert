package com.articheck.android.fragments;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.articheck.android.R;
import com.articheck.android.R.id;
import com.articheck.android.R.layout;
import com.articheck.android.objects.ConditionReport;
import com.google.common.collect.Maps;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

class State
{
    private int top;
    private int position;
    
    public State(int top, int position)
    {
        this.top = top;
        this.position = position;
    } // public State(int top, int position)
    
    public int getTop() {
        return top;
    }
    public void setTop(int top) {
        this.top = top;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }    
} // class State 

/** Display all the condition reports within the current exhibition in a list.
 * On clicking the list we update ConditionReportDetailFragment.
 * 
 * @author Asim Ihsan
 *
 */
public class ConditionReportsFragment extends ListFragment
{
    public final static String FRAGMENT_TAG = "fragment_condition_reports";
    private LinkedHashMap<Integer, ConditionReport> condition_report_lookup;
    private State mState = null;
    
    public ConditionReportsFragment()
    {
        super();
    }
    
    /**
     * Set up the list of exhibitions we will use to populate the list.
     * 
     * @param condition_reports Container full of ConditionReport objects.
     * @throws JSONException 
     */
    public ConditionReportsFragment(List<ConditionReport> condition_reports) throws JSONException
    {
        super();
        updateConditionReports(condition_reports, false);
    } // public ConditionReportsFragment(ArrayList<ConditionReport> condition_reports)    
    
    public void updateConditionReports(List<ConditionReport> condition_reports, boolean update_ui) throws JSONException
    {
        final String TAG = getClass().getName() + "::updateConditionReports";
        Log.d(TAG, "entry");        
        
        condition_report_lookup = Maps.newLinkedHashMap();
        for (int i = 0; i < condition_reports.size(); i++)
        {
            condition_report_lookup.put(i, condition_reports.get(i));
        } // for (Integer i = 0; i < condition_reports.size(); i++)
        if (update_ui)
        {
            clearConditionReportDetail();
            populateTitles();
        } // if (update_ui)
    }    
    
    public ConditionReport getSelectedConditionReport()
    {
        final String TAG = getClass().getName() + "::getSelectedConditionReport";
        Log.d(TAG, "Entry");
        ConditionReport return_value;
        
        int position = mState.getPosition();
        if (!condition_report_lookup.containsKey(position))
        {
            return_value = null;
        }
        else
        {
            return_value = condition_report_lookup.get(position);
        }
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));
        return return_value;
    } // public ConditionReport getSelectedConditionReport()
    
    @Override
    public void onResume()
    {
        super.onResume();
        final String TAG = getClass().getName() + "::onResume";
        Log.d(TAG, "entry.");        
        
        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setCacheColorHint(Color.TRANSPARENT);
        populateTitles();
        
        // TODO this isn't working.  Not selecting an item from the
        // list on rotation.
        lv.setSelectionFromTop(mState.getPosition(), mState.getTop());
        updateConditionReportDetail(mState.getPosition());
    }
    
    @Override
    public void onCreate(Bundle inState)
    {
        super.onCreate(inState);
        final String TAG = getClass().getName() + "::onCreate";
        Log.d(TAG, "entry.");
        Log.d(TAG, "mState: " + mState);
        if (mState == null)
        {
            Log.d(TAG, "mState is null so initialise to -1, -1.");
            mState = new State(-1, -1);
        } // if (mState == null)        
        if (inState != null)
        {
            // Restore the selected item and the scroll position.
            Log.d(TAG, "Restore the selected item and the scroll position.");
            int top = inState.getInt("top");
            int position = inState.getInt("position");
            Log.d(TAG, "top: " + top + "position: " + position);
            mState.setTop(top);
            mState.setPosition(position);            
        } // if (inState != null)        
    } // public void onCreate(Bundle saved)
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        final String TAG = getClass().getName() + "::onSaveInstanceState";
        Log.d(TAG, "entry.");        
        
        // Get the currently selected item and the exactly amount
        // of this item that is currently non-visible; the latter
        // is so that we can restore the scroll position.
        int top = mState.getTop();
        int position = mState.getPosition();
        Log.d(TAG, "top: " + top + ", position: " + position);
        outState.putInt("top", top);
        outState.putInt("position", position);
        
    } // public void onSaveInstanceState(Bundle outState)
    
    private void populateTitles()
    {
        final String TAG = getClass().getName() + "::populateTitles";
        Log.d(TAG, "entry. condition_report_lookup: " + condition_report_lookup);
        ArrayList<String> condition_report_strings = new ArrayList<String>(); 
        for (ConditionReport condition_report : condition_report_lookup.values())
        {            
            String title = condition_report.getTitle();
            condition_report_strings.add(title);            
        }
        Log.d(TAG, "Current ListAdapter: " + getListAdapter());
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                                                 R.layout.condition_report_list_item,
                                                 condition_report_strings));
    } // private void populateTitles()
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        final String TAG = getClass().getName() + "::onListItemClick";
        Log.d(TAG, "entry. position: " + position);
        int top = (v == null) ? 0 : v.getTop();
        Log.d(TAG, "top: " + top);
        mState.setTop(top);
        mState.setPosition(position);
        updateConditionReportDetail(position);
    } // private void onListItemClick(ListView l, View v, int position, long id)
    
    private void clearConditionReportDetail()
    {
        final String TAG = getClass().getName() + "::clearConditionReportDetail";
        Log.d(TAG, "Entry");
        
        FragmentManager fm = getFragmentManager();
        ConditionReportDetailFragment fragment = (ConditionReportDetailFragment)fm.findFragmentByTag(ConditionReportDetailFragment.FRAGMENT_TAG);
        FragmentTransaction ft = fm.beginTransaction();
        if (fragment != null)
        {
            Log.d(TAG, "Found ConditionReportDetailFragment.");
            ft.remove(fragment);            
        } // if (fragment != null)
        ft.commit();
        
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i)
        {    
            fm.popBackStack();
        }        
    } // private void clearConditionReportDetail()
    
    private void updateConditionReportDetail(int position)
    {
        final String TAG = getClass().getName() + "::updateConditionReportDetail";
        Log.d(TAG, "entry. position: " + position);        
        FragmentManager fm = getFragmentManager();
        ConditionReportDetailFragment fragment = (ConditionReportDetailFragment)fm.findFragmentByTag(ConditionReportDetailFragment.FRAGMENT_TAG);
        
        assert(condition_report_lookup.containsKey(position));        
        ConditionReport condition_report = condition_report_lookup.get(position);
        Log.d(TAG, "ConditionReport condition_report: " + condition_report);
        FragmentTransaction ft = fm.beginTransaction();
        
        if (fragment != null)
        {
            Log.d(TAG, "Found ConditionReportDetailFragment.");
            ft.remove(fragment);            
        }        
        
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
        ConditionReportDetailFragment new_fragment = new ConditionReportDetailFragment(condition_report);            
        ft.add(R.id.second_pane, new_fragment, ConditionReportDetailFragment.FRAGMENT_TAG)
          //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
          .addToBackStack(null)
          .commit();        
    } // private void updateConditionReportDetail(int position)
        
} // public class ConditionReportsFragment extends ListFragment