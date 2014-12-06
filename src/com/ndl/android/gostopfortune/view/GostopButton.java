package com.ndl.android.gostopfortune.view;

import com.judeau.util.*;
import com.ndl.android.gostopfortune.*;

import android.content.*;
import android.graphics.*;
import android.graphics.Shader.*;
import android.graphics.drawable.*;
import android.util.*;
import android.view.*;
import android.view.View.*;

public class GostopButton extends View {
	public GostopButton(Context context) {
		super(context);
		initialize();
	}
	
	public GostopButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}
	
	public GostopButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize();
	}
	
	private void initialize()
	{
		this.setBackgroundResource(R.drawable.btn_gostop_bg);
	}
	
	protected void onDraw(Canvas canvas)
	{
		/*
		Bitmap bmpGradient = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.btn_gostop_bg);
		canvas.drawBitmap(bmpGradient, null, new Rect(0, 0, this.getWidth(), this.getHeight()), null);
		*/
		
		Bitmap bmpDori = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.btn_gostop_dori);
		int doriHeight = (int) (this.getHeight()*0.86f);
		float doriScaleW = (float)doriHeight/(float)bmpDori.getHeight();
		int doriWidth = (int)(bmpDori.getWidth() * doriScaleW);
		int doriY = this.getHeight() - doriHeight - 4;
		
		Bitmap bmpTxt = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.btn_gostop_txt);
		int txtWidth = (int) (this.getWidth()*0.56f);
		float txtScaleH = (float)txtWidth/(float)bmpTxt.getWidth();
		int txtHeight = (int)(bmpTxt.getHeight() * txtScaleH);
		int txtY = (int) ((this.getHeight()-txtHeight)*0.5f);;
		
		int space = UnitUtil.dpiToPixel(getContext(), 6);
		int startX = (int)( ( this.getWidth() - (doriWidth + space + bmpTxt.getWidth()) ) * 0.5f );
		
		canvas.drawBitmap(bmpDori, null, new Rect(startX, doriY, startX + doriWidth, doriY + doriHeight), null);
		canvas.drawBitmap(bmpTxt, null, new Rect(startX + doriWidth + space, txtY, startX + doriWidth + space + txtWidth, txtY + txtHeight), null);
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int wSize = MeasureSpec.getSize(widthMeasureSpec);
		int hSize = MeasureSpec.getSize(heightMeasureSpec);
		
		setMeasuredDimension(wSize, hSize);
	}
	
}
