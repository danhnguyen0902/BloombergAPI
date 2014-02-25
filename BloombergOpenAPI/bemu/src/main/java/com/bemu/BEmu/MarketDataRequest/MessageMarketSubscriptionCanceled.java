//------------------------------------------------------------------------------
// <copyright project="BEmuJava" file="BEmu.MarketDataRequest.MessageMarketSubscriptionCanceled.java" company="Jordan Robinson">
//     Copyright (c) 2013 Jordan Robinson. All rights reserved.
//
//     The use of this software is governed by the Microsoft Public License
//     which is included with this distribution.
// </copyright>
//------------------------------------------------------------------------------

package com.bemu.BEmu.MarketDataRequest;

import com.bemu.BEmu.Message;
import com.bemu.BEmu.Subscription;
import com.bemu.BEmu.Name;
import com.bemu.BEmu.Element;

public class MessageMarketSubscriptionCanceled extends Message
{
    private final String _topicName;
    private final ElementMarketSubscriptionCancelReason _reason;

    MessageMarketSubscriptionCanceled(Subscription sub)
    {
    	super(new Name("SubscriptionTerminated"), sub.correlationID(), null);
    	
        this._topicName = sub.security();
        this._reason = new ElementMarketSubscriptionCancelReason();
    }
    
	public int numElements()
	{
		return 1;
	}
    
	public String topicName()
	{
		return this._topicName;
	}
	
	public boolean hasElement(String name, boolean excludeNullElements)
	{
		return this._reason.name().toString().equals(name);
	}
	
	public Element getElement(String name) throws Exception
	{
		if(name.equals(this._reason.name().toString()))
			return this._reason;
			
		else
			return super.getElement(name);
	}
	
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(String.format("%s = {%s", this.messageType().toString(), System.getProperty("line.separator")));
        result.append(this._reason.prettyPrint(1));
        result.append("}" + System.getProperty("line.separator"));

        return result.toString();
    }

}
