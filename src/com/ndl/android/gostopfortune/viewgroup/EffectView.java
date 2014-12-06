package com.ndl.android.gostopfortune.viewgroup;

import com.judeau.util.NumberUtil;
import com.ndl.android.gostopfortune.R;
import com.ndl.android.gostopfortune.animation.CustomInterpolator;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.*;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class EffectView extends FrameLayout implements ValueAnimator.AnimatorUpdateListener, AnimatorListener {

	public EffectView(Context context) {
		super(context);
		initialize(context, null);
	}

	public EffectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public EffectView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}
	
	private ImageView mEffectView;
	
	private void initialize(Context context, AttributeSet attrs){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_effect, this, true);
		
		mEffectView = (ImageView) findViewById(R.id.img_effect);
		mEffectView.setAlpha(0f);
		
		this.setPivotX(0);
		this.setPivotY(0);
	}
	
	public void startEffect(){
		mEffectView.setVisibility(View.VISIBLE);
		mEffectView.setAlpha(1f);
		mEffectView.setRotation(0);
		
		ValueAnimator ani = ValueAnimator.ofFloat(0, 1, 1.2f);
		ani.setStartDelay(0);
		ani.setDuration(600);
		ani.setInterpolator(new DecelerateInterpolator());// CustomInterpolator(0.15f));
		ani.addUpdateListener(this);
		ani.addListener(this);
		ani.start();
	}
	
	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		float x = (float) animation.getAnimatedValue();
		
		float scale = NumberUtil.getLinearFunctionResult(x, 0, 0.4f, 0, 1);
		mEffectView.setScaleX(scale);
		mEffectView.setScaleY(scale);
		
		if(x >= 1){
			float alpha = NumberUtil.getLinearFunctionResult(x, 1.0f, 1.2f, 1, 0);
			mEffectView.setAlpha(alpha);
			
			mEffectView.setScaleX(x);
			mEffectView.setScaleY(x);
		}
		
		float rotation = NumberUtil.getLinearFunctionResult(x, 0, 1.2f, 0, 360*1.6f);
		mEffectView.setRotation(rotation);
	}
	
	@Override
	public void onAnimationEnd(Animator animator) {
		mEffectView.setVisibility(View.GONE);
		if(mEffectListener != null) mEffectListener.onEffectEnd();
	}
	
	private OnEffectListener mEffectListener;
	public void setOnEffectListener(OnEffectListener listener){
		mEffectListener = listener;
	}
	public interface OnEffectListener{
		public void onEffectEnd();
	}
	
	@Override
	public void onAnimationCancel(Animator animator) {}
	@Override
	public void onAnimationRepeat(Animator animator) {}
	@Override
	public void onAnimationStart(Animator animator) {}

}
