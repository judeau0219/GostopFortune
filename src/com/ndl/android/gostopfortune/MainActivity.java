package com.ndl.android.gostopfortune;

import java.util.*;

import android.content.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;

import com.judeau.util.*;
import com.ndl.android.gostopfortune.datamodel.*;
import com.ndl.android.gostopfortune.datamodel.GamePreference.OnMuteListener;
import com.ndl.android.gostopfortune.fragment.popup.*;
import com.ndl.android.gostopfortune.viewgroup.*;

public class MainActivity extends FragmentActivity implements OnMuteListener, GameStage.OnGameListener {

	public ArrayList<Integer> mResults = new ArrayList<Integer>();
	
	public String alertTitle;
	public String alertMessage;
	
	private GamePreference mGamePreference;
	
	private GameStage mGameStage;
	private ImageView mCover;
	
	private SoundPool mPool = null;
	
	private int mSoundSuccess;
	private int mSoundFail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mGamePreference = GamePreference.getInstance(this);
		mGamePreference.setOnMuteListener(this);
		
		mGameStage = (GameStage) findViewById(R.id.game_stage);
		mGameStage.setOnGameListener(this);
		
		mCover = (ImageView) findViewById(R.id.img_cover);
		mCover.setVisibility(View.GONE);
		
		// 키를 이용해 볼륨 조절 시 상세 볼륨 컨트롤 옵션을 미디어로 설정.. 
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);  
		
		initSound();
		initTutorial();
	}
	
	private void initTutorial(){
		if(!GamePreference.getInstance(this).get("tutorial", false)){
			GamePreference.getInstance(this).put("tutorial", true);
			// addTutorial();
			addTutorialPopup();
		}
	}
	
	private void callPmangGostop(){
		Intent intent;
		String packageName = getResources().getString(R.string.pmang_gostop_package);
		
		if( PackageUtil.isInstalledApplication(this, packageName) ){
			intent = this.getPackageManager().getLaunchIntentForPackage(packageName);
		}else{
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+packageName));
		}
		
		startActivity(intent);
	}
	
	public void onClick(View v){
		int id = v.getId();
		
		switch(id){
		case R.id.btn_banner:
			callPmangGostop();
			break;
		case R.id.btn_gostop:
			callPmangGostop();
			break;
		case R.id.btn_menu:
			addMenuPopup();
//			addAlertPopup("아쉽게 맞는 패가\n없습니다!", "더 이상 맞는 패가 없습니다.\n다시 진행해 주세요!");
			/*
			mResults.clear();
			mResults.add(1);
			mResults.add(2);
			mResults.add(3);
			mResults.add(4);
			mResults.add(5);
			mResults.add(6);
			mResults.add(7);
			mResults.add(8);
			mResults.add(9);
			mResults.add(10);
			mResults.add(11);
			mResults.add(12);
			addResultPopup();
			*/
			break;
		case R.id.btn_restart:
			removePopup();
			
			mResults.clear();
			mGameStage.startGame();
			break;
		case R.id.btn_close:
			removePopup();
			break;
		case R.id.btn_close_tutorial:
			removePopup();
			break;
		case R.id.btn_game_guide:
			// addTutorial();
			addTutorialPopup();
			break;
		}
	}
	
	private void addTutorial(){
		Intent intent = new Intent(this, TutorialActivity.class);
		startActivity(intent);
	}
	
	private void addMenuPopup(){
		mCover.setVisibility(View.VISIBLE);
		
		Fragment fragment = new FragmentMenu();
		
		FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
		tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		tr.replace(R.id.frag_popup, fragment);
		tr.commit();
	}
	
	private void addAlertPopup(String title, String message){
		soundPlay(mSoundFail);
		
		mCover.setVisibility(View.VISIBLE);
		
		alertTitle = title;
		alertMessage = message;
		
		Fragment alert = new FragmentAlert();
		
		FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
		tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		tr.replace(R.id.frag_popup, alert);
		tr.commit();
	}
	
	private void addResultPopup(){
		mCover.setVisibility(View.VISIBLE);
		
		Fragment fragment = new FragmentResult();
		
		FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
		tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		tr.replace(R.id.frag_popup, fragment);
		tr.commit();
	}
	
	private void addTutorialPopup(){
		Fragment fragment = new FragmentTutorial();
		
		FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
		tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		tr.replace(R.id.frag_popup, fragment);
		tr.commit();
	}
	
	private boolean removePopup(){
		mCover.setVisibility(View.GONE);
		
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frag_popup);
		
		if(fragment != null){
			FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
			tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			tr.remove(fragment);
			tr.commit();
			
			return true;
		}
		
		return false;
	}
	
	private void initSound(){
		if(!GamePreference.getInstance(this).get(GamePreference.MUTE_EFFECT, false)){
			if(mPool == null){
				mPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
				
				mSoundSuccess = mPool.load(this, R.raw.success, 1);
				mSoundFail = mPool.load(this, R.raw.fail, 1);
			}
		}
	}
	
	private void soundPlay(int soundID){
		if(!GamePreference.getInstance(this).get(GamePreference.MUTE_EFFECT, false)){
			initSound();
			mPool.play(soundID, 0.5f, 0.5f, 0, 0, 1);
		}
	}
	
	private void startBgm(){
		mGameStage.startBgm();
	}
	
	private void stopBgm(){
		mGameStage.stopBgm();
	}
	
	@Override
	public void onGameStart() {
		
	}
	@Override
	public void onGameEnd() {
		
	}
	@Override
	public void onGameAlert(String title, String message) {
		addAlertPopup(title, message);
	}
	@Override
	public void onGameResult(ArrayList<Integer> results) {
		mResults = results;
		
		if(mResults.size() > 0){
			callResultHandler.sendEmptyMessageDelayed(0, 600);
		}else{
			addAlertPopup(getResources().getString(R.string.alert_noresult_title), getResources().getString(R.string.alert_noresult_contents));
		}
	}
	
	@Override
	public void onShowResult() {
		if(mResults.size() > 0){
			addResultPopup();
		}else{
			addAlertPopup(getResources().getString(R.string.alert_noresult_title), getResources().getString(R.string.alert_noresult_contents));
		}
	}
	
	Handler callResultHandler = new Handler(){
		public void handleMessage(Message msg){
			soundPlay(mSoundSuccess);
			addResultPopup();
		}
	};
	
	@Override
	public void onMute(boolean mute) {
		if(mute){
			stopBgm();
		}else{
			startBgm();
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		if(!mGamePreference.get(GamePreference.MUTE_BGM, false))
			startBgm();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		stopBgm();
	}
	
	private int mBackCount = 0;
	private Toast mEndToast;
	
	@Override
	public void onBackPressed(){
		if(!removePopup()){
			mBackCount++;
			
			if(mBackCount == 2){
				mEndToast.cancel();
				finish();
			}else{
				mEndToast = Toast.makeText(this, R.string.alert_end, Toast.LENGTH_LONG);
				mEndToast.show();
				mHandler.sendEmptyMessageDelayed(0, 3000);
			}
		}
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			mBackCount = 0;
		}
	};
	/*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		
		switch(keyCode){
			case KeyEvent.KEYCODE_VOLUME_UP:
				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,  AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,  AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
				return true;
		}
		
		return false;
	}
	*/
}
