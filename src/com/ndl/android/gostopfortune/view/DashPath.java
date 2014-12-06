package com.ndl.android.gostopfortune.view;

import com.ndl.android.gostopfortune.*;

import android.content.*;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.graphics.Shader.*;
import android.util.*;
import android.view.*;
import android.view.View.*;

public class DashPath extends View {

	public DashPath(Context context) {
		super(context);
	}
	
	public DashPath(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public DashPath(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	protected void onDraw(Canvas canvas)
	{
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		Bitmap bgTile = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.img_dashpath);
		paint.setShader(new BitmapShader(bgTile, TileMode.REPEAT, TileMode.REPEAT));
		canvas.drawRect(0, 0, this.getWidth(), 3, paint);
		
		/*
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setAntiAlias(false);
		
		paint.setStyle(Paint.Style.STROKE);
		DashPathEffect dashPath = new DashPathEffect(new float[]{50,50}, 2);  
		paint.setPathEffect(dashPath);
		paint.setStrokeWidth(3);
		paint.setColor(0xffcfb695);
		
		canvas.drawLine(0,0,this.getWidth(),0,paint);
		*/
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int wSize = MeasureSpec.getSize(widthMeasureSpec);
//		int hSize = MeasureSpec.getSize(heightMeasureSpec);
		
		setMeasuredDimension(wSize, 3);
	}
}
