package com.danh.bloombergopenapi;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class CustomAdapter extends ArrayAdapter<String> {

	private Activity activity;
	private ArrayList data;
	public Resources res;
	SpinnerModel tempValues = null;
	LayoutInflater inflater;

	/************* CustomAdapter Constructor *****************/
	public CustomAdapter(CustomSpinnerActivity activitySpinner, int textViewResourceId,
			ArrayList objects, Resources resLocal) {
		super(activitySpinner, textViewResourceId, objects); // there'll be an
																// error in this
																// line if we
																// put in
																// ArrayList<SpinnerModel>
																// instead of
																// just
																// ArrayList

		/********** Take passed values **********/
		activity = activitySpinner;
		data = objects;
		res = resLocal;

		/*********** Layout inflator to call external xml layout () **********************/
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {

		/********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
		View row = inflater.inflate(R.layout.spinner_rows, parent, false);

		/***** Get each Model object from Arraylist ********/
		tempValues = null;
		tempValues = (SpinnerModel) data.get(position);

		TextView request = (TextView) row.findViewById(R.id.request);
		TextView description = (TextView) row.findViewById(R.id.description);
		ImageView companyLogo = (ImageView) row.findViewById(R.id.image);

		if (position == 0) {
			// Default selected Spinner item
			request.setText("[Select a request...]");
			description
					.setText("Please note that the data in this request is "
							+ "completely random; it is not real data. "
							+ "Do not make any trading or investment decisions using the data shown here.");
		} else {
			// Set values for spinner each row
			request.setText(tempValues.getRequestName());
			description.setText(tempValues.getUrl());
			companyLogo.setImageResource(res.getIdentifier(
					"com.danh.bloombergopenapi:drawable/"
							+ tempValues.getImage(), null, null));

		}

		return row;
	}
}
