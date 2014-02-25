package com.danh.bloombergopenapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bemu.BEmu.*; //un-comment this line to use the Bloomberg API Emulator
//import com.bloomberglp.blpapi.*; //un-comment this line to use the actual Bloomberg API

public class MarketDataSubscriptionActivity extends Activity {
	// Handler
	private Handler handler;
	
	// TextViews
	private TextView responseTV = null;
	private TextView errorTV = null;
	
	// Buttons
	private Button request;
	private Button stop;
	
	// Session
	private Session session;

	//MarketDataSubscriptionActivity _this = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_market_data_subscription);
		
		// Handler
		handler = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				// super.handleMessage(msg);
				Bundle b = msg.getData();
				//int ID = b.getInt("ID");
				String responseContent = b.getString("responseContent");
				String errorContent = b.getString("errorContent");

				//responseTV.append("\n" + ID + "\n**********"); // this is why I make threadLoopCount static
				responseTV.append(responseContent);
				errorTV.append(errorContent);
				
				/*try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		};

		// TextView
		responseTV = (TextView) findViewById(R.id.responseTextView2);
		errorTV = (TextView) findViewById(R.id.errorTextView2);
		responseTV.setMovementMethod(new ScrollingMovementMethod());
		errorTV.setMovementMethod(new ScrollingMovementMethod());
		
		// Buttons
		request = (Button) findViewById(R.id.request);
		stop = (Button) findViewById(R.id.stop);
		
		request.setVisibility(View.VISIBLE);
		stop.setVisibility(View.GONE);
	}

	public void startThread() {
		MarketDataSubscriptionActivity._fields = new ArrayList<String>();
		MarketDataSubscriptionActivity._fields.add("BID");
		MarketDataSubscriptionActivity._fields.add("ASK");
		// RunMarketDataSubscription._fields.add("ZASK");

		SessionOptions soptions = new SessionOptions();
		soptions.setServerHost("127.0.0.1");
		soptions.setServerPort(8194);

		MarketDataSubscriptionActivity r = new MarketDataSubscriptionActivity();
		MyEventHandler mevt = r.new MyEventHandler();
		session = new Session(soptions, mevt, this);
		try {
			//Session.STOP = 0;

			session.startAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private static List<String> _fields;

	public class MyEventHandler implements EventHandler {
		public StringBuilder responseContent = new StringBuilder("");
		public StringBuilder errorContent = new StringBuilder("");
		//private int dataCount = 0;

		public void processEvent(Event event, Session session) {
			responseContent.delete(0, responseContent.length());
			errorContent.delete(0, errorContent.length());
			// or:
			// responseContent.setLength(0);
			// errorContent.setLength(0);

			/*dataCount++;
			if (dataCount == 1 + 3) {
				Session.STOP = 1;
			}*/

			switch (event.eventType().intValue()) {
			case Event.EventType.Constants.SESSION_STATUS: {
				MessageIterator iter = event.messageIterator();
				while (iter.hasNext()) {
					Message message = iter.next();
					if (message.messageType().equals("SessionStarted")) {
						try {
							session.openServiceAsync("//blp/refdata",
									new CorrelationID(-9999));
						} catch (Exception e) {
							// System.err.println("Could not open //blp/refdata for async");
							errorContent
									.append("Could not open //blp/refdata for async"
											+ "\n");
						}
					}
				}
				break;
			}

			case Event.EventType.Constants.SERVICE_STATUS: {
				MessageIterator iter = event.messageIterator();
				while (iter.hasNext()) {
					Message message = iter.next();

					if (message.messageType().equals("ServiceOpened")) {
						SubscriptionList slist = new SubscriptionList();
						// slist.add(new Subscription("ZYZZ US EQUITY",
						// RunMarketDataSubscription._fields)); //the code
						// treats securities that start with a "Z" as
						// non-existent
						slist.add(new Subscription("SPY US EQUITY",
								MarketDataSubscriptionActivity._fields));
						slist.add(new Subscription(
								"AAPL 150117C00600000 EQUITY",
								MarketDataSubscriptionActivity._fields));

						try {
							session.subscribe(slist);
						} catch (Exception e) {
							// System.err.println("Subscription error");
							errorContent.append("Subscription error" + "\n");
						}
					}
				}
				break;
			}

			case Event.EventType.Constants.SUBSCRIPTION_DATA:
			case Event.EventType.Constants.PARTIAL_RESPONSE:
			case Event.EventType.Constants.RESPONSE: {
				try {
					dumpEvent(event); // calls the function that gets data
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // Handle Partial Response
				break;
			}

			case Event.EventType.Constants.SUBSCRIPTION_STATUS: {
				MessageIterator iter = event.messageIterator();
				while (iter.hasNext()) {
					Message msg = iter.next();

					try {
						boolean fieldExceptionsExist = msg.messageType()
								.toString().equals("SubscriptionStarted")
								&& msg.hasElement("exceptions", true);

						boolean securityError = msg.messageType().toString()
								.equals("SubscriptionFailure")
								&& msg.hasElement("reason", true);

						if (fieldExceptionsExist) {
							Element elmExceptions = msg
									.getElement("exceptions");
							for (int i = 0; i < elmExceptions.numValues(); i++) {
								Element elmException = elmExceptions
										.getValueAsElement(i);
								String fieldId = elmException
										.getElementAsString("fieldId");

								Element elmReason = elmException
										.getElement("reason");
								String source = elmReason
										.getElementAsString("source");
								int errorCode = elmReason
										.getElementAsInt32("errorCode");
								String category = elmReason
										.getElementAsString("category");
								String description = elmReason
										.getElementAsString("description");

								/*
								 * System.err.println("field error: ");
								 * System.err
								 * .println(String.format("\tfieldId = %s",
								 * fieldId));
								 * System.err.println(String.format("\tsource = %s"
								 * , source)); System.err.println(String.format(
								 * "\terrorCode = %s", errorCode));
								 * System.err.println
								 * (String.format("\tcategory = %s", category));
								 * System
								 * .err.println(String.format("\tdescription = %s"
								 * , description));
								 */

								errorContent.append("field error: " + "\n");
								errorContent.append(String.format(
										"\tfieldId = %s", fieldId) + "\n");
								errorContent.append(String.format(
										"\tsource = %s", source) + "\n");
								errorContent.append(String.format(
										"\terrorCode = %s", errorCode) + "\n");
								errorContent.append(String.format(
										"\tcategory = %s", category) + "\n");
								errorContent.append(String.format(
										"\tdescription = %s", description)
										+ "\n");
							}
						} else if (securityError) {
							String security = msg.topicName();

							Element elmReason = msg.getElement("reason");
							String source = elmReason
									.getElementAsString("source");
							int errorCode = elmReason
									.getElementAsInt32("errorCode");
							String category = elmReason
									.getElementAsString("category");
							String description = elmReason
									.getElementAsString("description");

							/*
							 * System.err.println("security not found: ");
							 * System
							 * .err.println(String.format("\tsecurity = %s",
							 * security));
							 * System.err.println(String.format("\tsource = %s",
							 * source));
							 * System.err.println(String.format("\terrorCode = %s"
							 * , errorCode));
							 * System.err.println(String.format("\tcategory = %s"
							 * , category));
							 * System.err.println(String.format("\tdescription = %s"
							 * , description));
							 */

							errorContent.append("security not found: " + "\n");
							errorContent.append(String.format(
									"\tsecurity = %s", security) + "\n");
							errorContent.append(String.format("\tsource = %s",
									source) + "\n");
							errorContent.append(String.format(
									"\terrorCode = %s", errorCode) + "\n");
							errorContent.append(String.format(
									"\tcategory = %s", category) + "\n");
							errorContent.append(String.format(
									"\tdescription = %s", description) + "\n");
						}
					} catch (Exception ex) {
						// System.err.println(ex.getMessage());
						errorContent.append(ex.getMessage() + "\n");
					}

				}
				break;
			}
			}
		}

		// From here, the data is gotten
		private void dumpEvent(Event event) throws Exception {
			// System.out.println();
			// System.out.println("eventType=" + event.eventType());

			responseContent.append("\n" + "eventType=" + event.eventType()
					+ "\n");

			MessageIterator messageIterator = event.messageIterator();
			SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");

			while (messageIterator.hasNext()) {
				Message message = messageIterator.next();

				String security = message.topicName();
				for (int i = 0; i < MarketDataSubscriptionActivity._fields
						.size(); i++) {
					// This ignores the extraneous fields in the response
					String field = MarketDataSubscriptionActivity._fields
							.get(i);
					if (message.hasElement(field, true)) // be careful,
															// excludeNullElements
															// is false by
															// default
					{
						Element elmField = message.getElement(field);

						String output = String.format("%s: %s, %s", fmt
								.format(new Date()), security, elmField
								.toString().trim());
						// System.out.println(output);
						responseContent.append(output + "\n");
					}
				}
			}
			// System.out.println();
			responseContent.append("\n");
		}
	}

	public void request(View view) {
		handler.removeCallbacksAndMessages(null);

		responseTV.setText("");
		errorTV.setText("");
		//Session.threadLoopCount = 0;

		// Flip the visibility of the buttons
		request.setVisibility(View.GONE);
		stop.setVisibility(View.VISIBLE);

		startThread();
	}

	public void stopThread() {
		session.stopAsync();
	}

	public void stop(View view) {
		stopThread();
	}
	
	public Handler getHandler()
	{
		return this.handler;
	}
}
