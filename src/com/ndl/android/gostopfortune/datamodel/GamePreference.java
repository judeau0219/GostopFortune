package com.ndl.android.gostopfortune.datamodel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class GamePreference {

	private final String PREF_NAME = "com.ndl.android.gostopfortune.pref";
	
	public final static String MUTE_EFFECT = "mute_effect";
	public final static String MUTE_BGM = "mute_bgm";
	
	private static GamePreference mInstance;
	
	private SharedPreferences mPref;
	private SharedPreferences.Editor mEditor;
	
	public GamePreference(Context context){
		mPref = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		mEditor = mPref.edit();
	}
	
	public synchronized static GamePreference getInstance(Context context){
		if( mInstance != null ) return mInstance;
		
		mInstance = new GamePreference(context);
		return mInstance;
	}
	
	public void put(String key, String value){
		mEditor.putString(key, value);
		mEditor.commit();
	}
	
	public void put(String key, boolean value){
		if(key.equals(MUTE_BGM)) if(mMuteListener != null) mMuteListener.onMute(value);
		
		mEditor.putBoolean(key, value);
		mEditor.commit();
	}
	
	public void put(String key, int value){
		mEditor.putInt(key, value);
		mEditor.commit();
	}
	
	public String get(String key, String defValue){
		try{
			return mPref.getString(key, defValue);
		}catch(Exception e){
			return defValue;
		}
	}
	
	public int get(String key, int defValue){
		try{
			return mPref.getInt(key, defValue);
		}catch(Exception e){
			return defValue;
		}
	}
	
	public boolean get(String key, boolean defValue){
		try{
			return mPref.getBoolean(key, defValue);
		}catch(Exception e){
			return defValue;
		}
	}
	
	private OnMuteListener mMuteListener;
	public void setOnMuteListener( OnMuteListener listener )
	{
		this.mMuteListener = listener;
	}
	public interface OnMuteListener
	{
		void onMute(boolean mute);
	}

}
