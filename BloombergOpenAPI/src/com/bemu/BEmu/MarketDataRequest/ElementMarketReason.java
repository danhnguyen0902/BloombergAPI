//------------------------------------------------------------------------------
// <copyright project="BEmuJava" file="BEmu.MarketDataRequest.ElementMarketReason.java" company="Jordan Robinson">
//     Copyright (c) 2013 Jordan Robinson. All rights reserved.
//
//     The use of this software is governed by the Microsoft Public License
//     which is included with this distribution.
// </copyright>
//------------------------------------------------------------------------------

package com.bemu.BEmu.MarketDataRequest;

import com.bemu.BEmu.Element;
import com.bemu.BEmu.Name;
import com.bemu.BEmu.types.RandomDataGenerator;

public class ElementMarketReason extends ElementParent
{
    private final ElementMarketString _source, _category, _description;
    private final ElementMarketInt _errorCode;

    public enum ReasonTypeEnum { badSecurity, badField }

    public ElementMarketReason(ReasonTypeEnum reasonType)
    {
        if (reasonType == ReasonTypeEnum.badSecurity)
        {
            this._source = new ElementMarketString("source", String.format("%s%s@%s", RandomDataGenerator.randomString(5).toLowerCase(), RandomDataGenerator.randomInt(9), RandomDataGenerator.randomInt(99)));
            this._errorCode = new ElementMarketInt("errorCode", RandomDataGenerator.randomInt(99));
            this._category = new ElementMarketString("category", "BAD_SEC");
            this._description = new ElementMarketString("description", "Invalid security, rcode = -1");
        }
        else //if (reasonType == ReasonTypeEnum.badField)
        {
            this._source = new ElementMarketString("source", String.format("%s@%s", RandomDataGenerator.randomString(7).toLowerCase(), RandomDataGenerator.randomInt(999)));
            this._errorCode = new ElementMarketInt("errorCode", RandomDataGenerator.randomInt(99));
            this._category = new ElementMarketString("category", "BAD_FLD");
            this._description = new ElementMarketString("description", "Unknown Field");
        }
    }
	
    public Name name()
    {
    	return new Name("reason");
    }
    
    public int numValues()
    {
    	return 1;
    }
    
    public int numElements()
    {
    	return 4;
    }
    
    public boolean isComplexType()
    {
    	return true;
    }
    
    public boolean isArray()
    {
    	return false;
    }
    
    public boolean isNull()
    {
    	return false;
    }
    
    public int getElementAsInt32(String name) throws Exception
    {
    	return this.getElement(name).getValueAsInt32();
    }
    
    public String getElementAsString(String name) throws Exception
    {
    	return this.getElement(name).getValueAsString();
    }
        public Element getElement(String name) throws Exception
    {
    	if(name.equals("source"))
    		return this._source;
    	
    	else if(name.equals(this._errorCode.name().toString()))
    		return this._errorCode;
    	
    	else if(name.equals("category"))
    		return this._category;
    	
    	else if(name.equals("description"))
    		return this._description;
    	
    	else
    		throw new Exception("name not recognized. names are case-sensitive.");
    }
    
    public boolean hasElement(String name, boolean excludeNullElements)
    {
    	return this.hasElement(name);
    }
    
    public boolean hasElement(String name)
    {
    	return 
    			name.equals("source") || 
    			name.equals(this._errorCode.name().toString()) ||
    			name.equals("category") ||
    			name.equals("description");
    }
    
    protected StringBuilder prettyPrint(int tabIndent)
    {
        String tabs = com.bemu.BEmu.types.IndentType.Indent(tabIndent);
        StringBuilder result = new StringBuilder();
        
        result.append(String.format("%s%s = {%s", tabs, this.name().toString(), System.getProperty("line.separator")));
        result.append(this._source.prettyPrint(tabIndent + 1));
        result.append(this._errorCode.prettyPrint(tabIndent + 1));
        result.append(this._category.prettyPrint(tabIndent + 1));
        result.append(this._description.prettyPrint(tabIndent + 1));
        result.append(String.format("%s}%s", tabs, System.getProperty("line.separator")));

        return result;
    }

}
