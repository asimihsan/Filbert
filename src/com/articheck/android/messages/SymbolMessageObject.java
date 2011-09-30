package com.articheck.android.messages;

import com.google.common.base.Objects;
import com.articheck.android.utilities.Point;

public class SymbolMessageObject
{
    private String symbol_name;
    private Point location;
    
    public SymbolMessageObject(String symbol_name, Point location)
    {
        this.symbol_name = symbol_name;
        this.location = location;
    }

    public String getSymbolName()
    {
        return symbol_name;
    }

    public void setSymbolName(String symbol_name)
    {
        this.symbol_name = symbol_name;
    }

    public Point getLocation()
    {
        return location;
    }

    public void setLocation(Point location)
    {
        this.location = location;
    }
    
    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                       .add("symbol_name", symbol_name)
                       .add("location", location)
                       .toString();
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hashCode(symbol_name, location);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        } // if (o == this)
        if (!(o instanceof SymbolMessageObject))
        {
            return false;
        } // if (!(o instanceof SymbolMessageObject))
        SymbolMessageObject s = (SymbolMessageObject)o;
        boolean result = (Objects.equal(symbol_name, s.symbol_name)
                           && Objects.equal(location, s.location));
        return result;
    } // public boolean equals(Object o)    
}