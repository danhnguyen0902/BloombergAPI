package com.bemu.BEmu.Requests;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.bemu.BEmu.*; //un-comment this line to use the Bloomberg API Emulator
//import com.bloomberglp.blpapi.*; //un-comment this line to use the actual Bloomberg API

public class RunHistoricalDataRequest
{
	private static SimpleDateFormat dateFmt = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat dateFmtOut = new SimpleDateFormat("yyyy-MM-dd");
	
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
			Service service = session.getService("//blp/refdata");
			Request request = service.createRequest("HistoricalDataRequest");

            //Request information for the following securities			
            request.append("securities", "SPY US EQUITY");
            request.append("securities", "C A COMDTY");
            request.append("securities", "AAPL 150117C00600000 EQUITY"); //this is a stock option: TICKER yyMMdd[C/P]\d{8} EQUITY
            
            //uncomment the following line to see what a request for a nonexistent security looks like
			//request.append("securities", "ZIBM US EQUITY");
			//  My code treats all securities that start with a 'Z' as a nonexistent security
            
            
            
            
            //Include the following simple fields in the result
            request.append("fields", "BID");
            request.append("fields", "ASK");
            
            //uncomment the following line to see what a request for an invalid field looks like
            //request.append("fields", "ZBID");
			//  My code treats all fields that start with a 'Z' as an invalid field

            //Historical requests allow a few overrides.  See the developer's guide A.2.4 for more information.
            Calendar calStart = Calendar.getInstance();
            calStart.add(Calendar.MONTH, -1);
            String strStart = dateFmt.format(calStart.getTime());

            Calendar calEnd = Calendar.getInstance();
            calEnd.add(Calendar.DAY_OF_MONTH, 10);
            String strEnd = dateFmt.format(calEnd.getTime());
            
            request.set("startDate", strStart); //Request that the information start three months ago from today.  This override is required.
            request.set("endDate", strEnd); //Request that the information end three days before today.  This is an optional override.  The default is today.
                        
            //Determine the frequency and calendar type of the output. To be used in conjunction with Period Selection.
            request.set("periodicityAdjustment", "CALENDAR"); //Optional string.  Valid values are ACTUAL (default), CALENDAR, and FISCAL.
            
            //Determine the frequency of the output. To be used in conjunction with Period Adjustment.
            request.set("periodicitySelection", "DAILY"); //Optional string.  Valid values are DAILY (default), WEEKLY, MONTHLY, QUARTERLY, SEMI_ANNUALLY, and YEARLY

            //Sets quote to Price or Yield for a debt instrument whose default value is quoted in yield (depending on pricing source).
            request.set("pricingOption", "PRICING_OPTION_PRICE"); //Optional string.  Valid values are PRICING_OPTION_PRICE (default) and PRICING_OPTION_YIELD

            //Adjust for "change on day"
            request.set("adjustmentNormal", true); //Optional bool. Valid values are true and false (default = false)

            //Adjusts for Anormal Cash Dividends
            request.set("adjustmentAbnormal", false); //Optional bool. Valid values are true and false (default = false)

            //Capital Changes Defaults
            request.set("adjustmentSplit", true); //Optional bool. Valid values are true and false (default = false)

            //The maximum number of data points to return, starting from the startDate
            //request.set("maxDataPoints", 5); //Optional integer.  Valid values are positive integers.  The default is unspecified in which case the response will have all data points between startDate and endDate

            //Indicates whether to use the average or the closing price in quote calculation.
            request.set("overrideOption", "OVERRIDE_OPTION_CLOSE"); //Optional string.  Valid values are OVERRIDE_OPTION_GPA for an average and OVERRIDE_OPTION_CLOSE (default) for the closing price
            
            CorrelationID requestID = new CorrelationID(1);
            session.sendRequest(request, requestID);
            
			boolean continueLoop = true;
			while(continueLoop)
			{
				Event evt = session.nextEvent();
				switch(evt.eventType().intValue())
				{
					case Event.EventType.Constants.RESPONSE:
						RunHistoricalDataRequest.processResponseHist(evt);
						continueLoop = false;
						break;
					case Event.EventType.Constants.PARTIAL_RESPONSE:
						RunHistoricalDataRequest.processResponseHist(evt);
						break;
					default:
						RunHistoricalDataRequest.handleOtherEvent(evt);
						break;
				}
			}
		}
	}
	
	private static void processResponseHist(Event evt) throws Exception
	{
        //System.out.println("EventType = " + evt.eventType());
		responseContent.append("EventType = " + evt.eventType() + "\n");
        
		MessageIterator miter = evt.messageIterator();
		while(miter.hasNext())
		{
			Message msg = miter.next();
			
			/*System.out.println();
			System.out.println("correlationID= " + msg.correlationID());
			System.out.println("messageType= " + msg.messageType());*/
			
			responseContent.append("\n" + "correlationID= " + msg.correlationID() + "\n");
			responseContent.append("messageType= " + msg.messageType() + "\n");
					
			Element elmSecurityData = msg.getElement("securityData");
			
			Element elmSecurity = elmSecurityData.getElement("security");
			String security = elmSecurity.getValueAsString();
			//System.out.println(security);
			responseContent.append(security + "\n");
			
			if(elmSecurityData.hasElement("securityError", true))
			{
                Element elmSecError = elmSecurityData.getElement("securityError");
                String source = elmSecError.getElementAsString("source");
                int code = elmSecError.getElementAsInt32("code");
                String category = elmSecError.getElementAsString("category");
                String errorMessage = elmSecError.getElementAsString("message");
                String subCategory = elmSecError.getElementAsString("subcategory");

                /*System.err.println("security error");
                System.err.println(String.format("source = %s", source));
                System.err.println(String.format("code = %s", code));
                System.err.println(String.format("category = %s", category));
                System.err.println(String.format("errorMessage = %s", errorMessage));
                System.err.println(String.format("subCategory = %s", subCategory));*/
                
                errorContent.append("security error" + "\n");
                errorContent.append(String.format("\tsource = %s", source) + "\n");
                errorContent.append(String.format("\tcode = %s", code) + "\n");
                errorContent.append(String.format("\tcategory = %s", category) + "\n");
        		errorContent.append(String.format("\terrorMessage = %s", errorMessage) + "\n");
				errorContent.append(String.format("\tsubCategory = %s", subCategory) + "\n");
			}
			else
			{
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
                        System.err.println("field error: ");
                        System.err.println(String.format("\tfieldId = %s", fieldId));
                        System.err.println(String.format("\tsource = %s", source));
                        System.err.println(String.format("\tcode = %s", code));
                        System.err.println(String.format("\tcategory = %s", category));
                        System.err.println(String.format("\terrorMessage = %s", strMessage));
                        System.err.println(String.format("\tsubCategory = %s", subCategory));*/
                        
                        errorContent.append("\n\n" + "\tfield error: " + "\n");
                        errorContent.append(String.format("\tfieldId = %s", fieldId) + "\n");
                        errorContent.append(String.format("\tsource = %s", source) + "\n");
                        errorContent.append(String.format("\tcode = %s", code) + "\n");
	                    errorContent.append(String.format("\tcategory = %s", category) + "\n");
                		errorContent.append(String.format("\terrorMessage = %s", strMessage) + "\n");
        				errorContent.append(String.format("\tsubCategory = %s", subCategory) + "\n");
                    }
				}
				
				Element elmFieldData = elmSecurityData.getElement("fieldData");
				for (int valueIndex = 0; valueIndex < elmFieldData.numValues(); valueIndex++)
				{
	                Element elmValues = elmFieldData.getValueAsElement(valueIndex);                
					Datetime date = elmValues.getElementAsDate("date");
	                double bid = elmValues.getElementAsFloat64("BID");
	                double ask = elmValues.getElementAsFloat64("ASK");
	                
	                //System.out.print(RunHistoricalDataRequest.dateFmtOut.format(date.calendar().getTime()));
	                //System.out.println(String.format(": BID = $%,.2f, ASK = $%,.2f", bid, ask));
	                responseContent.append(RunHistoricalDataRequest.dateFmtOut.format(date.calendar().getTime()));
	                responseContent.append(String.format(": BID = $%,.2f, ASK = $%,.2f", bid, ask) + "\n");
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
