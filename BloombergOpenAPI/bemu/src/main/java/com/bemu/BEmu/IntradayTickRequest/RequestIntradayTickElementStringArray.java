//------------------------------------------------------------------------------
// <copyright project="BEmuJava" file="BEmu.IntradayTickRequest.RequestIntradayTickElementStringArray.java" company="Jordan Robinson">
//     Copyright (c) 2013 Jordan Robinson. All rights reserved.
//
//     The use of this software is governed by the Microsoft Public License
//     which is included with this distribution.
// </copyright>
//------------------------------------------------------------------------------

package com.bemu.BEmu.IntradayTickRequest;

import com.bemu.BEmu.Element;
import com.bemu.BEmu.Name;

import java.util.List;
import java.util.ArrayList;

public class RequestIntradayTickElementStringArray extends Element
{
    private final String _elementName;
    private final List<String> _values;

    RequestIntradayTickElementStringArray(String elementName)
    {
        this._elementName = elementName;
        this._values = new ArrayList<String>();
    }
    
    public Name name()
    {
    	return new Name(this._elementName);
    }
    
    public int numValues()
    {
    	return this._values.size();
    }
    
    public int numElements()
    {
    	return 0;
    }
    
    public boolean isComplexType()
    {
    	return false;
    }
    
    public boolean isArray()
    {
    	return true;
    }
    
    public boolean isNull()
    {
    	return false;
    }
    
    private final static String[] _allowedEventTypes = { "TRADE", "BID", "ASK", "BID_BEST", "ASK_BEST", "MID_PRICE", "AT_TRADE", "BEST_BID", "BEST_ASK" };
    void addValue(String value) throws Exception
    {
    	boolean found = false;
    	for(int i = 0; i < RequestIntradayTickElementStringArray._allowedEventTypes.length; i++)
    	{
    		if(RequestIntradayTickElementStringArray._allowedEventTypes[i].equals(value))
    		{
    			found = true;
    			break;
    		}
    	}
    	
    	if(found)    	
    		this._values.add(value);
    	else
    		throw new Exception("Cannot convert String to Enumeration. value is case-sensitive.");
    }
    
    List<String> values()
    {
    	return this._values;
    }
    
    protected StringBuilder prettyPrint(int tabIndent)
    {
        String tabs = com.bemu.BEmu.types.IndentType.Indent(tabIndent);
        StringBuilder result = new StringBuilder();

        result.append(String.format("%s%s[] = {%s", tabs, this._elementName, System.getProperty("line.separator")));

        StringBuilder secs = new StringBuilder();
        for(int i = 0; i < this._values.size(); i++)
        {
        	secs.append(this._values.get(i));
        	if(i < this._values.size() - 1)
        		secs.append(",");
        }
        result.append(String.format("%s%s%s%s", tabs, com.bemu.BEmu.types.IndentType.TAB, secs.toString(), System.getProperty("line.separator")));
        
        result.append(String.format("%s}%s", tabs, System.getProperty("line.separator")));

        return result;
    }

}
