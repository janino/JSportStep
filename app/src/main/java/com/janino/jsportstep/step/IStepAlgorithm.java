package com.janino.jsportstep.step;

public interface IStepAlgorithm {
	public int detectStep(long timeStamp, float[] indata);
	public void setParameter(double... args);
}
