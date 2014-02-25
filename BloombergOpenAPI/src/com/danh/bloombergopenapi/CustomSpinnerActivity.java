package com.danh.bloombergopenapi;

import java.util.ArrayList;

import com.bemu.BEmu.Requests.RunHistoricalDataRequest;
import com.bemu.BEmu.Requests.RunIntradayBarDataRequest;
import com.bemu.BEmu.Requests.RunIntradayTickDataRequest;
import com.bemu.BEmu.Requests.RunReferenceDataRequest;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CustomSpinnerActivity extends Activity {

	public ArrayList<SpinnerModel> CustomListViewValuesArr = new ArrayList<SpinnerModel>();
	private CustomAdapter adapter;
	private CustomSpinnerActivity activity = null;

	private Spinner spinner;
	private String[] request = { "", "Send a Reference Data request",
			"Send a Market Data request", "Send a Historical Data request",
			"Send an Intraday Tick Data request",
			"Send an Intraday Bar Data request" };
	private String[] website = { "", "jobs.bloomberg.com/",
			"www.bloomberg.com", "www.bloomberg.com/company/",
			"www.bloomberg.com/mobile/", "www.bloombergtradebook.com/" };

	private TextView responseTV = null;
	private TextView errorTV = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_spinner);

		// References
		System.out.println("Bloomberg API Emulator Examples");
		System.out.println("http://bemu.codeplex.com/");
		System.out.println("By: Robinson664");

		// Spinner View
		activity = this;

		spinner = (Spinner) findViewById(R.id.spinner1);

		// Text View
		responseTV = (TextView) findViewById(R.id.responseTextView);
		errorTV = (TextView) findViewById(R.id.errorTextView);
		responseTV.setMovementMethod(new ScrollingMovementMethod());
		errorTV.setMovementMethod(new ScrollingMovementMethod());

		// Put data (the request[] strings) into the ArrayList
		// (CustomListViewValuesArr)
		setListData();

		Resources res = getResources();
		adapter = new CustomAdapter(activity, R.layout.spinner_rows,
				CustomListViewValuesArr, res);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				// Clear the current texts
				responseTV.setText("");
				errorTV.setText("");

				int index = spinner.getSelectedItemPosition();
				if (index != 0) {
					Toast.makeText(getBaseContext(),
							"You have selected item : " + request[index],
							Toast.LENGTH_SHORT).show();

					// Send request
					sendRequest(index);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// Do nothing
			}
		});
	}

	/****** Function to set data in ArrayList *************/
	public void setListData() {

		for (int i = 0; i < request.length; i++) {

			final SpinnerModel sched = new SpinnerModel();

			/******* Firstly take data in model object ******/
			sched.setRequestName(request[i]);
			sched.setImage("image" + i);

			sched.setUrl("http://" + website[i]);

			/******** Take Model Object in ArrayList **********/
			CustomListViewValuesArr.add(sched);
		}

	}

	private void sendRequest(int index) {
		switch (index) {
		case 1:
			try {
				RunReferenceDataRequest.RunExample();

				// Print out the new responses and errors
				responseTV.setText(RunReferenceDataRequest.responseContent);
				errorTV.setText(RunReferenceDataRequest.errorContent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 2:
			Intent intent = new Intent("MarketDataSubscriptionActivity");
			startActivity(intent);
			break;
		case 3:
			try {
				RunHistoricalDataRequest.RunExample();

				// Print out the new responses and errors
				responseTV.setText(RunHistoricalDataRequest.responseContent);
				errorTV.setText(RunHistoricalDataRequest.errorContent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 4:
			try {
				RunIntradayTickDataRequest.RunExample();

				// Print out the new responses and errors
				responseTV.setText(RunIntradayTickDataRequest.responseContent);
				errorTV.setText(RunIntradayTickDataRequest.errorContent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 5:
			try {
				RunIntradayBarDataRequest.RunExample();

				// Print out the new responses and errors
				responseTV.setText(RunIntradayBarDataRequest.responseContent);
				errorTV.setText(RunIntradayBarDataRequest.errorContent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * (keyCode == KeyEvent.KEYCODE_BACK) { // your code return true; }
	 * 
	 * return super.onKeyDown(keyCode, event); }
	 */

}