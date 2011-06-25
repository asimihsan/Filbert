package com.articheck.android.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.SimpleGraph;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.Objects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import android.util.Log;

/**
 * @author ai
 *
 */
public class Json
{
    /**
     * Convert a JSONArray of String objects to a List<String>.
     * 
     * @param json_array JSONArray of String objects.
     * @return A list of strings corresponding to the contents of the
     * JSONArray.
     */
    public static List<String> JsonArrayToList(JSONArray json_array)
    {   
        final String TAG = Json.class.getName() + "::JsonArrayToList";
        Log.d(TAG, "Entry");
        
        int size = json_array.length();
        List<String> return_value = new ArrayList<String>(size);
        for (int i = 0; i < size; i++)
        {
            String value = json_array.optString(i);
            Log.d(TAG, String.format(Locale.US, "Value at index '%s' is '%s'", i, value));
            return_value.add(value);
        } // for (int i = 0; i < size; i++)
        
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));
        return return_value;
    } // public static List<String> JsonArrayToList(JSONArray json_array)
    
    /**
     * Wrapper around a condition report's decoded JSON contents.  It can either
     * start of with the existing JSONObject corresponding to the contents
     * or start off from scratch.
     *  
     * @author ai
     *
     */
    public static class ConditionReportContentsJsonWrapper
    {
        static class Node
        {
            private String key;
            private List<String> values;
            private boolean single_value;
            
            Node(String key, List<String> values)
            {
                this.key = key;
                this.single_value = false;                
                this.values = new ArrayList<String>(values);
            } //Node(String key, T value)
            
            Node(String key, String value)
            {
                this.key = key;
                this.single_value = true;                
                this.values = new ArrayList<String>(1);
                this.values.add(value);
            } //Node(String key, T value)
            
            Node(String key)
            {
                this.key = key;
                this.values = null;                
            }
            
            public String getKey()
            {
                return key;
            }
            
            public boolean getSingleValue()
            {
                return single_value;
            }            
            
            public List<String> getValues()
            {
                if (single_value)
                {
                    throw new IllegalStateException("getValues() call only valid for non-single-value nodes.");
                } // if (single_value)
                return new ArrayList<String>(values);
            } // public List<String> getValues()            

            
            public void setValue(List<String> values)
            {
                if (single_value)
                {
                    throw new IllegalStateException("setValues() call only valid for non-single-value nodes.");
                } // if (!single_value)
                values = new ArrayList<String>(values);                            
            } // public void setValue(String value)            
            
            public String getValue()
            {
                if (!single_value)
                {
                    throw new IllegalStateException("getValue() call only valid for single-value nodes.");
                } // if (!single_value)
                return values.get(0);
            } // public String getValue()
            
            public void setValue(String value)
            {
                if (!single_value)
                {
                    throw new IllegalStateException("setValue() call only valid for single-value nodes.");
                } // if (!single_value)
                values.set(0, value);                                
            } // public void setValue(String value)            
            
            @Override
            public boolean equals(Object o)
            {
                if (o == this)
                {
                    return true;
                } // if (o == this)
                if (!(o instanceof Node))
                {
                    return false;
                } // if (!(o instanceof Field))
                Node node = (Node) o;
                return Objects.equal(getKey(), node.getKey());
            } // public boolean equals(Object o)
            
            @Override
            public int hashCode()
            {
                return Objects.hashCode(getKey());
            } // public int hashCode()
            
            @Override
            public String toString()
            {
                return Objects.toStringHelper(this)
                               .add("key", key)
                               .add("values", values)
                               .add("single_value", single_value)
                               .toString();
            } // public String toString()
            
        } // static class Node
        
        private JSONObject json_object;
        private DirectedMultigraph<Node, DefaultEdge> graph;        
        
        public ConditionReportContentsJsonWrapper(JSONObject json_object)
        {
            this.json_object = json_object;
            initialize();
        } // ConditionReportContentsJsonWrapper(JSONObject json_object)
        
        public ConditionReportContentsJsonWrapper()
        {
            this.json_object = new JSONObject();
            initialize();
        } // ConditionReportContentsJsonWrapper()
        
        public JSONObject getJsonObject()
        {
            return json_object;
        }
        
        private void initialize()
        {
            final String TAG = getClass().getName() + "::initialize";
            Log.d(TAG, "Entry.");            
            
            graph = new DirectedMultigraph<Node, DefaultEdge>(DefaultEdge.class);            
            JSONArray section_objects = json_object.names();
            for (String section_name : JsonArrayToList(section_objects))
            { 
                Log.d(TAG, String.format(Locale.US, "Considering section_name: '%s'", section_name));
                Node section_node = new Node(section_name, "__section");
                if (!graph.addVertex(section_node))
                {
                    Log.e(TAG, String.format(Locale.US, "section_name '%s' already exists in graph.", section_name));
                } // if (!graph.addVertex(section_node))
                
                JSONObject fields = json_object.optJSONObject(section_name);
                JSONArray field_names = fields.names();
                for (String field_name : JsonArrayToList(field_names))
                {
                    Log.d(TAG, String.format(Locale.US, "Considering field_name: '%s'", field_name));
                    String section_field_key = getSectionFieldName(section_name, field_name);
                    Log.d(TAG, String.format(Locale.US, "section_field_key: '%s'", section_field_key));
                    
                    JSONArray json_array = fields.optJSONArray(field_name);
                    if (json_array == null)
                    {
                        Log.d(TAG, "Field is not a JSONArray, hence is a string.");                        
                        String field_value = fields.optString(field_name);
                        Node field_node = new Node(section_field_key, field_value);
                        if (!graph.addVertex(field_node))
                        {
                            Log.e(TAG, String.format(Locale.US, "field_name '%s' already exists in graph.", field_name));
                        } // if (!graph.addVertex(field_node))
                        graph.addEdge(section_node, field_node);
                    }
                    else
                    {
                        Log.d(TAG, "Field is a JSONArray.");
                        List<String> field_values = JsonArrayToList(json_array);
                        Node field_node = new Node(section_field_key, field_values);
                        if (!graph.addVertex(field_node))
                        {
                            Log.e(TAG, String.format(Locale.US, "field_name '%s' already exists in graph.", field_name));
                        } // if (!graph.addVertex(field_node))
                        graph.addEdge(section_node, field_node);                        
                    } // if (json_array == null)
                } // for (String field_name: JsonArrayToList(field_names))
            } // for (String section_name : JsonArrayToList(section_objects))
        } // private initialize()
        
        private String getSectionFieldName(String section_name, String field_name)
        {
            return String.format(Locale.US, "%s -> %s", section_name, field_name);
        }
        
        private Node getNode(String section_name, String field_name)
        {
            final String TAG = getClass().getName() + "::getNode";
            Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s'", section_name, field_name));            
            
            String section_field_key = getSectionFieldName(section_name, field_name);
            Log.d(TAG, String.format(Locale.US, "section_field_key: '%s'", section_field_key));
            
            Node section_node = new Node(section_name);
            Node field_node = new Node(section_field_key);
            DefaultEdge edge = graph.getEdge(section_node, field_node);
            Log.d(TAG, String.format(Locale.US, "Edge: '%s'", edge));
            if (edge == null)
            {
                Log.d(TAG, "Edge is null, so either section or field does not exist.");
                return null;
            } // if (edge == null)
            
            Node actual_field_node = graph.getEdgeTarget(edge);
            Log.d(TAG, String.format(Locale.US, "Field node: '%s'", actual_field_node));
            
            return actual_field_node;
        } // private Node getNode(String section_name, String field_name)
        
        public String getValueFromSectionNameAndFieldName(String section_name, String field_name)
        {
            final String TAG = getClass().getName() + "::getValueFromSectionNameAndFieldName";
            Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s'", section_name, field_name));
            
            Node actual_field_node = getNode(section_name, field_name);
            if (actual_field_node == null)
            {
                Log.d(TAG, "node returned is null, so either section or field does not exist.");
                return null;
            } // if (actual_field_node == null)
            
            String return_value = actual_field_node.getValue();
            Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));
            return return_value;
            
        } // public getValueFromSectionNameAndFieldName(String section_name, String field_name)
        
        public List<String> getValuesFromSectionNameAndFieldName(String section_name, String field_name)
        {            
            final String TAG = getClass().getName() + "::getValuesFromSectionNameAndFieldName";
            Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s'", section_name, field_name));
            
            Node actual_field_node = getNode(section_name, field_name);            
            if (actual_field_node == null)
            {
                Log.d(TAG, "node returned is null, so either section or field does not exist.");
                return null;
            } // if (actual_field_node == null)            
            
            List<String> return_value = actual_field_node.getValues();
            Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));
            return return_value;            
        } // public getValueFromSectionNameAndFieldName(String section_name, String field_name)        
        
        public String getTitle()
        {
            return getValueFromSectionNameAndFieldName("Basic info", "Title");
        } // public String getTitle()
        
    } // public static class ConditionReportContentsJsonWrapper
    
} // public class Json
