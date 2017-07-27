package com.janino.jsportstep.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.janino.jsportstep.R;


public class PedoActivity extends FragmentActivity implements OnClickListener {
	private Fragment mStepFragment;

	private Button check_btn;
	private Button step_btn;
	private FragmentManager fm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedo);

		check_btn = (Button) findViewById(R.id.check_btn);
		check_btn.setOnClickListener(this);
		step_btn = (Button) findViewById(R.id.step_btn);
		step_btn.setOnClickListener(this);

		fm = getSupportFragmentManager();
		mStepFragment = fm.findFragmentById(R.id.container);
		if (mStepFragment == null) {
			mStepFragment = new StepFragment();
		}
		if (savedInstanceState == null) {
			fm.beginTransaction().add(R.id.container, mStepFragment).commit();
		}
	}

	private CheckFragment checkFragment;

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.check_btn:
			if (checkFragment == null) {
				checkFragment = new CheckFragment();
			}
			fm.beginTransaction().replace(R.id.container, checkFragment)
					.commit();
			break;
		case R.id.step_btn:
			if (mStepFragment == null) {
				mStepFragment = new StepFragment();
			}
			fm.beginTransaction().replace(R.id.container, mStepFragment)
					.commit();

			break;
		}
	}
}
