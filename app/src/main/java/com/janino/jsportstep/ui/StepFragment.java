package com.janino.jsportstep.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.janino.jsportstep.R;
import com.janino.jsportstep.step.Pedometer;
import com.lidroid.xutils.util.LogUtils;

public class StepFragment extends Fragment implements OnClickListener {
	private Context mContext;
	public static final String SP_STEP_DATA_NAME = "sp_step_data";
	public static final String SP_STEP_DATA_TOTAL_DISTANCE_LAST_KEY = "sp_step_data_total_distance_last_key";
	private TextView tv_steps;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();

		View view = inflater.inflate(R.layout.fragment_step, container, false);
		tv_steps = (TextView)view.findViewById(R.id.tv_steps);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	private Pedometer.OnStepListener mOnStepListener = new Pedometer.OnStepListener() {
		@Override
		public void onStep(int step, float speed, float calories, long time,
				float distance) {
			Log.e("jsport","step=" + step + ",speed=" + speed + ",calories="
					+ calories + ",distance=" + distance);
			tv_steps.setText(""+step);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		Pedometer.getInstance().connect(getActivity());
		Pedometer.getInstance().addListener(mOnStepListener);

		LogUtils.d(Pedometer.getInstance().isDetectorRegistered(
				this.getActivity())
				+ "---isDetectorRegistered");
		if (Pedometer.getInstance().isDetectorRegistered(this.getActivity())) {

		} else {

		}
	}

	@Override
	public void onPause() {
		Pedometer.getInstance().removeListener(mOnStepListener);
		Pedometer.getInstance().disconnect(getActivity());
		super.onPause();
	}

	private void setSpTotalDistanceLast(Context context, float value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(
				SP_STEP_DATA_NAME, Context.MODE_PRIVATE).edit();
		editor.putFloat(SP_STEP_DATA_TOTAL_DISTANCE_LAST_KEY, value);
		editor.commit();
	}

	private float getSpTotalDistanceLast(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_STEP_DATA_NAME,
				Context.MODE_PRIVATE);
		return sp.getFloat(SP_STEP_DATA_TOTAL_DISTANCE_LAST_KEY, 0);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {

	}

}
