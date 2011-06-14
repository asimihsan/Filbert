package com.articheck.android;

import java.util.LinkedHashMap;
import java.util.Map;

import com.articheck.android.DatabaseManager.ConditionReport;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Display the details of the condition report selected in
 * ConditionReportFragments. 
 * 
 * @author Asim Ihsan
 *
 */
public class ConditionReportDetailFragment extends Fragment
{
    final static String FRAGMENT_TAG = "fragment_condition_report_detail";
    private View  mContentView;
    private Map<String, View> lookup_field_to_view = null;
    private ConditionReport mConditionReport = null;
    private JSONArray json_template;
    
    public ConditionReportDetailFragment()
    {
        super();
    }

    public ConditionReportDetailFragment(ConditionReport condition_report)
    {
        super();
        final String TAG = getClass().getName() + "Constructor with ConditionReport";
        Log.d(TAG, "Entry.  condition_report: " + condition_report);        
        this.mConditionReport = condition_report;        
    }
    
    @Override
    public void onResume()
    {
        final String TAG = getClass().getName() + "::onResume";
        Log.d(TAG, "Entry");        
        super.onResume();
        Log.d(TAG, "Update condition_report using: " + mConditionReport);
        if (mConditionReport != null) 
        {
            try {
                updateContent(mConditionReport);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "exception updating content", e);
            } // try/catch            
        } // if (mConditionReport != null)
    } // public void onResume()

    
    /* (non-Javadoc)
     * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     * 
     * References:
     * 
     * http://stackoverflow.com/questions/2305395/laying-out-views-in-relativelayout-programmatically
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final String TAG = getClass().getName() + "::onCreateView";
        Log.d(TAG, "Entry");
        
        //mContentView = inflater.inflate(R.layout.fragment_condition_report_detail, null);
        json_template = null;
        if (mConditionReport != null)
        {
            try {
                json_template = new JSONArray(mConditionReport.template_contents);
            } catch (JSONException e) {
                Log.e(TAG, "Exception while decoding template contents", e);
                return null;
            }
        } // if (mConditionReport != null)      
        Log.d(TAG, "json_template: " + json_template);
        
        Activity activity = getActivity();
        Resources resources = activity.getResources();

        mContentView = new ScrollView(activity);
        ((ScrollView) mContentView).setFillViewport(true);
        mContentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
                                                      LayoutParams.MATCH_PARENT));        
        mContentView.setPadding(resources.getDimensionPixelSize(R.dimen.body_padding_large),
                                resources.getDimensionPixelSize(R.dimen.body_padding_medium),
                                resources.getDimensionPixelSize(R.dimen.body_padding_large),
                                resources.getDimensionPixelSize(R.dimen.body_padding_medium));
        TableLayout table_layout = new TableLayout(activity);
        table_layout.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, 
                                                                  TableLayout.LayoutParams.WRAP_CONTENT));
        if (json_template != null)
        {
            Log.d(TAG, "Setting up view with JSON template.");
            try {
                lookup_field_to_view = new LinkedHashMap<String, View>();
                int template_size = json_template.length();
                Log.d(TAG, "template_size: " + template_size);
                for (int i = 0; i < template_size; ++i)
                {
                    JSONObject element = json_template.getJSONObject(i);
                    String type = element.getString("type");
                    Log.d(TAG, "Index: " + i + ", type is: " + type);
                    if (type.equals("text"))
                    {
                        Log.d(TAG, "Index: " + i + ", is a text field");        
                        
                        // -----------------------------------------------------
                        //  Create the row that holds the label and edit fields.
                        // -----------------------------------------------------
                        TableRow row_view = new TableRow(activity);
                        // -----------------------------------------------------                        
                        
                        // -----------------------------------------------------
                        //  Left-hand label describing the text field.
                        // -----------------------------------------------------
                        TextView label_view = new TextView(activity);
                        label_view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
                                                                    LayoutParams.WRAP_CONTENT));
                        String friendly_name = element.getString("friendly_name");
                        label_view.setText(friendly_name);
                        Log.d(TAG, "Set label_view " + label_view + " id to " + (i*2+1));
                        label_view.setId(i*2+1);
                        // -----------------------------------------------------
                        
                        // -----------------------------------------------------
                        //  Right-hand editable text field.
                        // -----------------------------------------------------                        
                        EditText text_view = new EditText(activity);
                        Log.d(TAG, "Set label_view " + label_view + " id to " + (i*2+2));
                        text_view.setId(i*2+2);
                        text_view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
                                                                   LayoutParams.WRAP_CONTENT));
                        String internal_name = element.getString("internal_name");
                        if (internal_name.equals("title"))
                        {
                            Log.d(TAG, "Index " + i + " is the title.");
                            text_view.setTextAppearance(activity, R.style.TextHeader);
                        } // if (internal_name.equals("title"))
                        // -----------------------------------------------------                        
                        
                        TableRow.LayoutParams label_lp = new TableRow.LayoutParams();
                        row_view.addView(label_view, label_lp);
                        TableRow.LayoutParams text_lp = new TableRow.LayoutParams();
                        row_view.addView(text_view, text_lp);
                        table_layout.addView(row_view);                        
                        
                        lookup_field_to_view.put(internal_name, text_view);                    
                    } // if (type == "text")
                    
                } // for (int i = 0; i < template_size; ++i)
            } catch (JSONException e) {
                Log.e(TAG, "Exception creating view from template.", e);
                return null;
            } // try/catch
        } // if (json_template != null)        
        
        mContentView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                final String TAG = getClass().getName() + "::onCreateView::onLongClick";
                Log.d(TAG, "Entry");
                
                final FragmentManager fm = getFragmentManager();
                final ConditionReportsFragment f = (ConditionReportsFragment)fm.findFragmentByTag(ConditionReportsFragment.FRAGMENT_TAG);
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                
                View v = getActivity().findViewById(R.id.first_pane);
                Log.d(TAG, "View v: " + v);
                Log.d(TAG, "ConditionReportsFragment f: " + f);               
                if (f.isVisible())
                {                    
                    ft.hide(f);                    
                    v.setVisibility(View.GONE);                               
                } 
                else   
                {
                    ft.show(f);
                    v.setVisibility(View.VISIBLE);
                } // if (c.isVisible())
                
                // TODO this doesn't work, can't back out.
                ft.addToBackStack(null)
                  .commit();
                return true;
            }
        });
        
        Log.d(TAG, "Returning: " + mContentView);
        ((ViewGroup) mContentView).addView(table_layout);
        return mContentView;
    } // public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)    
    
    /**
     * @param condition_report 
     * @throws JSONException 
     */
    public void updateContent(ConditionReport condition_report) throws JSONException
    {
        // Get and update the title.
        final String TAG = getClass().getName() + "::updateContent";
        Log.d(TAG, "entry.  condition_report: " + condition_report);
        
        View v = getView();
        Log.d(TAG, "getView() result: " + v);
        
        JSONObject json_object = new JSONObject(condition_report.contents);
        Log.d(TAG, "lookup_field_to_value size: " + lookup_field_to_view.size());
        for (Map.Entry<String, View> entry : lookup_field_to_view.entrySet())
        {
            String value = json_object.getString(entry.getKey());
            EditText view = (EditText) entry.getValue();
            Log.d(TAG, "Setting TextView " + view + " to value " + value);
            view.setText(value);
        } // for (Map.Entry<String, View> entry : lookup_field_to_view.entrySet())        
    }
        
} // public class ConditionReportsFragment extends ListFragment