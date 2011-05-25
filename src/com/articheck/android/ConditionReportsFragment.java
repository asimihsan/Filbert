package com.articheck.android;

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
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        populateTitles();
        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setCacheColorHint(Color.TRANSPARENT);        
    }
    
    private void populateTitles()
    {
        String[] items = new String[100];
        for (int i = 0; i < 100; i++)
        {
            items[i] = "Condition report " + i;
        } // for (int i = 0; i < 10; i++)
        setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.condition_report_list_item, items));
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