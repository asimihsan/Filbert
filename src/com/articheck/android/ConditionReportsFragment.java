package com.articheck.android;

import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
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
        ConditionReportDetailFragment fragment = (ConditionReportDetailFragment)getFragmentManager().findFragmentById(R.id.fragment_condition_report_detail);
        fragment.updateContent("Condition report title " + position);
    } // private void updateConditionReportDetail(int position)
        
} // public class ConditionReportsFragment extends ListFragment