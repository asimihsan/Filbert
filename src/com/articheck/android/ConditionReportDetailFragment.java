package com.articheck.android;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.articheck.android.ConditionReport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private Map<String, View> lookup_text_to_view = null;
    private Map<String, View> lookup_check_to_view = null;
    private Map<String, View> lookup_radio_to_view = null;
    private ConditionReport mConditionReport = null;
    private JSONArray json_template;
    
    static class LookupView
    {
        
    }
    
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
        
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();        
        
        //mContentView = inflater.inflate(R.layout.fragment_condition_report_detail, null);
        json_template = null;
        if (mConditionReport != null)
        {
            try {
                json_template = new JSONArray(mConditionReport.getTemplateContents());
            } catch (JSONException e) {
                Log.e(TAG, "Exception while decoding template contents", e);
                return null;
            }
        } // if (mConditionReport != null)      
        Log.d(TAG, "json_template: " + json_template);
        
        Activity activity = getActivity();
        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();

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
                lookup_text_to_view = new LinkedHashMap<String, View>();
                lookup_check_to_view = new LinkedHashMap<String, View>();
                lookup_radio_to_view = new LinkedHashMap<String, View>();
                int template_size = json_template.length();
                Log.d(TAG, "template_size: " + template_size);                
                for (int i = 0; i < template_size; ++i)
                {
                    JSONObject element = json_template.getJSONObject(i);
                    String type = element.getString("type");
                    String internal_name = element.getString("internal_name");
                    Log.d(TAG, "Index: " + i + ", type is: " + type);
                    
                    // ---------------------------------------------------------
                    //  Create the row that holds the label and edit fields.
                    // ---------------------------------------------------------
                    TableRow row_view = new TableRow(activity);
                    // ---------------------------------------------------------                        
                    
                    // ---------------------------------------------------------
                    //  Left-hand label describing the text field.
                    // ---------------------------------------------------------
                    TextView label_view = new TextView(activity);
                    label_view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
                                                                LayoutParams.WRAP_CONTENT));
                    String friendly_name = element.getString("friendly_name");
                    label_view.setText(friendly_name);
                    Log.d(TAG, "Set label_view " + label_view + " id to " + (i*2+1));
                    label_view.setId(i*2+1);
                    
                    TableRow.LayoutParams label_lp = new TableRow.LayoutParams();
                    row_view.addView(label_view, label_lp);                    
                    // ---------------------------------------------------------
                    
                    if (type.equals("text"))
                    {
                        Log.d(TAG, "Index: " + i + ", is a text field");
                        
                        // -----------------------------------------------------
                        //  Right-hand editable text field.
                        // -----------------------------------------------------                        
                        EditText text_view = new EditText(activity);
                        Log.d(TAG, "Set label_view " + label_view + " id to " + (i*2+2));
                        text_view.setId(i*2+2);
                        text_view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
                                                                   LayoutParams.WRAP_CONTENT));                        
                        if (internal_name.equals("title"))
                        {
                            Log.d(TAG, "Index " + i + " is the title.");
                            text_view.setTextAppearance(activity, R.style.TextHeader);
                        } // if (internal_name.equals("title"))
                        // -----------------------------------------------------                        
                        
                        TableRow.LayoutParams text_lp = new TableRow.LayoutParams();
                        row_view.addView(text_view, text_lp);
                        
                        lookup_text_to_view.put(internal_name, text_view);
                    } 
                    else if (type.equals("check"))
                    {
                        // -----------------------------------------------------
                        //  Right-hand check boxes.
                        // -----------------------------------------------------                        
                        Log.d(TAG, "Index: " + i + ", are check boxes.");
                        JSONArray values = element.getJSONArray("values");
                        Log.d(TAG, "Check box values: " + values);
                        Type collection_type = new TypeToken<Collection<String>>() {}.getType();
                        Collection<String> decoded_values = gson.fromJson(values.toString(), collection_type);
                        Log.d(TAG, "Decoded values: " + decoded_values);
                        
                        LinearLayout linear_layout = new LinearLayout(activity);
                        linear_layout.setOrientation(Configuration.ORIENTATION_PORTRAIT);                                                
                        TableRow.LayoutParams linear_layout_lp = new TableRow.LayoutParams();
                        
                        for (String value : decoded_values)
                        {
                            CheckBox check_box = new CheckBox(activity);
                            LinearLayout.LayoutParams check_box_lp = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
                                                                                                                     LayoutParams.MATCH_PARENT));
                            check_box.setText(value);
                            linear_layout.addView(check_box, check_box_lp);
                        } // for (String value : decoded_values)                        
                        row_view.addView(linear_layout, linear_layout_lp);
                        
                        //lookup_check_to_view(internal_name, check_box);
                    } 
                    else if (type.equals("radio"))
                    {
                        // -----------------------------------------------------
                        //  Right-hand radio group.
                        // -----------------------------------------------------                        
                        Log.d(TAG, "Index: " + i + ", is radio group.");                        
                        JSONArray values = element.getJSONArray("values");                        
                        Log.d(TAG, "Radio group values: " + values);
                        Type collection_type = new TypeToken<Collection<String>>() {}.getType();
                        Collection<String> decoded_values = gson.fromJson(values.toString(), collection_type);
                        Log.d(TAG, "Decoded values: " + decoded_values);                        
                        
                        RadioGroup radio_group = new RadioGroup(activity);
                        for (String value : decoded_values)
                        {
                            RadioButton radio_button = new RadioButton(activity);
                            radio_button.setText(value);                            
                            radio_group.addView(radio_button);
                        } // for (String value : decoded_values)                        
                        TableRow.LayoutParams radio_group_lp = new TableRow.LayoutParams();
                        row_view.addView(radio_group, radio_group_lp);                        
                    } // if (type of template)                    
                    
                    table_layout.addView(row_view);
                    
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
        
        JSONObject json_object = new JSONObject(condition_report.getContents());
        Log.d(TAG, "lookup_field_to_value size: " + lookup_text_to_view.size());
        for (Map.Entry<String, View> entry : lookup_text_to_view.entrySet())
        {
            String value = json_object.getString(entry.getKey());
            EditText view = (EditText) entry.getValue();
            Log.d(TAG, "Setting TextView " + view + " to value " + value);
            view.setText(value);
        } // for (Map.Entry<String, View> entry : lookup_text_to_view.entrySet())        
    }
        
} // public class ConditionReportsFragment extends ListFragment