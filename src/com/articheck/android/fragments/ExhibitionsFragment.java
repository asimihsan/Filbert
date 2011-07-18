/*
 * TODO
 * - Rotation no longer force closes but we need to preserve the selected
 * item on rotation.
 * 
 */

package com.articheck.android.fragments;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.articheck.android.R;
import com.articheck.android.R.layout;
import com.articheck.android.objects.Exhibition;

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
        // TODO
        final String TAG = getClass().getName() + "::onListItemClick";
        Log.d(TAG, "entry");        
        
    } // private void onListItemClick(ListView l, View v, int position, long id)
    
    private void populateTitles()
    {
        final String TAG = getClass().getName() + "::populateTitles";
        Log.d(TAG, "entry. exhibition_lookup: " + exhibition_lookup);
        ArrayList<String> exhibition_strings = new ArrayList<String>();
        for(Integer i = 0; i < exhibition_lookup.size(); i++)
        {
            Exhibition exhibition = exhibition_lookup.get(i);
            exhibition_strings.add(exhibition.getExhibitionName());
        } // for(Integer i = 0; i < exhibitions.size(); i++)
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                                                 R.layout.condition_report_list_item,
                                                 exhibition_strings));
    } // private void populateTitles()    
        
} // public class ExhibitionsFragment extends ListFragment