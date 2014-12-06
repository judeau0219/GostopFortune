package com.ndl.android.gostopfortune.viewgroup;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.judeau.util.NumberUtil;
import com.ndl.android.gostopfortune.R;
import com.ndl.android.gostopfortune.datamodel.CardModel;
import com.ndl.android.gostopfortune.task.BitmapWorkerTask;

public class CardView extends FrameLayout implements ValueAnimator.AnimatorUpdateListener, AnimatorListener {

	public CardView(Context context) {
		super(context);
		initialize(context, null);
	}

	public CardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context, attrs);
	}
	
	private Context mContext;
	
	private CardModel mCardModel;
	
	private ImageView mFrontView;
	private ImageView mBackView;
	
	private FrameLayout mSelected;
	private ImageView mOnView;
	
	private boolean mFront = false;
	private boolean mRunning = false;
	
	public float homeScale = 1;
	
	public float endY = 0;
	
	private void initialize(Context context, AttributeSet attrs){
		mContext = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_card, this, true);
		
		mFrontView = (ImageView) findViewById(R.id.img_front);
		mBackView = (ImageView) findViewById(R.id.img_back);
		
		mSelected = (FrameLayout) findViewById(R.id.img_selected);
		mOnView = (ImageView) findViewById(R.id.img_on);
		
		setFront(mFront);
	}
	
	public CardModel getCardModel(){
		return mCardModel;
	}
	public void setCardModel(CardModel model){
		mCardModel = model;
		
		BitmapWorkerTask task = new BitmapWorkerTask(getContext(), mFrontView);
	    task.execute(getCardModel().resource);
	}
	
	public boolean getFront(){
		return mFront;
	}
	public void setFront(boolean isFront){
		mFront = isFront;
		
		int tr = (mFront) ? 0 : 180;
		this.setRotationY(tr);
		
		mSelected.setVisibility(View.GONE);
		removeSelectedAnimation();
		
		setImageVisible(mFront);
	}
	
	public void reverseAnimation(boolean front){
		reverseAnimation(front, 200, 0);
	}
	public void reverseAnimation(boolean front, long duration){
		reverseAnimation(front, duration, 0);
	}
	public void reverseAnimation(boolean front, long duration, long delay){	
		if(mRunning) return;
		
		mSelected.setVisibility(View.GONE);
		removeSelectedAnimation();
		
		mFront = front;
		
		ValueAnimator ani = ValueAnimator.ofFloat(0, 100);
		ani.setStartDelay(delay);
		ani.setDuration(duration);
		ani.setInterpolator(new AccelerateDecelerateInterpolator());
		ani.addUpdateListener(this);
		ani.addListener(this);
		
		ani.start();
	}

	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		float x = (float) animation.getAnimatedValue();
		float tr = (mFront) ? 0 : 180;
		float rotationY = NumberUtil.getLinearFunctionResult(x, 0, 100, this.getRotationY(), tr);
		
		if(rotationY > 90 && rotationY <= 270){ // back
			if(mBackView.getVisibility() == View.GONE){
				setImageVisible(false);
			}
		}else{
			if(mFrontView.getVisibility() == View.GONE){
				setImageVisible(true);
			}
		}
		
		this.setRotationY(rotationY);
		
		float x2 = Math.abs(rotationY - 90);
		
		float scale = NumberUtil.getLinearFunctionResult(x2, 90, 0, homeScale, homeScale*1.1f);
		
		this.setScaleX(scale);
		this.setScaleY(scale);
	}
	
	private void setImageVisible(boolean front){
		if( front ){
			mFrontView.setVisibility(View.VISIBLE);
			mBackView.setVisibility(View.GONE);
		}else{
			mFrontView.setVisibility(View.GONE);
			mBackView.setVisibility(View.VISIBLE);
		}
	}
	
	public void setSelectedVisible(boolean selected){
		if(selected){
			mSelected.setVisibility(View.VISIBLE);
			addSelectedAnimation();
		}else{
			mSelected.setVisibility(View.GONE);
			removeSelectedAnimation();
		}
	}
	
	private ObjectAnimator mAni;
	
	private void addSelectedAnimation(){
		mAni = ObjectAnimator.ofFloat(mOnView, "alpha", 0 );
		
		mAni.setDuration(200);
		mAni.setRepeatCount(Animation.INFINITE);
		mAni.setRepeatMode(ValueAnimator.REVERSE);
		mAni.start();
	}
	
	private void removeSelectedAnimation(){
		if(mAni != null){
			if(mAni.isRunning())	mAni.cancel();
			mOnView.setAlpha(1.0f);
		}
	}
	
	//
	@Override
	public void onAnimationStart(Animator animation) {
		mRunning = true;
	}
	@Override
	public void onAnimationEnd(Animator animation) {
		mRunning = false;
	}
	@Override
	public void onAnimationCancel(Animator animation) {
		mRunning = false;
	}
	@Override
	public void onAnimationRepeat(Animator animation) {}
/*
	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;
	    private int data = 0;

	    public BitmapWorkerTask(ImageView imageView) {
	        // ImageView 의 WeakReference 가지고 있게 하여 사용자가 다른창으로 이동하거나 하면
	    	// 작업이 끝나지 않았어도 ImageView 가 가비지 콜렉팅 되게 하였다.
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(Integer... params) {
	        data = params[0];
	        return decodeSampledBitmapFromResource(getResources(), data, 100, 100);
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            if (imageView != null) { // imageView 가 살아있는지 체크
	                imageView.setImageBitmap(bitmap);
//	                if(mImageLoadListener != null) mImageLoadListener.onImageLoadComplete();
	            }
	        }
	    }
	}
	
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

		// 읽어들이려는 이미지의 해상도만 알아낸다
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    // 이미지의 해상도를 줄인다. 
	    // 예) options.inSampleSize = 4 >> 이미지를 1/4 로 해상도를 줄인다.
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

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
