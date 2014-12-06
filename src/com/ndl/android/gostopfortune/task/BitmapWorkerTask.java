package com.ndl.android.gostopfortune.task;

import java.lang.ref.*;

import com.judeau.util.*;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.widget.*;

public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

	private final WeakReference<ImageView> imageViewReference;
//    private int data = 0;
    
    private Context mContext;

    public BitmapWorkerTask(Context context, ImageView imageView) {
    	mContext = context;
    	
        // ImageView 의 WeakReference 가지고 있게 하여 사용자가 다른창으로 이동하거나 하면
    	// 작업이 끝나지 않았어도 ImageView 가 가비지 콜렉팅 되게 하였다.
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        // data = params[0];
        
        return BitmapFactory.decodeResource(mContext.getResources(), params[0]); // decodeSampledBitmapFromResource(mContext.getResources(), data, 120, 180);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) { // imageView 가 살아있는지 체크
                imageView.setImageBitmap(bitmap);
            }
        }
    }
    
    /* 큰이미지를 불러와 정해진 사이즈에 맞게 표시할때..
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

		// 읽어들이려는 이미지의 해상도만 알아낸다
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    // 이미지의 해상도를 줄인다. 
	    // 예) options.inSampleSize = 4 >> 이미지를 1/4 로 해상도를 줄인다.
	    options.inSampleSize = 1; // calculateInSampleSize(options, reqWidth, reqHeight);

	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}
	
	// inSampleSize 계산
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	    
	    if (height > reqHeight || width > reqWidth) {
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	    
	    return inSampleSize;
	}
	*/
}


