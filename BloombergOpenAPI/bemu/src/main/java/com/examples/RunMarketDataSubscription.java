package com.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.bemu.BEmu.*; //un-comment this line to use the Bloomberg API Emulator
//import com.bloomberglp.blpapi.*; //un-comment this line to use the actual Bloomberg API

public class RunMarketDataSubscription
{
	public static void RunExample() throws Exception
	{
		RunMarketDataSubscription._fields = new ArrayList<String>();
		RunMarketDataSubscription._fields.add("BID");
		RunMarketDataSubscription._fields.add("ASK");
		//RunMarketDataSubscription._fields.add("ZASK");
		
		SessionOptions soptions = new SessionOptions();
		soptions.setServerHost("127.0.0.1");
		soptions.setServerPort(8194);

		RunMarketDataSubscription r = new RunMarketDataSubscription();
		MyEventHandler mevt = r.new MyEventHandler();
        Session session = new Session(soptions, mevt);
        session.startAsync();
	}

    private static List<String> _fields;
	
	public class MyEventHandler implements EventHandler
	{		
		public void processEvent(Event event, Session session)
		{
			switch (event.eventType().intValue())
			{
				case Event.EventType.Constants.SESSION_STATUS:
				{
					MessageIterator iter = event.messageIterator();
					while (iter.hasNext())
					{
						Message message = iter.next();
						if (message.messageType().equals("SessionStarted"))
						{
							try
							{
								session.openServiceAsync("//blp/refdata", new CorrelationID(-9999));
							}
							catch (Exception e)
							{
								System.err.println("Could not open //blp/refdata for async");
							}
						}
					}
					break;
				}
				
				case Event.EventType.Constants.SERVICE_STATUS:
				{
					MessageIterator iter = event.messageIterator();
					while (iter.hasNext())
					{
						Message message = iter.next();
						
						if (message.messageType().equals("ServiceOpened"))
						{
							SubscriptionList slist = new SubscriptionList();
							//slist.add(new Subscription("ZYZZ US EQUITY", RunMarketDataSubscription._fields)); //the code treats securities that start with a "Z" as non-existent
							slist.add(new Subscription("SPY US EQUITY", RunMarketDataSubscription._fields));
							slist.add(new Subscription("AAPL 150117C00600000 EQUITY", RunMarketDataSubscription._fields));
							
							try
							{
								session.subscribe(slist);
							}
							catch (Exception e)
							{
								System.err.println("Subscription error");
							}
						}
					}
					break;
				}

				case Event.EventType.Constants.SUBSCRIPTION_DATA:
				case Event.EventType.Constants.PARTIAL_RESPONSE:
				case Event.EventType.Constants.RESPONSE:	
				{
					try {
						dumpEvent(event);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // Handle Partial Response
					break;
				}
				
                case Event.EventType.Constants.SUBSCRIPTION_STATUS:
                {
					MessageIterator iter = event.messageIterator();
					while (iter.hasNext())
					{
						Message msg = iter.next();
						
						try
						{	
							boolean fieldExceptionsExist = msg.messageType().toString().equals("SubscriptionStarted") &&
									msg.hasElement("exceptions", true);
							
							boolean securityError = msg.messageType().toString().equals("SubscriptionFailure") &&
									msg.hasElement("reason", true);
							
	                        if (fieldExceptionsExist)
	                        {
	                            Element elmExceptions = msg.getElement("exceptions");
	                            for (int i = 0; i < elmExceptions.numValues(); i++)
	                            {
	                                Element elmException = elmExceptions.getValueAsElement(i);
	                                String fieldId = elmException.getElementAsString("fieldId");
	
	                                Element elmReason = elmException.getElement("reason");
	                                String source = elmReason.getElementAsString("source");
	                                int errorCode = elmReason.getElementAsInt32("errorCode");
	                                String category = elmReason.getElementAsString("category");
	                                String description = elmReason.getElementAsString("description");
	
	                                System.err.println("field error: ");
	                                System.err.println(String.format("\tfieldId = %s", fieldId));
	                                System.err.println(String.format("\tsource = %s", source));
	                                System.err.println(String.format("\terrorCode = %s", errorCode));
	                                System.err.println(String.format("\tcategory = %s", category));
	                                System.err.println(String.format("\tdescription = %s", description));
	                            }
	                        }
	                        else if (securityError)
	                        {
	                            String security = msg.topicName();

	                            Element elmReason = msg.getElement("reason");
	                            String source = elmReason.getElementAsString("source");
	                            int errorCode = elmReason.getElementAsInt32("errorCode");
	                            String category = elmReason.getElementAsString("category");
	                            String description = elmReason.getElementAsString("description");

	                            System.err.println("security not found: ");
	                            System.err.println(String.format("\tsecurity = %s", security));
	                            System.err.println(String.format("\tsource = %s", source));
	                            System.err.println(String.format("\terrorCode = %s", errorCode));
	                            System.err.println(String.format("\tcategory = %s", category));
	                            System.err.println(String.format("\tdescription = %s", description));
	                        }
						}
						catch(Exception ex)
						{
							System.err.println(ex.getMessage());
						}

					}
                	break;
                }
				
			}
		}
	
		private void dumpEvent(Event event) throws Exception
		{
			System.out.println();
			System.out.println("eventType=" + event.eventType());
			MessageIterator messageIterator = event.messageIterator();
			SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss"); 
			
			while (messageIterator.hasNext())
			{
				Message message = messageIterator.next();
				
				String security = message.topicName();
				for(int i = 0; i < RunMarketDataSubscription._fields.size(); i++)
				{
					//This ignores the extraneous fields in the response
					String field =RunMarketDataSubscription._fields.get(i);
					if(message.hasElement(field, true)) //be careful, excludeNullElements is false by default
					{
						Element elmField = message.getElement(field);
						
						String output = String.format("%s: %s, %s", fmt.format(new Date()), security, elmField.toString().trim());
						System.out.println(output);
					}
				}
			}
			System.out.println();
		}
	}
}
