/*
 * TODO
 * - Rotation no longer force closes but we need to preserve the selected
 * item on rotation.
 * 
 */

package com.articheck.android;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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
public class ExhibitionsFragment extends ListFragment
{
    final static String FRAGMENT_TAG = "fragment_exhibitions";    
    private LinkedHashMap<Integer, Exhibition> exhibition_lookup;
    
    public ExhibitionsFragment()
    {
        super();
    }
    
    /**
     * Set up the list of exhibitions we will use to populate the list.
     * 
     * @param exhibitions Container full of exhibitions, i.e. Exhibition
     * objects.
     */
    public ExhibitionsFragment(ArrayList<Exhibition> exhibitions)
    {
        super();
        updateExhibitions(exhibitions, false);
    } // public ExhibitionsFragment(ArrayList<Exhibition> exhibitions)
    
    public void updateExhibitions(ArrayList<Exhibition> exhibitions, Boolean update_ui)
    {
        final String TAG = getClass().getName() + "::updateExhibitions";
        Log.d(TAG, "entry");        
        
        exhibition_lookup = new LinkedHashMap<Integer, Exhibition>();
        for (Integer i = 0; i < exhibitions.size(); i++)
        {
            exhibition_lookup.put(i, exhibitions.get(i));
        } // for (Integer i = 0; i < exhibitions.size(); i++)
        if (update_ui)
        {
            populateTitles();
        } // if (update_ui)
    }

    @Override
    public void onResume()
    {
        final String TAG = getClass().getName() + "::onResume";
        Log.d(TAG, "entry");
        super.onResume();
        populateTitles();
        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setCacheColorHint(Color.TRANSPARENT);        
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        final String TAG = getClass().getName() + "::onListItemClick";
        Log.d(TAG, "entry");        
        updateConditionReportDetail(position);
    } // private void onListItemClick(ListView l, View v, int position, long id)
    
    private void populateTitles()
    {
        final String TAG = getClass().getName() + "::populateTitles";
        Log.d(TAG, "entry. exhibition_lookup: " + exhibition_lookup);
        ArrayList<String> exhibition_strings = new ArrayList<String>();
        for(Integer i = 0; i < exhibition_lookup.size(); i++)
        {
            exhibition_strings.add(exhibition_lookup.get(i).name);
        } // for(Integer i = 0; i < exhibitions.size(); i++)
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                                                 R.layout.condition_report_list_item,
                                                 exhibition_strings));
    } // private void populateTitles()    
    
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
        
} // public class ExhibitionsFragment extends ListFragment