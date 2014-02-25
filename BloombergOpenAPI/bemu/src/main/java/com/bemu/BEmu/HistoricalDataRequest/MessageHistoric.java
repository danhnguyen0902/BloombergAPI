//------------------------------------------------------------------------------
// <copyright project="BEmuJava" file="BEmu.HistoricalDataRequest.MessageHistoric.java" company="Jordan Robinson">
//     Copyright (c) 2013 Jordan Robinson. All rights reserved.
//
//     The use of this software is governed by the Microsoft Public License
//     which is included with this distribution.
// </copyright>
//------------------------------------------------------------------------------

package com.bemu.BEmu.HistoricalDataRequest;

import com.bemu.BEmu.Element;
import com.bemu.BEmu.Name;
import com.bemu.BEmu.Message;
import com.bemu.BEmu.CorrelationID;
import com.bemu.BEmu.Datetime;
import java.util.Map;
import java.util.List;

public class MessageHistoric extends Message
{
    private final ElementHistoricSecurityData _security;
    
    MessageHistoric(CorrelationID corr, String securityName, List<String> badFields, Map<Datetime, Map<String, Object>> fieldData, int sequenceNumber)
    {
    	super(new Name("HistoricalDataResponse"), corr, null);
    	this._security = new ElementHistoricSecurityData(securityName, badFields, fieldData, sequenceNumber);
    }
    
    ElementHistoricSecurityData security()
    {
    	return this._security;
    }
    
	public Element getElement(String name) throws Exception
	{
		if(name.equals(this._security.name().toString()))
			return this._security;
		else
			throw new Exception("not implemented. case-sensitive.");
	}
    
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        
        result.append("HistoricalDataResponse (choice) = {" + System.getProperty("line.separator"));
        result.append(this._security.prettyPrint(1));
        result.append("}");

        return result.toString();
    }
}
