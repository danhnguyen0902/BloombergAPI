package com.bemu.BEmu.Requests;

import com.bemu.BEmu.*; //un-comment this line to use the Bloomberg API Emulator
//import com.bloomberglp.blpapi.*; //un-comment this line to use the actual Bloomberg API

public class RunReferenceDataRequest
{
	// Create the texts
    public static StringBuilder responseContent = new StringBuilder("");
    public static StringBuilder errorContent = new StringBuilder("");
    
	public static void RunExample() throws Exception
	{
		SessionOptions soptions = new SessionOptions();
		soptions.setServerHost("127.0.0.1");
		soptions.setServerPort(8194);
		
		Session session = new Session(soptions);
		if(session.start() && session.openService("//blp/refdata"))
		{
            CorrelationID requestID = new CorrelationID(1);
			Service refDataSvc = session.getService("//blp/refdata");
			if(refDataSvc == null)
			{
				//System.err.println("Cannot get service");
				errorContent.append("Cannot get service!\n");
			}
			else
			{
				Request request = refDataSvc.createRequest("ReferenceDataRequest");
	
	            //request information for the following securities
	            request.append("securities", "SPY US EQUITY");
	            request.append("securities", "MSFT US EQUITY");
	            request.append("securities", "AAPL 150117C00600000 EQUITY"); //this is a stock option: TICKER yyMMdd[C/P]\d{8} EQUITY
                //request.append("securities", "ZYZZ US EQUITY"); //the code treats securities that start with a "Z" as non-existent
	
	            //include the following simple fields in the response
	            //request.append("fields", "ZPX_LAST"); //the code treats a field that starts with a "Z" as a bad field
	            request.append("fields", "PX_LAST");
	            request.append("fields", "BID");
	            request.append("fields", "ASK");
	            request.append("fields", "TICKER");
	            request.append("fields", "OPT_EXPIRE_DT"); //only stock options have this field
	
	            //request a field that can be overriden and returns bulk data
	            request.append("fields", "CHAIN_TICKERS"); //only stocks have this field 
	            Element overrides = request.getElement("overrides");
	
	            //request only puts
	            Element ovrdPutCall = overrides.appendElement();
	            ovrdPutCall.setElement("fieldId", "CHAIN_PUT_CALL_TYPE_OVRD");
	            ovrdPutCall.setElement("value", "P"); //accepts either "C" for calls or "P" for puts
	
	            //request 5 options in the response
	            Element ovrdNumStrikes = overrides.appendElement();
	            ovrdNumStrikes.setElement("fieldId", "CHAIN_POINTS_OVRD");
	            ovrdNumStrikes.setElement("value", 5); //accepts a positive integer
	
	            //request options that expire on Dec. 20, 2014
	            Element ovrdDtExps = overrides.appendElement();
	            ovrdDtExps.setElement("fieldId", "CHAIN_EXP_DT_OVRD");
	            ovrdDtExps.setElement("value", "20141220"); //accepts dates in the format yyyyMMdd (this is Dec. 20, 2014)
	
	            session.sendRequest(request, requestID);
	
	            boolean continueToLoop = true;
	            while (continueToLoop)
	            {
	                Event eventObj = session.nextEvent();
	                switch (eventObj.eventType().intValue())
	                {
	                    case Event.EventType.Constants.RESPONSE: // final event
	                        continueToLoop = false;
	                        RunReferenceDataRequest.handleResponseEvent(eventObj);
	                        break;
	                    case Event.EventType.Constants.PARTIAL_RESPONSE:
	                    	RunReferenceDataRequest.handleResponseEvent(eventObj);
	                        break;
						default:
							RunReferenceDataRequest.handleOtherEvent(eventObj);
							break;
	                }
	            }
			}
		}	
	}

    private static void handleResponseEvent(Event eventObj) throws Exception
    {
    	//System.out.println("EventType =" + eventObj.eventType());
    	responseContent.append("EventType =" + eventObj.eventType() + "\n");
		MessageIterator miter = eventObj.messageIterator();
		while(miter.hasNext())
        {
			Message message = miter.next();
			
			//System.out.println("correlationID=" + message.correlationID().toString());
			//System.out.println("messageType =" + message.messageType().toString());
			
			responseContent.append("correlationID=" + message.correlationID().toString() + "\n");
			responseContent.append("messageType =" + message.messageType().toString() + "\n");

            Element elmSecurityDataArray = message.getElement("securityData");
            for (int valueIndex = 0; valueIndex < elmSecurityDataArray.numValues(); valueIndex++)
            {
                Element elmSecurityData = elmSecurityDataArray.getValueAsElement(valueIndex);

                String security = elmSecurityData.getElementAsString("security");
                //System.out.println();
                //System.out.println();
                //System.out.println(security);
                
                responseContent.append("\n\n" + security + "\n");

                boolean hasFieldErrors = elmSecurityData.hasElement("fieldExceptions", true);
                if(hasFieldErrors)
                {
                    Element elmFieldErrors = elmSecurityData.getElement("fieldExceptions");
                    for (int errorIndex = 0; errorIndex < elmFieldErrors.numValues(); errorIndex++)
                    {
                        Element fieldError = elmFieldErrors.getValueAsElement(errorIndex);
                        String fieldId = fieldError.getElementAsString("fieldId");

                        Element errorInfo = fieldError.getElement("errorInfo");
                        String source = errorInfo.getElementAsString("source");
                        int code = errorInfo.getElementAsInt32("code");
                        String category = errorInfo.getElementAsString("category");
                        String strMessage = errorInfo.getElementAsString("message");
                        String subCategory = errorInfo.getElementAsString("subcategory");

                        /*System.err.println();
                        System.err.println();
                        System.err.println("\tfield error: " + security);
                        System.err.println(String.format("\tfieldId = %s", fieldId));
                        System.err.println(String.format("\tsource = %s", source));
                        System.err.println(String.format("\tcode = %s", code));
                        System.err.println(String.format("\tcategory = %s", category));
                        System.err.println(String.format("\terrorMessage = %s", strMessage));
                        System.err.println(String.format("\tsubCategory = %s", subCategory));*/
                        
                        errorContent.append("\n\n" + "\tfield error: " + security + "\n");
                        errorContent.append(String.format("\tfieldId = %s", fieldId) + "\n");
                        errorContent.append(String.format("\tsource = %s", source) + "\n");
                        errorContent.append(String.format("\tcode = %s", code) + "\n");
	                    errorContent.append(String.format("\tcategory = %s", category) + "\n");
                		errorContent.append(String.format("\terrorMessage = %s", strMessage) + "\n");
        				errorContent.append(String.format("\tsubCategory = %s", subCategory) + "\n");
                    }
                }
                
                boolean isSecurityError = elmSecurityData.hasElement("securityError", true);
                if(isSecurityError)
                {
                    Element secError = elmSecurityData.getElement("securityError");
                    String source = secError.getElementAsString("source");
                    int code = secError.getElementAsInt32("code");
                    String category = secError.getElementAsString("category");
                    String errorMessage = secError.getElementAsString("message");
                    String subCategory = secError.getElementAsString("subcategory");

                    /*System.err.println("security error");
                    System.err.println(String.format("source = %s", source));
                    System.err.println(String.format("code = %s", code));
                    System.err.println(String.format("category = %s", category));
                    System.err.println(String.format("errorMessage = %s", errorMessage));
                    System.err.println(String.format("subCategory = %s", subCategory));*/
                    
                    errorContent.append("security error");
                    errorContent.append(String.format("\tsource = %s", source) + "\n");
                    errorContent.append(String.format("\tcode = %s", code) + "\n");
                    errorContent.append(String.format("\tcategory = %s", category) + "\n");
            		errorContent.append(String.format("\terrorMessage = %s", errorMessage) + "\n");
    				errorContent.append(String.format("\tsubCategory = %s", subCategory) + "\n");
                }
                else
                {
	                Element elmFieldData = elmSecurityData.getElement("fieldData");
	
	                double pxLast = elmFieldData.getElementAsFloat64("PX_LAST");
	                double bid = elmFieldData.getElementAsFloat64("BID");
	                double ask = elmFieldData.getElementAsFloat64("ASK");
	                String ticker = elmFieldData.getElementAsString("TICKER");
	
	                /*System.out.println("PX_LAST = " + String.valueOf(pxLast));
	                System.out.println("BID = " + String.valueOf(bid));
	                System.out.println("ASK = " + String.valueOf(ask));
	                System.out.println("TICKER = " + ticker);*/
	                
	                responseContent.append("PX_LAST = " + String.valueOf(pxLast) + "\n");
	                responseContent.append("BID = " + String.valueOf(bid) + "\n");
	                responseContent.append("ASK = " + String.valueOf(ask) + "\n");
	                responseContent.append("TICKER = " + ticker + "\n");
	
	                boolean excludeNullElements = true;
	                if (elmFieldData.hasElement("CHAIN_TICKERS", excludeNullElements)) //be careful, excludeNullElements is false by default
	                {
	                    Element chainTickers = elmFieldData.getElement("CHAIN_TICKERS");
	                    for (int chainTickerValueIndex = 0; chainTickerValueIndex < chainTickers.numValues(); chainTickerValueIndex++)
	                    {
	                        Element chainTicker = chainTickers.getValueAsElement(chainTickerValueIndex);
	                        String strChainTicker = chainTicker.getElementAsString("Ticker");
	
	                        //System.out.println("CHAIN_TICKER = " + strChainTicker);
	                        responseContent.append("CHAIN_TICKER = " + strChainTicker + "\n");
	                    }
	                }
	                else
	                {
	                	//System.out.println("No CHAIN_TICKER information");
	                	responseContent.append("No CHAIN_TICKER information");
	                }
                }
                
            }
        }
    }

    private static void handleOtherEvent(Event evt)
    {
        //System.out.println("EventType = " + evt.eventType());
		responseContent.append("EventType = " + evt.eventType() + "\n");
    	MessageIterator miter = evt.messageIterator();
		while(miter.hasNext())
		{
			Message message = miter.next();

        	/*System.out.println("correlationID=" + message.correlationID());
        	System.out.println("messageType=" + message.messageType());
        	System.out.println(message.toString());*/
			
			responseContent.append("correlationID=" + message.correlationID() + "\n");
			responseContent.append("messageType=" + message.messageType() + "\n");
			responseContent.append(message.toString() + "\n");
        	
            if (Event.EventType.Constants.SESSION_STATUS == evt.eventType().intValue() && message.messageType().equals("SessionTerminated"))
            {
            	//System.out.println("Terminating: " + message.messageType());
            	responseContent.append("Terminating: " + message.messageType() + "\n");
            	
            }
		}
    }

}
