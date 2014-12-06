package com.ndl.android.gostopfortune.animation;

import android.animation.TimeInterpolator;


public class CustomInterpolator implements TimeInterpolator {

	float pos = 0;
	float spd = 0.13f;
	
	public CustomInterpolator(){
		this(0.13f);
	}
	
	public CustomInterpolator(float speed){
		spd = speed;
	}
	
	public float getInterpolation(float input) {
		pos += spd * (1 - pos);
		if(input == 1) pos = 1;
		return pos;
	}

}
