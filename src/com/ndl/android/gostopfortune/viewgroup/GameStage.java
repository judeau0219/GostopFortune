package com.ndl.android.gostopfortune.viewgroup;

import java.util.ArrayList;
import java.util.Collections;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.media.*;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;

import com.judeau.util.*;
import com.ndl.android.gostopfortune.R;
import com.ndl.android.gostopfortune.animation.CustomInterpolator;
import com.ndl.android.gostopfortune.datamodel.*;
import com.ndl.android.gostopfortune.manager.*;
import com.ndl.android.gostopfortune.viewgroup.EffectView.OnEffectListener;

public class GameStage extends FrameLayout implements View.OnClickListener, View.OnTouchListener, AnimatorListener, 
																					ValueAnimator.AnimatorUpdateListener, OnEffectListener {

	public GameStage(Context context) {
		super(context);
		initialize(context, null);
	}

	public GameStage(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public GameStage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}
	
	private static final String ANI_SHOW = "show";
	private static final String ANI_INIT = "init";
	private static final String ANI_MATCH = "match";
	private static final String ANI_SELECTED = "selected";
	private static final String ANI_DOWN = "down";
	private static final String ANI_COLLAPSE = "collapse";
	private static final String ANI_HOLD = "hold";
	private static final String ANI_RESULT = "result";
	
	private static final int DRAG_VELOCITY = 100;
	
	private Context mContext;
	
	private int mTotalLen = 48;
	private int mBaseLen = 20;
	
	private int mCol = 4;
	
	private float mDownScale = 0.53f;
	private float mHoldScale = 1;
	
	private int mStageW;
	private int mStageH;
	
	private int mCardW;
	private int mCardH;
	
	private int mHoldCardW;
	private int mHoldCardH;
	
	private int mDownCardW;
	private int mDownCardH;
	
	private int mDownPaddingX;
	
	private int mCardHomeX;
	private int mCardHomeY;
	private int mCardInitY;
	
	private int mSideSpace;
	
	private int mBaseStartX;
	private int mBaseStartY;
	private int mBaseSpaceY;
	
	private int mDownStartY;
	private int mDownSpaceX;
	private int mDownFirstSpaceX;
	
	private int mFingerH;
	private int mHandH;
	
	private int mPrevY = 0;
	private int mPeepY = 0;
	private int mDragDy = 0;
	private int mEndY = 0;
	
	private int mEffectW = 0;
	private int mEffectH = 0;
	
	private int mArrowTargetY;
	private int mArrowHomeY;
	
	private int mSelectedIndex = 0;
	
	private VelocityTracker mVelocityTracker = null;
	
	private boolean mSetLayoutComplete = false;
	private boolean mRestart = false;
	private boolean mDownShift = false;
	
	private String mAniState;
	private int mAniLen = 0;
	private int mAniStartCount = 0;
	private int mAniEndCount = 0;
	
	private ArrayList<Point> mPositionList = new ArrayList<Point>();
	private ArrayList<CardModel> mModelList = new ArrayList<CardModel>();
	
	private ArrayList<ImageView> mAreaList = new ArrayList<ImageView>();
	private ArrayList<CardView> mTempList = new ArrayList<CardView>();
	private ArrayList<CardView> mDownList = new ArrayList<CardView>();
	private ArrayList<CardView> mHoldList = new ArrayList<CardView>();
	private ArrayList<ArrayList<CardView>> mSelectedList = new ArrayList<ArrayList<CardView>>();
	private ArrayList<ArrayList<CardView>> mBaseList = new ArrayList<ArrayList<CardView>>();
	
	private LinearLayout mFloor;
	private FrameLayout mCard;
	private ImageView mHand;
	private ImageView mFinger;
	private EffectView mEffect;
	private ImageView mArrow;
	private FrameLayout mDrag;
	
	private ImageView mStartBtn;
	
	private LinearLayout mLinearResult;
	private ImageView mResultShowBtn;
	private ImageView mRestartBtn;
	
	private SoundPool mPool = null;
	
//	private int mSoundStart;
	private int mSoundClick;
	private int mSoundReverse;
	private int mSoundDingdong;
	private int mSoundDown;
	
	private MediaPlayer mPlayer;
	
	private AnimatorSet mAniSet;
	
	// 초기화
	private void initialize(Context context, AttributeSet attrs){
		mContext = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.game_stage, this, true);
		
		mDragDy = getResources().getDimensionPixelSize(R.dimen.drag_distance_y);
		
		mFloor = (LinearLayout) findViewById(R.id.lin_floor);
		mCard = (FrameLayout) findViewById(R.id.fra_card);
		mHand = (ImageView) findViewById(R.id.img_hand);
		mFinger = (ImageView) findViewById(R.id.img_finger);
		mDrag = (FrameLayout) findViewById(R.id.fra_drag);
		mArrow = (ImageView) findViewById(R.id.img_arrow);
		
		mStartBtn = (ImageView) findViewById(R.id.btn_start);
		
		mLinearResult = (LinearLayout) findViewById(R.id.linear_result);
		mResultShowBtn = (ImageView) findViewById(R.id.btn_result_show);
		mRestartBtn = (ImageView) findViewById(R.id.btn_result_restart);
		
		mStartBtn.setOnClickListener(this);
		mResultShowBtn.setOnClickListener(this);
		mRestartBtn.setOnClickListener(this);
		
		initSound();
		makeCardList();
	}
	
	private void makeCardList(){
		
		for( int i=0; i<4; i++ ){
			ImageView av = new ImageView(mContext);
			av.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			av.setImageResource(R.drawable.selected_area);
			mAreaList.add(av);
			mFloor.addView(av);
		}
		
		for( int i=0; i<mTotalLen; i++){
			CardView cv = new CardView(mContext);
			cv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			mCard.addView(cv);
			mHoldList.add(cv);
			
			int mainIndex = (int)(i/mCol) + 1;
			int subIndex = (i%mCol) + 1;
			int resource = R.drawable.card_01_1 + i;
			mModelList.add(new CardModel(mainIndex, subIndex, resource));
		}
		
		for(int i=0; i<mCol; i++){
			mSelectedList.add(new ArrayList<CardView>());
			mBaseList.add(new ArrayList<CardView>());
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		mStageW = MeasureSpec.getSize(widthMeasureSpec);
		mStageH = MeasureSpec.getSize(heightMeasureSpec);
		
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		
		// 측정된 너비와 높이를 저장하기 위해 onMeasure 함수내에서 호출되는 메소드이다.
		setMeasuredDimension(mStageW, mStageH);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// ViewGroup 를 상속받는 경우 super 를 호출하지 않고 child 의 layout을 호출하여 초기 위치를 설정해 주어야 한다.
		super.onLayout(changed, l, t, r, b);
		
		if(mSetLayoutComplete) return;
		
		setCardLayout();
		mSetLayoutComplete = true;
	}
	
	private float setScaleObject(View v, float rate, boolean zeroPivot){
		float temp = mStageH*rate;
		float scale = temp/v.getHeight();
		
		v.setScaleX(scale);
		v.setScaleY(scale);
		
		if(zeroPivot){
			v.setPivotX(0);
			v.setPivotY(0);
		}
		
		return scale;
	}
	
	private void setCardSize(){
		mCardW = mHoldList.get(0).getWidth();
		mCardH = mHoldList.get(0).getHeight();
		
		float temp = mStageH*0.3f;
		mHoldScale = temp/mCardH;
		
		temp = mStageH*0.155f;
		mDownScale = temp/mCardH;
		
		mHoldCardW = (int) (mCardW * mHoldScale);
		mHoldCardH = (int) (mCardH * mHoldScale);
		
		mDownCardW = (int) (mCardW * mDownScale);
		mDownCardH = (int) (mCardH * mDownScale);
		
		mBaseSpaceY = (int) (mDownCardH*0.1);
		mDownSpaceX = (int) (mDownCardW*0.15);
		mDownFirstSpaceX = (int) (mDownCardW*0.7);
		
		int holdPaddingX = (int)((mHoldCardW-mCardW)*0.5);
		int holdPaddingY = (int)((mHoldCardH-mCardH)*0.5);
		
		mDownPaddingX = (int)((mDownCardW-mCardW)*0.5);
		int downPaddingY = (int)((mDownCardH-mCardH)*0.5);
		
		mDownStartY += downPaddingY;
		mBaseStartY += downPaddingY;
		
		mCardHomeX = (int)((mStageW - mHoldCardW)*0.5 + holdPaddingX);
		mCardHomeY = (int)(mStageH - (mHoldCardH*0.9) + holdPaddingY);
		mCardInitY = (int)(mStageH + holdPaddingY);
		mPeepY = (int)(mCardHomeY - (mHoldCardH * 0.1));
	}
	
	private void setFloor(){
		((ImageView)findViewById(R.id.img_area_line)).setY(mStageH*0.23f);
		
		float scale = setScaleObject(mAreaList.get(0), 0.168f, false);
		int areaW = (int)(mAreaList.get(0).getWidth()*scale);
		int areaH = (int)(mAreaList.get(0).getHeight()*scale);
		
		float spaceX = areaW + (mStageW - mSideSpace*2 - areaW*4)/3;
		float startY = mStageH*0.064f;
		
		for(int i=0; i<4; i++){
			ImageView iv = mAreaList.get(i);
			iv.setPivotX(0);
			iv.setPivotY(0);
			
			int tx = (int)(mSideSpace + spaceX*i);
			int ty = (int)startY;
			
			iv.setX(tx);
			iv.setY(ty);
			iv.setScaleX(scale);
			iv.setScaleY(scale);
			
			tx += (int)((areaW - mCardW) * 0.5);
			ty += (int)((areaH - mCardH) * 0.5);
			mPositionList.add(new Point(tx, ty));
		}
	}
	
	private void setObject(){
		float scale;
		
		scale = setScaleObject(mFinger, 0.054f, true);
		int fingerW = (int)(mFinger.getWidth() * scale);
		mFingerH = (int)(mFinger.getHeight() * scale);
		
		scale = setScaleObject(mHand, 0.164f, true);
		int handW = (int)(mHand.getWidth() * scale);
		mHandH = (int)(mHand.getHeight() * scale);
		
		mFinger.setX((mStageW - fingerW)*0.5f);
		mHand.setX((mStageW - handW)*0.5f);
		
		scale = setScaleObject(mArrow, 0.173f, true);
		int arwW = (int)(mArrow.getWidth()*scale);
		int arwH = (int)(mArrow.getHeight()*scale);
		int arwX = (int)((mStageW-arwW)*0.5);
		mArrowTargetY = (int)(mCardHomeY - arwH*0.4);
		mArrowHomeY = mCardHomeY;
		
		mArrow.setX(arwX);
		mArrow.setY(mArrowHomeY);
		
		mLinearResult.setVisibility(View.GONE);
	}
	
	private void setCardLayout(){
		if(!mSetLayoutComplete){
			mSideSpace = (int)(mStageW*0.06f);
			mBaseStartY = (int)(mStageH*0.28f);
			mDownStartY = (int)(mStageH*0.52f);
			mEndY = (int)(mStageH*0.63f);
			
			setCardSize();
			setFloor();
			setObject();
			removeArrow();
			setDragText();
			
//			soundPlay(mSoundStart);
		}
		
		for(int i=0; i<mTotalLen; i++){
			CardView cv = mHoldList.get(i);
			
			cv.setFront(false);
			
			cv.setX(mCardHomeX);
			cv.setY(mCardInitY);
			
			cv.homeScale = mHoldScale;
			
			cv.setScaleX(mHoldScale);
			cv.setScaleY(mHoldScale);
		}
		
		mHand.setY(mStageH);
		mFinger.setY(mStageH + (mHandH-mFingerH));
	}
	
	private void setDragText(){
		TextView txtDrag = (TextView) findViewById(R.id.txt_drag);
		txtDrag.setTypeface(FontManager.getTypeFaceNanum(mContext));
		TextUtil.setTextSizeByDensity(mContext, txtDrag, 18);
		TextUtil.setTextViewColorPartial(txtDrag, getResources().getString(R.string.drag_guide), getResources().getString(R.string.drag_guide_highlight), 0xffffeb73);
	}
	
	
	// 게임 시작
	public void startGame(){
		if(mRestart){
			for(int i=0; i<mSelectedList.size(); i++){
				mHoldList.addAll(mSelectedList.get(i));
				mSelectedList.get(i).clear();
			}
			
			for(int i=0; i<mBaseList.size(); i++){
				mHoldList.addAll(mBaseList.get(i));
				mBaseList.get(i).clear();
			}
			
			mHoldList.addAll(mDownList);
			mDownList.clear();
			
			setCardLayout();
			mSelectedIndex = 0;
			
			Collections.shuffle(mModelList);
		}
		
		mLinearResult.setVisibility(View.GONE);
		
		mStartBtn.setVisibility(View.GONE);
		mStartBtn.setOnClickListener(null);
		
		showCard();
		mRestart = true;
	}
	
	private void showCard(){
		mAniState = ANI_SHOW;
		
		Collections.shuffle(mModelList);
		
		long duration = 150;
		
		int temp = -UnitUtil.dpiToPixel(mContext, 20);
		int ty = mCardHomeY + mHandH + temp;
		
		for(int i=0; i<mTotalLen; i++){
			CardView cv = mHoldList.get(i);
			cv.setCardModel(mModelList.get(i));
			cardBringToFront(cv);
			
			float tr = (float) NumberUtil.getRandom(-10, 10, 2);
			cv.animate().setStartDelay(0).setDuration(duration).setInterpolator(new DecelerateInterpolator())
				.y(ty).rotation(tr).setListener(null);
		}
		
		mHand.animate().setStartDelay(0).setDuration(duration).setInterpolator(new DecelerateInterpolator()).y(mStageH);
		mFinger.animate().setStartDelay(0).setDuration(duration).setInterpolator(new DecelerateInterpolator()).y(mStageH + (mHandH-mFingerH))
			.setListener(this);
	}
	
	private void showHand(){
		long duration = 150;
		
		for(int i=0; i<mHoldList.size(); i++){
			CardView cv = mHoldList.get(i);
			cv.animate().setStartDelay(0).setDuration(duration).setInterpolator(new DecelerateInterpolator())
				.y(mCardHomeY).setListener(null);
		}
		
		mHand.animate().setStartDelay(0).setDuration(duration).setInterpolator(new DecelerateInterpolator()).y(mStageH-mHandH);
		mFinger.animate().setStartDelay(0).setDuration(duration).setInterpolator(new DecelerateInterpolator()).y(mStageH-mFingerH)
			.setListener(null);
	}
	
	private void initCard(){
		mAniStartCount = mAniEndCount = 0;
		mAniLen = mBaseLen;
		mAniState = ANI_INIT;
		
		for(int i=0; i<mBaseLen; i++){
			CardView cv = mHoldList.get(mTotalLen-i-1);
			cv.homeScale = mDownScale;
			
			int baseIndex = i%mCol;
			mBaseList.get(baseIndex).add(cv);
			
			int tx = mPositionList.get(baseIndex).x;
			int ty = mBaseStartY + mBaseSpaceY * (i/mCol);
			float tr = (float) NumberUtil.getRandom(-3, 3, 2);
			
			long duration = 300;
			long delay = i*70;
			
			cv.animate().setStartDelay(delay).setDuration(duration).setInterpolator(new DecelerateInterpolator())
				.x(tx).y(ty).scaleX(mDownScale).scaleY(mDownScale).rotation(tr).setListener(this);
		}
	}
	
	@Override
	public void onClick(View v) { 
		int id = v.getId();
		if(id == R.id.btn_start){
			startGame();
			return;
		}else if(id == R.id.btn_result_restart){
			startGame();
			return;
		}else if(id == R.id.btn_result_show){
			if(mGameListener != null) mGameListener.onShowResult();
			return;
		}
		
		CardView cv = (CardView) v;
		
		if(cv.getFront()){ // 앞면인경우
			soundPlay(mSoundClick);
			
			if(mTempList.size() > 0){ // 기존에 선택된 카드가 있는 경우
				if(cv.getCardModel().mainIndex == mTempList.get(0).getCardModel().mainIndex){ 
					if(cv.getCardModel().subIndex == mTempList.get(0).getCardModel().subIndex){
						clearTempList();
						if(mHoldList.contains(cv)) addArrow();
					}else{
						cv.setSelectedVisible(true);
						mTempList.add(cv);
						
						if(mHoldList.contains(cv)) removeArrow();
						matchAnimation();
					}
				}else{
					clearTempList();
					
					cv.setSelectedVisible(true);
					mTempList.add(cv);
					
					if(mHoldList.contains(cv)) removeArrow();
				}
			}else{
				cv.setSelectedVisible(true);
				mTempList.add(cv);
				
				if(mHoldList.contains(cv)) removeArrow();
			}
		}else{ // 뒷면인 경우 앞면으로 뒤집기
			soundPlay(mSoundReverse);
			
			cv.reverseAnimation(true);
			setHoldCardEvent(true); // 뒤집은 HoldCard에 TouchEvent달기 
			
			if(mHoldList.contains(cv)) addArrow();
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent e) { 
		CardView cv = (CardView) v;
		
		// if(mTempList.contains(cv)) return false; // 선택된 패인 경우 드래그 금지..
		
		if(mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();
		
		mVelocityTracker.addMovement(e);
		
		int action = e.getAction();
		
		switch(action){
		case MotionEvent.ACTION_DOWN:
			mPrevY = (int) e.getY();
			cardBringToFront(cv);
			break;
		case MotionEvent.ACTION_MOVE:
			int ty = (int) (cv.getY() - (mPrevY - e.getY()));
			if(mPeepY > ty) ty = mPeepY;
//			if(mCardHomeY < ty) ty = mCardHomeY;
			cv.setY(ty);
			
			break;
		case MotionEvent.ACTION_UP:
			mVelocityTracker.computeCurrentVelocity(1000);
			int velocity = Math.abs((int)mVelocityTracker.getYVelocity());
			int gapY = (int) (mPrevY - e.getY());
			
			if(velocity > DRAG_VELOCITY && gapY > mDragDy || cv.getY() == mPeepY){
				// 짝이 없는 경우 카드 내려놓기
				addDownList(cv, velocity);
				return true;
			}else{
				if(cv.getY() != mCardHomeY){
					cv.animate().setStartDelay(0).setDuration(60).setInterpolator(new CustomInterpolator(0.7f)).y(mCardHomeY).setListener(null);
					
					if(gapY > mDragDy){
						return true;
					}
				}
			}
			
			mVelocityTracker.recycle(); 
	        mVelocityTracker = null; 
		}
		
		return false;
	}
	
	
	// 게임 플로우
	private void addDownList(CardView cv, int velocity){
		/*
		velocity : 300~10000
		spd : 0.3~0.8;
		duration : 300~50;
		*/
		soundPlay(mSoundDown);
		removeArrow();
		setAllCardEvent(false);
		removeCardView(mHoldList, cv);
		
		mAniState = ANI_DOWN;
		
		int duration = 80; // (int) NumberUtil.getLinearFunctionResult(velocity, 300, 10000, 300, 50);
		float spd = 0.7f; // NumberUtil.getLinearFunctionResult(velocity, 300, 10000, 0.3f, 0.8f);
		
		int tx = mSideSpace + mDownSpaceX * mDownList.size() + mDownPaddingX;
		if(mDownList.size() > 0) tx += mDownFirstSpaceX;
		
		int ty = mDownStartY;
		cv.homeScale = mDownScale;
		
		cv.animate().setStartDelay(0).setDuration(duration).setInterpolator(new CustomInterpolator(spd))
			.x(tx).y(ty).scaleX(mDownScale).scaleY(mDownScale).rotation(0).setListener(this);
		
		cardBringToFront(cv);
		
		clearTempList();
		
		mDownList.add(cv);
	}
	
	private Point mTargetPoint;
	
	private void matchAnimation(){
		mAniState = ANI_MATCH;
		
		setAllCardEvent(false);
		
		CardView selectedCard = mTempList.get(0);
		CardView targetCard = mTempList.get(1);
		
		if(mHoldList.contains(targetCard)){
			selectedCard = mTempList.get(1);
			targetCard = mTempList.get(0);
		}else{
			mTempList.add(mTempList.remove(0));
		}
		
		mTargetPoint = new Point((int)targetCard.getX(), (int)targetCard.getY());
		
		addEffect();
		cardBringToFront(selectedCard);
		
		float tr = (float) NumberUtil.getRandom(-20, 20, 2);
		selectedCard.animate().setStartDelay(0).setDuration(150).setInterpolator(new DecelerateInterpolator())
			.x(targetCard.getX()).y(targetCard.getY()).scaleX(mDownScale).scaleY(mDownScale).rotation(tr).setListener(this);
	}
	
	private void addSelectedList(){
		mAniState = ANI_SELECTED;
		
		mAniStartCount = mAniEndCount = 0;
		mAniLen = 2;
		
		mDownShift = false;
		
		int tx = mPositionList.get(mSelectedIndex).x;
		int ty = mPositionList.get(mSelectedIndex).y;
		
		for(int i=0; i<mTempList.size(); i++){
			CardView cv = mTempList.get(i);
			cv.homeScale = mDownScale;
			
			removeCard(cv);
			
			mSelectedList.get(mSelectedIndex).add(cv);
			
			long duration = 150;
			
			float tr = (float) NumberUtil.getRandom(-4, 4, 2);
			
			cv.setOnClickListener(null);
			cv.setOnTouchListener(null);
			
			cv.reverseAnimation(false);
			cv.animate().setStartDelay(0).setDuration(duration).setInterpolator(new AccelerateDecelerateInterpolator())
				.x(tx).y(ty).scaleX(mDownScale).scaleY(mDownScale).rotation(tr).setListener(this);
			
			cardBringToFront(cv);
		}
		
		clearTempList();
		
		mSelectedIndex++;
		if(mSelectedIndex == mCol) mSelectedIndex = 0;
	}
	
	private void setPositionDownList(){
		int len = mDownList.size();
		for(int i=0; i<len; i++){
			CardView cv = mDownList.get(i);
			
			int tx = mSideSpace + mDownSpaceX * i + mDownPaddingX;
			if(i > 0) tx += mDownFirstSpaceX;
				
			int ty = mDownStartY;
			int tr = 0;
			long duration = 150;
			
			cv.animate().setStartDelay(0).setDuration(duration).setInterpolator(new DecelerateInterpolator())
				.x(tx).y(ty).rotation(tr).setListener(null);
		}
	}
	
	private void collapseDownList(){
		setAllCardEvent(false);
		
		mAniState = ANI_COLLAPSE;
		
		mAniStartCount = mAniEndCount = 0;
		mAniLen = mDownList.size();
		
		CardView lastView = mDownList.get(mDownList.size()-1);
		int tx = (int) lastView.getX();
		int ty = (int) lastView.getY();
		
		for(int i=0; i<mAniLen; i++){
			CardView cv = mDownList.get(mDownList.size()-i-1);
			cv.animate().setStartDelay(i*100).setDuration(200).setInterpolator(new DecelerateInterpolator())
				.x(tx).y(ty).rotation(0).setListener(this);
		}
	}
	
	private void restoreHoldList(){
		mAniState = ANI_HOLD;
		
		mAniStartCount = mAniEndCount = 0;
		mAniLen = mDownList.size();
		
		for(int i=0; i<mAniLen; i++){
			CardView cv = mDownList.get(i);
			cv.homeScale = mHoldScale;
			
			float tr = (float) NumberUtil.getRandom(-10, 10, 2);
			cv.animate().setStartDelay(0).setDuration(150).setInterpolator(new AccelerateDecelerateInterpolator())
				.x(mCardHomeX).y(mCardHomeY).scaleX(mHoldScale).scaleY(mHoldScale).rotation(tr).setListener(this);
			
			mHoldList.add(0, cv);
		}
		
		mDownList.clear();
	}
	
	private void alignDownList(){
		int len = mDownList.size();
		
		int areaW = mStageW-(mSideSpace*2);
		int spaceX = areaW/len;
		
		if(mDownCardW > spaceX){
			areaW -= mDownCardW-spaceX;
			spaceX = areaW/len;
		}else{
			if(spaceX > mDownCardW) spaceX = mDownCardW;
		}
		
		int startX = mSideSpace + (int)((areaW - spaceX*len)*0.5) + mDownPaddingX;
		
		for(int i=0; i<len; i++){
			CardView cv = mDownList.get(i);
			
			int tx = startX + spaceX * i;
			int ty = mDownStartY;
			int tr = 0;
			long duration = 150;
			
			cv.animate().setStartDelay(0).setDuration(duration).setInterpolator(new DecelerateInterpolator())
				.x(tx).y(ty).rotation(tr).setListener(null);
		}
	}
	
	
	// 게임 종료
	private void endGame(){
		setAllCardEvent(false);
		
		mAniState = ANI_RESULT;
		
		int areaY = mPositionList.get(0).y;
		int spaceY = (mEndY - areaY)/12;
		int len = mSelectedList.size();
		for(int i=0; i<len; i++){
			ArrayList<CardView> list = mSelectedList.get(i);
			int slen = list.size();
			for(int j=0; j<slen; j++){
				CardView cv = list.get(j);
				cv.homeScale = mDownScale;
				cv.endY = areaY + spaceY * j;
				
				if(i == len-1 && j == slen-1){
					mLinearResult.setY(cv.endY + mDownCardH + spaceY/2);
				}
			}
		}
		
		ValueAnimator ani = ValueAnimator.ofFloat(areaY, mEndY);
		ani.setStartDelay(0);
		ani.setDuration(1000);
		ani.setInterpolator(new AccelerateDecelerateInterpolator());
		ani.addUpdateListener(this);
		ani.addListener(this);
		
		ani.start();
		
		mHand.animate().setStartDelay(0).setDuration(100).setInterpolator(new DecelerateInterpolator()).y(mStageH);
		mFinger.animate().setStartDelay(0).setDuration(100).setInterpolator(new DecelerateInterpolator()).y(mStageH+(mHand.getHeight()-mFinger.getHeight()))
			.setListener(null);
	}
	
	private void showResult(){
		mLinearResult.setVisibility(View.VISIBLE);
		
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		for(int i=0; i<mSelectedList.size(); i++){
			ArrayList<Integer> list = new ArrayList<Integer>();
			
			for(int j=0; j<mSelectedList.get(i).size(); j++){
				list.add(mSelectedList.get(i).get(j).getCardModel().mainIndex);
			}
			
			Collections.sort(list);
			int cnt = 0;
			
			for(int j=0; j<list.size(); j++){
				if(j > 0){
					if(list.get(j-1) == list.get(j)){
						cnt++;
					}else{
						cnt = 1;
					}
				}else{
					cnt = 1;
				}
				
				if(cnt == 4){
					results.add(list.get(j));
				}
			}
		}
		
		for(int i=0; i<mSelectedList.size(); i++){
			for(int j=0; j<mSelectedList.get(i).size(); j++){
				CardView cv = mSelectedList.get(i).get(j);
				if(results.contains(cv.getCardModel().mainIndex)){
					cv.setSelectedVisible(true);
				}
			}
		}
		
		if(mGameListener != null) {
			mGameListener.onGameResult(results);
		}
	}
	
	
	// 리스트 매칭 체크
	private boolean checkGameResult(){
		if(mHoldList.size() == 0){
			if(mDownList.size() == 0){
				if(checkBaseListEmpty()){
					endGame();
					return true;
				}else{ 
					// 나가리인지 체크: BaseCard 끼리 맞는 짝이 있는지 체크해서 없으면 나가리
					if(checkBaseList()){
						return false;
					}else{ // 나가리
						if(mGameListener != null) {
							mGameListener.onGameAlert(getResources().getString(R.string.alert_nomatch_title), getResources().getString(R.string.alert_nomatch_contents));
						}
						return true;
					}
				}
			}else{
				if(checkDownListEdge()){ 
					return false;
				}else{
					if(checkBaseAndDownList()){ // 전체 매칭되는 패가 있는지 검사
						collapseDownList();
					}else{ // 나가리
						alignDownList();
						
						if(mGameListener != null) {
							mGameListener.onGameAlert(getResources().getString(R.string.alert_nomatch_title), getResources().getString(R.string.alert_nomatch_contents));
						}
					}
					
					return true;
				}
			}
		}else{ 
			return false;
		}
	}
	
	private boolean checkBaseAndDownList(){
		int len = mBaseList.size();
		for(int i=0; i<len; i++){
			int slen = mBaseList.get(i).size();
			
			if(slen > 0){
				CardView baseCard = mBaseList.get(i).get(slen-1);
				
				if(!baseCard.getFront()){
					return true; 
				}
				
				for(int j=0; j<mDownList.size(); j++){
					if(baseCard.getCardModel().mainIndex == mDownList.get(j).getCardModel().mainIndex){
						return true;
					}
				}
			}
		}
		
		if(checkDownList()) return true;
		if(checkBaseList()) return true;
		
		return false;
	}
	
	private boolean checkDownList(){
		int len = mDownList.size();
		if(len < 2) return false;
		
		int mIndex = mDownList.get(0).getCardModel().mainIndex;
		
		for(int i=1; i<len; i++){
			if(mIndex == mDownList.get(i).getCardModel().mainIndex){
				return true;
			}
			
			if(i>1){
				if(mDownList.get(i-1).getCardModel().mainIndex == mDownList.get(i).getCardModel().mainIndex){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean checkDownListEdge(){
		int downSize = mDownList.size();
		if(downSize > 1 && mDownList.get(0).getCardModel().mainIndex == mDownList.get(downSize-1).getCardModel().mainIndex){
			return true;
		}
		
		int len = mBaseList.size();
		for(int i=0; i<len; i++){
			int slen = mBaseList.get(i).size();
			
			if(slen > 0){
				CardView baseCard = mBaseList.get(i).get(slen-1);
				
				if(baseCard.getCardModel().mainIndex == mDownList.get(0).getCardModel().mainIndex){
					return true;
				}
				if(baseCard.getCardModel().mainIndex == mDownList.get(downSize-1).getCardModel().mainIndex){
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean checkBaseList(){
		CardView cv;
		ArrayList<CardView> list;
		
		int len = mBaseList.size();
		for(int i=0; i<len; i++){
			list = mBaseList.get(i);
			
			if(list.size() > 0){
				cv = list.get(list.size()-1);
				
				for(int j=0; j<len; j++){
					list = mBaseList.get(j);
					
					if(i != j && list.size() > 0){
						if(cv.getCardModel().mainIndex == list.get(list.size()-1).getCardModel().mainIndex){
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	private boolean checkBaseListEmpty(){
		boolean empty = true;
		for(int i=0; i<mBaseList.size(); i++)
			if(mBaseList.get(i).size() > 0) empty = false;
		return empty;
	}
	
	
	// 이벤트
	private void setAllCardEvent(boolean addEvent){
		setHoldCardEvent(addEvent);
		setBaseCardEvent(addEvent);
		setDownCardEvent(addEvent);
	}
	
	private void setHoldCardEvent(boolean addEvent){
		int len = mHoldList.size();
		for(int i=0; i<len; i++){
			CardView cv = mHoldList.get(i);
			
			if(addEvent && i == len-1){
				cv.setOnClickListener(this);
				if(cv.getFront()) {
					cv.setOnTouchListener(this); // 들고있는 제일 윗 패가 오픈되어 있는 경우에만 드래그 가능
				}else{
					cv.setOnTouchListener(null);
				}
			}else{
				cv.setOnClickListener(null);
				cv.setOnTouchListener(null);
			}
		}
	}
	
	private void setBaseCardEvent(boolean addEvent){
		int len = mBaseList.size();
		for(int i=0; i<len; i++){
			int slen = mBaseList.get(i).size();
			
			for(int j=0; j<slen; j++){
				CardView cv = mBaseList.get(i).get(j);
				
				if(addEvent && j == slen-1){
					cv.setOnClickListener(this);
					cv.setOnTouchListener(null);
				}else{
					cv.setOnClickListener(null);
					cv.setOnTouchListener(null);
				}
			}
		}
	}
	
	private void setDownCardEvent(boolean addEvent){
		int len = mDownList.size();
		for(int i=0; i<len; i++){
			CardView cv = mDownList.get(i);
			
			if(i == 0 || i == len-1){
				if(addEvent){
					cv.setOnClickListener(this);
				}else{
					cv.setOnClickListener(null);
				}
			}else{
				cv.setOnClickListener(null);
			}
			
			cv.setOnTouchListener(null);
		}
	}
	
	
	// 유틸
	private boolean removeCard(CardView view){
		// 깔린 패
		for(int i=0; i<mBaseList.size(); i++){
			if(removeCardView(mBaseList.get(i), view)) return true;
		}
		
		// 잡힌 패
		if(removeCardView(mHoldList, view)) return true;
		
		// 내려놓은 패
		if(mDownList.indexOf(view) == 0)	mDownShift = true;
		if(removeCardView(mDownList, view)) return true;
		
		return false;
	}
	
	private boolean removeCardView(ArrayList<CardView> list, CardView cv){
		if(list.remove(cv)) return true;
		return false;
	}
	
	private void clearTempList(){
		for(int i=0; i<mTempList.size(); i++){
			CardView cv = mTempList.get(i);
			cv.setSelectedVisible(false);
		}
		
		mTempList.clear();
	}
	
	private void addEffect(){
		if(mEffect == null){
			mEffect = new EffectView(mContext);
			mEffect.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			mCard.addView(mEffect);
		}
		
		mCard.bringChildToFront(mEffect);
		mCard.invalidate();
	}
	
	private void startEffect(){
		if(mEffectW == 0){
			float temp = mStageH*0.3f;
			float scale = temp/mEffect.getHeight();
			
			mEffect.setScaleX(scale);
			mEffect.setScaleY(scale);
			
			mEffectW = (int)(mEffect.getWidth()*scale);
			mEffectH= (int)(mEffect.getHeight()*scale);
		}
		
		int effX = (int)(mTargetPoint.x + (mCardW - mEffectW)*0.5);
		int effY = (int)(mTargetPoint.y + (mCardH - mEffectH)*0.5);
		
		mEffect.setX(effX);
		mEffect.setY(effY);
		mEffect.setOnEffectListener(this);
		mEffect.startEffect();
	}
	
	private void cardBringToFront(CardView cv){
		mCard.bringChildToFront(cv);
		mCard.invalidate();
	}
	
	private void addArrow(){
		if(!GamePreference.getInstance(mContext).get("dragview", false)){
			GamePreference.getInstance(mContext).put("dragview", true);
			mDrag.setVisibility(View.VISIBLE);
			
			mArrow.setVisibility(View.VISIBLE);
			
			ObjectAnimator aniY = ObjectAnimator.ofFloat(mArrow, "y", mArrowTargetY);
			aniY.setRepeatCount(Animation.INFINITE);
			aniY.setRepeatMode(Animation.REVERSE);
			
			mAniSet = new AnimatorSet();
			mAniSet.setInterpolator(new AccelerateInterpolator());
			mAniSet.setDuration(300);
			mAniSet.play(aniY);
			mAniSet.start();
		}
	}
	
	private void removeArrow(){
		if(mAniSet != null) if(mAniSet.isRunning()){
			mAniSet.cancel();
		}
		
		mArrow.setY(mArrowHomeY);
		mArrow.setVisibility(View.GONE);
		mDrag.setVisibility(View.GONE);
	}
	
	
	// 사운드
	private void initSound(){
		if(!GamePreference.getInstance(mContext).get(GamePreference.MUTE_EFFECT, false)){
			if(mPool == null){
				mPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
//				mPool.setOnLoadCompleteListener(this);
				
//				mSoundStart = mPool.load(mContext, R.raw.start, 1);
				mSoundClick = mPool.load(mContext, R.raw.click, 1);
				mSoundReverse = mPool.load(mContext, R.raw.reverse, 1);
				mSoundDingdong = mPool.load(mContext, R.raw.dingdong, 1);
				mSoundDown = mPool.load(mContext, R.raw.down, 1);
			}
		}
	}
	
	private void soundPlay(int soundID){
		if(!GamePreference.getInstance(mContext).get(GamePreference.MUTE_EFFECT, false)){
			initSound();
			mPool.play(soundID, 0.5f, 0.5f, 0, 0, 1);
		}
	}
	
	public void startBgm(){
		if(mPlayer == null){
			mPlayer = MediaPlayer.create(mContext, R.raw.bgm);
			mPlayer.setVolume(0.1f, 0.1f);
			mPlayer.setLooping(true);
		}
		
		mPlayer.start();
	}
	
	public void stopBgm(){
		if(mPlayer != null) if(mPlayer.isPlaying()) mPlayer.pause();
	}
	
	@Override
	public void onEffectEnd() {
		mEffect.setOnEffectListener(null);
		addSelectedList();
	}
	
	@Override
	public void onAnimationStart(Animator animation) {
		CardView cv;
		
		switch(mAniState){
			case ANI_INIT: // 뿌려지는 제일 윗장 카드를 리스트 상에서 삭제
				soundPlay(mSoundDown);
				
				cv = mHoldList.remove(mHoldList.size()-1);
				cardBringToFront(cv);
				break;
			case ANI_COLLAPSE:
				soundPlay(mSoundReverse);
				
				cv = mDownList.get(mDownList.size()-mAniStartCount-1);
				cv.reverseAnimation(false);
				cardBringToFront(cv);
				break;
			case ANI_DOWN:
				break;
		}
		
		if(mAniStartCount == mAniLen-1) return;
		mAniStartCount++;
	}
	
	@Override
	public void onAnimationEnd(Animator animation) {
		switch(mAniState){
			case ANI_SHOW:
				initCard();
				break;
			case ANI_INIT:
				if(mAniEndCount > mAniLen-mCol-1){ // 마지막 뒤집히는 카드인 경우
					soundPlay(mSoundReverse);
					
					ArrayList<CardView> baseList = mBaseList.get(mAniEndCount-(mAniLen-mCol));
					CardView cv = baseList.get(baseList.size()-1);
					cv.reverseAnimation(true);
				}
				
				if(mAniEndCount == mAniLen-1) {
					showHand();
					setAllCardEvent(true);
				}
				
				break;
			case ANI_MATCH:
				soundPlay(mSoundDingdong);
				startEffect();
				break;
			case ANI_SELECTED: 
				if(mAniEndCount == mAniLen-1){ // 선택된 카드 두 장 선택영역으로 이동 완료 후 이벤트
					if(!checkGameResult()){
						setAllCardEvent(true);
						if(mDownShift) setPositionDownList();
					}
				}
				break;
			case ANI_DOWN:
				if(!checkGameResult()){
					setAllCardEvent(true);
				}
				break;
			case ANI_COLLAPSE:
				if(mAniEndCount == mAniLen-1){
					restoreHoldList();
				}
				break;
			case ANI_HOLD:
				if(mAniEndCount == mAniLen-1){
					setAllCardEvent(true);
				}
				break;
			case ANI_RESULT:
				showResult();
				break;
		}
		
		if(mAniEndCount == mAniLen-1) return;
		mAniEndCount++;
	}
	
	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		float y = (float) animation.getAnimatedValue();
		
		int len = mSelectedList.size();
		for(int i=0; i<len; i++){
			ArrayList<CardView> list = mSelectedList.get(i);
			int slen = list.size();
			for(int j=0; j<slen; j++){
				CardView cv = list.get(j);
				if(y < cv.endY){
					cv.setY(y);
				}else{
					if(!cv.getFront()){
						soundPlay(mSoundReverse);
						cv.setY(cv.endY);
						cv.reverseAnimation(true);
					}
				}
			}
		}
	}
	
	
	///////////////////////////////////////////////////////////
	private OnGameListener mGameListener;
	public void setOnGameListener(OnGameListener listener){
		mGameListener = listener;
	}
	public interface OnGameListener{
		public void onGameStart();
		public void onGameEnd();
		public void onGameAlert(String title, String message);
		public void onGameResult(ArrayList<Integer> result);
		public void onShowResult();
	}
	
	@Override
	public void onAnimationRepeat(Animator animation) {}
	@Override
	public void onAnimationCancel(Animator animation) {}

}
