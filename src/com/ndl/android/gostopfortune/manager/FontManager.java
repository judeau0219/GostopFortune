package com.ndl.android.gostopfortune.manager;

import android.content.Context;
import android.graphics.Typeface;

public class FontManager {

private static Typeface TF_NANUM;
	
	public static Typeface getTypeFaceNanum(Context context){
		if(TF_NANUM == null){
			TF_NANUM = Typeface.createFromAsset(context.getAssets(), "fonts/NanumMyeongjoExtraBold.ttf");
		}
		
		return TF_NANUM;
	}

}
