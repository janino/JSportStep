package com.janino.jsportstep.heartbeat;

import android.hardware.SensorEvent;

public class GravityDetector {
	static final int POOL_SIZE = 200;
	int currentSize_ = 0;
	double[] dataPool_ = new double['È'];
	private double gravity_;

	public GravityDetector() {
		reinit();
	}

	public double gravity() {
		return this.gravity_;
	}

	public void pushData(SensorEvent paramSensorEvent) {
		double d1 = Math.sqrt(paramSensorEvent.values[0]
				* paramSensorEvent.values[0] + paramSensorEvent.values[1]
				* paramSensorEvent.values[1] + paramSensorEvent.values[2]
				* paramSensorEvent.values[2]);
		this.dataPool_[this.currentSize_] = d1;
		this.currentSize_ = (1 + this.currentSize_);
		if (this.currentSize_ == 200) {
			this.currentSize_ = 0;
			double d2 = 0.0D;
			for (int i = 0; i < 200; i++)
				d2 += this.dataPool_[i];
			double d3 = d2 / 200.0D;
			double d4 = 0.0D;
			for (int j = 0; j < 200; j++)
				d4 += Math.abs(this.dataPool_[j] - d3);
			if (d4 / 200.0D < d3 / 20.0D)
				this.gravity_ = ((float) d3);
		}
	}

	public void reinit() {
		this.currentSize_ = 0;
		this.gravity_ = 9.812344551086426D;
	}
}
