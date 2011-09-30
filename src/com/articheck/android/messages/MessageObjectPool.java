package com.articheck.android.messages;

import java.util.List;

import com.articheck.android.utilities.Point;
import com.google.common.collect.Lists;

/**
 * @author ai
 *
 * We need so many messages to draw / redraw symbols on a photo that battery
 * life may suffer unless we collect messages from a pool.
 *
 */
public class MessageObjectPool
{
    private int pool_size = 100;    
    private List<SymbolMessageObject> symbol_message_objects_available;
    
    public MessageObjectPool()
    {
        initialize();
    } // public MessageObjectPool()
    
    private void initialize()
    {
        symbol_message_objects_available = Lists.newArrayListWithCapacity(pool_size);
        for (int i = 0; i < pool_size; i++)
        {
            symbol_message_objects_available.add(new SymbolMessageObject(null, null));
        } // for (int i = 0; i < pool_start_size; i++)
    } // private void initialize()
    
    private void increaseSymbolMessageObjectPoolSize()
    {
        for (int i = 0; i < pool_size; i++)
        {
            symbol_message_objects_available.add(new SymbolMessageObject(null, null));
        } // for (int i = 0; i < pool_start_size; i++)
        pool_size *= 2;
    } // private void increaseSymbolMessageObjectPoolSize()
    
    public SymbolMessageObject getSymbolMessageObject(String symbol_name, Point location)
    {
        if (symbol_message_objects_available.isEmpty())
        {
            increaseSymbolMessageObjectPoolSize();
        } // if (symbol_message_objects_available.isEmpty())
        SymbolMessageObject return_value = symbol_message_objects_available.remove(0);
        return_value.setSymbolName(symbol_name);
        return_value.setLocation(location);
        return return_value;
    } // public SymbolMessageObject getSymbolMessageObject(String symbol_name, Point location)
    
    public void returnSymbolMessageObject(SymbolMessageObject symbol_message_object)
    {
        symbol_message_objects_available.add(symbol_message_object);
    } // public void returnSymbolMessageObject(SymbolMessageObject symbol_message_object)
}
