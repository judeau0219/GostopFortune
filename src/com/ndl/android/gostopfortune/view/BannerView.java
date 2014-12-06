package com.ndl.android.gostopfortune.view;

import com.judeau.util.*;
import com.ndl.android.gostopfortune.*;

import android.content.*;
import android.graphics.*;
import android.graphics.Shader.*;
import android.util.*;
import android.view.*;
import android.view.View.*;

public class BannerView extends View {

	public BannerView(Context context) {
		super(context);
	}
	
	public BannerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	protected void onDraw(Canvas canvas)
	{
		Bitmap bmpGradient = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bg_banner);
		canvas.drawBitmap(bmpGradient, null, new Rect(0, 0, this.getWidth(), this.getHeight()-5), null);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		/*
		Bitmap bgTile = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bg_banner_tile);
		paint.setShader(new BitmapShader(bgTile, TileMode.REPEAT, TileMode.REPEAT));
		canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);
		*/
		Bitmap bmpDori = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.banner_dori);
		int doriHeight = (int) (this.getHeight()*0.94f);
		float doriScaleW = (float)doriHeight/(float)bmpDori.getHeight();
		int doriWidth = (int)(bmpDori.getWidth() * doriScaleW);
		
		Bitmap bmpTxt = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.banner_txt);
		int txtHeight = (int) (this.getHeight()*0.42f);
		float txtScaleH = (float)txtHeight/(float)bmpTxt.getHeight();
		int txtWidth = (int)(bmpTxt.getWidth() * txtScaleH);
		int txtY = (int) ((this.getHeight()-txtHeight)*0.5f);
		
		int space = UnitUtil.dpiToPixel(getContext(), 6);
		int startX = (int)( ( this.getWidth() - (doriWidth + space + txtWidth) ) * 0.5f ) - space;
		
		canvas.drawBitmap(bmpDori, null, new Rect(startX, 0, startX + doriWidth, doriHeight), null);
		canvas.drawBitmap(bmpTxt, null, new Rect(startX + doriWidth + space, txtY, startX + doriWidth + space + txtWidth, txtY + txtHeight), null);
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int wSize = MeasureSpec.getSize(widthMeasureSpec);
		int hSize = MeasureSpec.getSize(heightMeasureSpec);
		
		setMeasuredDimension(wSize, hSize);
	}

}
