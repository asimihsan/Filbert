package com.articheck.android.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.SimpleGraph;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Objects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import android.util.Log;

/**
 * @author ai
 *
 */
public class Json
{
    public static JSONArray ListToJsonArray(List<String> strings)
    {
        JSONArray return_value = new JSONArray();
        for (String string : strings)
        {
            return_value.put(string);
        }
        return return_value;
    } // public static JSONArray ListToJsonArray(List<String>)
    
    /**
     * Convert a JSONArray of String objects to a List<String>.
     * 
     * @param json_array JSONArray of String objects.
     * @return A list of strings corresponding to the contents of the
     * JSONArray.
     */
    public static List<String> JsonArrayToList(JSONArray json_array)
    {   
        int size = json_array.length();
        List<String> return_value = new ArrayList<String>(size);
        for (int i = 0; i < size; i++)
        {
            String value = json_array.optString(i);
            return_value.add(value);
        } // for (int i = 0; i < size; i++)
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
        static String TAG_HEADER = ConditionReportContentsJsonWrapper.class.getName();
        
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
            public boolean equals(Object object)
            {
                if (object instanceof Node)
                {
                    Node that = (Node)object;
                    return Objects.equal(this.key, that.key);
                } // if (object instanceof Node)
                return false;                   
            } // public boolean equals(Object o)
            
            @Override
            public int hashCode()
            {
                return Objects.hashCode(key);
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
        private boolean is_json_object_dirty;
        private boolean is_lookup_section_node_dirty;
        private BiMap<String, Node> lookup_section_node;
        
        @Override
        public String toString()
        {
            return Objects.toStringHelper(this)
                           .add("json_object", json_object)
                           .add("graph", graph)
                           .add("is_json_object_dirty", is_json_object_dirty)
                           .add("is_lookup_section_nodes_dirty", is_lookup_section_node_dirty)
                           .toString();
        } // public String toString()
        
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
        
        public JSONObject getJsonObject() throws JSONException
        {
            final String TAG = getClass().getName() + "::getJsonObject";
            Log.d(TAG, "Entry.");            
            if (is_json_object_dirty)
            {
                Log.d(TAG, "json_object is dirty, needs to be updated.");
                updateJsonObject();                       
            } // if (is_json_object_dirty)
            assert(is_json_object_dirty == false);
            return json_object;
        }
        
        /**
         * Convert the graph representation of the contents into a JSONObject.
         * This is the inverse of initialize().
         * @throws JSONException 
         */
        private void updateJsonObject() throws JSONException
        {            
            final String TAG = TAG_HEADER + "::updateJsonObject";
            Log.d(TAG, "Entry.");
            
            json_object = new JSONObject();            
            for (Node section_node : getSectionNodes())
            {
                JSONObject fields = new JSONObject();                    
                for (DefaultEdge edge : graph.outgoingEdgesOf(section_node))
                {
                    Log.d(TAG, String.format(Locale.US, "Considering graph edge: '%s", edge));
                    Node field_node = graph.getEdgeTarget(edge);
                    Log.d(TAG, String.format(Locale.US, "Field node: '%s'", field_node));
                    String field_name = getFieldNameFromSectionFieldKey(field_node.getKey());
                    if (field_node.getSingleValue())
                    {
                        Log.d(TAG, "Single value node.");
                        String field_value = field_node.getValue();                            
                        fields.put(field_name, field_value);                            
                    }
                    else
                    {
                        Log.d(TAG, "Multi value node.");
                        List<String> field_values = field_node.getValues();                            
                        fields.put(field_name, Json.ListToJsonArray(field_values));
                    }                        
                } // for (DefaultEdge edge : graph.outgoingEdgesOf(node))
                String section_name = section_node.getKey();
                json_object.put(section_name, fields);                
            }
                    
            is_json_object_dirty = false;
        } // private void updateJsonObject()
        
        /**
         * Convert the JSONObject contents in json_object into a directed
         * graph.  This is the inverse of updateJsonObject().
         */
        private void initialize()
        {
            final String TAG = TAG_HEADER + "::initialize";
            Log.d(TAG, "Entry.");            
            
            graph = new DirectedMultigraph<Node, DefaultEdge>(DefaultEdge.class);
            refreshLookupSectionNode();
            JSONArray section_objects = json_object.names();
            for (String section_name : JsonArrayToList(section_objects))
            { 
                Log.d(TAG, String.format(Locale.US, "Considering section_name: '%s'", section_name));
                addSection(section_name);
                
                JSONObject fields = json_object.optJSONObject(section_name);
                JSONArray field_names = fields.names();
                for (String field_name : Json.JsonArrayToList(field_names))
                {
                    Log.d(TAG, String.format(Locale.US, "Considering field_name: '%s'", field_name));                    
                    JSONArray json_array = fields.optJSONArray(field_name);
                    if (json_array == null)
                    {
                        Log.d(TAG, "Field is not a JSONArray, hence is a string.");                        
                        String field_value = fields.optString(field_name);
                        addField(section_name, field_name, field_value); 
                    }
                    else
                    {
                        Log.d(TAG, "Field is a JSONArray.");
                        List<String> field_values = JsonArrayToList(json_array);
                        addField(section_name, field_name, field_values);
                    } // if (json_array == null)
                } // for (String field_name: JsonArrayToList(field_names))
            } // for (String section_name : JsonArrayToList(section_objects))
            
            is_json_object_dirty = false;
            refreshLookupSectionNode();
        } // private initialize()
        
        private Node addSection(String section_name)
        {
            final String TAG = TAG_HEADER + "::addSection";
            Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s'", section_name));
                
            Node section_node = new Node(section_name, "__section");
            if (!getSectionNodes().contains(section_node))
            {                
                Log.d(TAG, "Graph does not currently contain a corresponding section node.");
                if (!graph.addVertex(section_node))
                {
                    Log.e(TAG, String.format(Locale.US, "section_name '%s' already exists in graph.", section_name));
                } // if (!graph.addVertex(section_node))                
            }
            is_json_object_dirty = true;
            is_lookup_section_node_dirty = true;
            Log.d(TAG, String.format(Locale.US, "Returning: '%s'", section_node));
            return section_node;            
        } // private Node addSection(String section_name)
        
        private Node addField(String section_name, String field_name, String field_value)
        {
            final String TAG = TAG_HEADER + "::addField";
            Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s', field_value: '%s'", section_name, field_name, field_value));
            
            String section_field_key = getSectionFieldKey(section_name, field_name);
            Log.d(TAG, String.format(Locale.US, "section_field_key: '%s'", section_field_key));
            
            Node section_node = getSectionNode(section_name);
            Node field_node = new Node(section_field_key, field_value); 
            if (!graph.addVertex(field_node))
            {
                Log.e(TAG, String.format(Locale.US, "field_name '%s' already exists in graph.", field_name));
            } // if (!graph.addVertex(field_node))
            if (graph.addEdge(section_node, field_node) == null)
            {
                Log.e(TAG, String.format(Locale.US, "Edge between section_name '%s' and field_name '%s' already exists in graph.", section_name, field_name));
            }
            is_json_object_dirty = true;
            return field_node;             
        } // private Node addField(String section_name, String field_name)        
        
        private Node addField(String section_name, String field_name, List<String> field_values)
        {
            final String TAG = TAG_HEADER + "::addField";
            Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s', field_values: '%s'", section_name, field_name, field_values));
            
            String section_field_key = getSectionFieldKey(section_name, field_name);
            Log.d(TAG, String.format(Locale.US, "section_field_key: '%s'", section_field_key));
            
            Node section_node = getSectionNode(section_name);
            Node field_node = new Node(section_field_key, field_values);
            if (!graph.addVertex(field_node))
            {
                Log.e(TAG, String.format(Locale.US, "field_name '%s' already exists in graph.", field_name));
            } // if (!graph.addVertex(field_node))            
            if (graph.addEdge(section_node, field_node) == null)
            {
                Log.e(TAG, String.format(Locale.US, "Edge between section_name '%s' and field_name '%s' already exists in graph.", section_name, field_name));
            }
            is_json_object_dirty = true;
            return field_node;             
        } // private Node addField(String section_name, String field_name)        
        
        private String getSectionFieldKey(String section_name, String field_name)
        {
            return String.format(Locale.US, "%s -> %s", section_name, field_name);            
        }
        
        static Pattern fieldNameFromSectionFieldKeyPattern = Pattern.compile("^.*? -> (.*?)$");
        private String getFieldNameFromSectionFieldKey(String section_field_key)
        {
            Matcher matcher = fieldNameFromSectionFieldKeyPattern.matcher(section_field_key);
            if (!matcher.find())
            {
                throw new IllegalArgumentException(String.format(Locale.US, "section_field_key '%s' does not match expected pattern.", section_field_key));
            }
            return matcher.group(1);
        }
        
        private void refreshLookupSectionNode()
        {
            final String TAG = "Json::refreshLookupSectionNode";
            Log.d(TAG, "Entry.");
            
            // At this point we know that lookup_section_node is dirty, so
            // we always need to rebuild it.
            ImmutableBiMap.Builder<String, Node> builder = new ImmutableBiMap.Builder<String, Node>();            
            for (Node node : graph.vertexSet())
            {
                Log.d(TAG, String.format(Locale.US, "Considering graph node: '%s", node));
                if (graph.incomingEdgesOf(node).size() == 0)
                {
                    Log.d(TAG, "No incoming edges, so this is a section node.");                    
                    builder.put(node.getKey(), node);
                } // if (graph.incomingEdgesOf(node).size() == 0)
            } // for (Node node : graph.vertexSet())
            
            lookup_section_node = builder.build();
            is_lookup_section_node_dirty = false;
        } // private void refreshLookupSectionNode()
        
        /**
         * Get all the section nodes in the graph, i.e. those nodes with no
         * incoming edges.
         * 
         * @return Collection of Node instances.
         */
        private Set<Node> getSectionNodes()
        {
            final String TAG = TAG_HEADER + "::getSectionNodes";
            Log.d(TAG, "Entry.");
            
            if (!is_lookup_section_node_dirty)
            {
                Log.d(TAG, "lookup_section_node is not dirty.");
                return lookup_section_node.values();
            } // if (!is_lookup_section_node_dirty)            
            refreshLookupSectionNode();
            Set<Node> section_nodes = lookup_section_node.values();
            
            Log.d(TAG, String.format(Locale.US, "Returning: '%s'", section_nodes));
            return section_nodes;
        } // private Set<Node> getSectionNodes()        
        
        /**
         * Get the Node instance in the graph corresponding to the section's
         * name.
         * 
         * @param section_name Name of the section.
         * @return Node instance of the section, or null if the section
         * could not be found.
         */
        private Node getSectionNode(String section_name)
        {
            final String TAG = TAG_HEADER + "::getSectionNode";
            Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s'", section_name));
            
            if (is_lookup_section_node_dirty)
            {
                Log.d(TAG, "lookup_section_node is dirty.");
                refreshLookupSectionNode();
            } // if (is_lookup_section_node_dirty)
            
            Node return_value;
            if (lookup_section_node.containsKey(section_name))
            {
                return_value = lookup_section_node.get(section_name);
            }
            else
            {
                return_value = null;
            } // if (lookup_section_node.containsKey(section_name))
            
            Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));
            return return_value;            
        } // private Node getSectionNode(String section_name)
        
        private Node getFieldNode(String section_name, String field_name)
        {
            final String TAG = TAG_HEADER + "::getNode";
            Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s'", section_name, field_name));            
            
            String section_field_key = getSectionFieldKey(section_name, field_name);
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
            final String TAG = TAG_HEADER + "::getValueFromSectionNameAndFieldName";
            Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s'", section_name, field_name));
            
            Node actual_field_node = getFieldNode(section_name, field_name);
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
            final String TAG = TAG_HEADER + "::getValuesFromSectionNameAndFieldName";
            Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s'", section_name, field_name));
            
            Node actual_field_node = getFieldNode(section_name, field_name);            
            if (actual_field_node == null)
            {
                Log.d(TAG, "node returned is null, so either section or field does not exist.");
                return null;
            } // if (actual_field_node == null)            
            
            List<String> return_value = actual_field_node.getValues();
            Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));
            return return_value;            
        } // public getValueFromSectionNameAndFieldName(String section_name, String field_name)
        
        public boolean setValue(String section_name, String field_name, String value)
        {
            final String TAG = TAG_HEADER + "::setValue";
            Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s', value: '%s'", section_name, field_name, value));
            
            Node actual_field_node = getFieldNode(section_name, field_name);
            if (actual_field_node == null)
            {
                Log.d(TAG, "node returned is null, so either section or field does not exist.");
                addSection(section_name);
                addField(section_name, field_name, value);                
            }
            else
            {
                actual_field_node.setValue(value);
            } // if (actual_field_node == null)
            is_json_object_dirty = true;
            return true;            
        } // public void setValue(String section_name, String field_name, String value)
        
        public boolean setValue(String section_name, String field_name, List<String> values)
        {
            final String TAG = TAG_HEADER + "::setValue";
            Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s', values: '%s'", section_name, field_name, values));
            
            Node actual_field_node = getFieldNode(section_name, field_name);
            if (actual_field_node == null)
            {
                Log.d(TAG, "node returned is null, so either section or field does not exist.");
                addSection(section_name);
                addField(section_name, field_name, values);                
            }
            else
            {
                actual_field_node.setValue(values);
            } // if (actual_field_node == null)            
            is_json_object_dirty = true;
            return true;            
        } // public boolean setValue(String section_name, String field_name, List<String> values)        
        
        public String getTitle()
        {
            return getValueFromSectionNameAndFieldName("Basic info", "Title");
        } // public String getTitle()
        
    } // public static class ConditionReportContentsJsonWrapper
    
} // public class Json
