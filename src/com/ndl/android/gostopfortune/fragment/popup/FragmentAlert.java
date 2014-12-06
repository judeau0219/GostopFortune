package com.ndl.android.gostopfortune.fragment.popup;

import com.judeau.util.*;
import com.ndl.android.gostopfortune.MainActivity;
import com.ndl.android.gostopfortune.R;
import com.ndl.android.gostopfortune.datamodel.Constants;
import com.ndl.android.gostopfortune.manager.FontManager;

import android.os.Bundle;
import android.support.v4.app.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentAlert extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_alert, container, false);
		return view;
	}
	
	private MainActivity mActivity;
	
	private TextView mTextTitle;
	private TextView mTextMessage;
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		mActivity = (MainActivity) getActivity();
		
		mTextTitle = (TextView) getView().findViewById(R.id.txt_title);
		mTextMessage = (TextView) getView().findViewById(R.id.txt_message);
		
		mTextTitle.setTypeface(FontManager.getTypeFaceNanum(getActivity()));
		mTextMessage.setTypeface(FontManager.getTypeFaceNanum(getActivity()));
		
		TextUtil.setTextSizeByDensity(getActivity(), mTextTitle, 22);
		TextUtil.setTextSizeByDensity(getActivity(), mTextMessage, 14);
		
		setText();
	}
	
	private void setText(){
		mTextTitle.setText(mActivity.alertTitle);
		mTextMessage.setText(mActivity.alertMessage);
	}

}
