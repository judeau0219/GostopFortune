package com.ndl.android.gostopfortune.fragment.popup;

import com.judeau.util.*;
import com.ndl.android.gostopfortune.R;
import com.ndl.android.gostopfortune.datamodel.GamePreference;
import com.ndl.android.gostopfortune.manager.FontManager;

import android.os.Bundle;
import android.support.v4.app.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class FragmentMenu extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_menu, container, false);
		return view;
	}
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		CheckBox checkBoxEffect = (CheckBox) getView().findViewById(R.id.check_effect);
		checkBoxEffect.setChecked(GamePreference.getInstance(getActivity()).get(GamePreference.MUTE_EFFECT, false));
		checkBoxEffect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				GamePreference.getInstance(getActivity()).put(GamePreference.MUTE_EFFECT, isChecked);
			}
		});
		
		CheckBox checkBoxBgm = (CheckBox) getView().findViewById(R.id.check_bgm);
		checkBoxBgm.setChecked(GamePreference.getInstance(getActivity()).get(GamePreference.MUTE_BGM, false));
		checkBoxBgm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				GamePreference.getInstance(getActivity()).put(GamePreference.MUTE_BGM, isChecked);
			}
		});
		
		TextView txtEffect = (TextView) getView().findViewById(R.id.txt_effect);
		TextView txtBgm = (TextView) getView().findViewById(R.id.txt_bgm);
		txtEffect.setTypeface(FontManager.getTypeFaceNanum(getActivity()));
		txtBgm.setTypeface(FontManager.getTypeFaceNanum(getActivity()));
		
		TextUtil.setTextSizeByDensity(getActivity(), txtEffect, 16);
		TextUtil.setTextSizeByDensity(getActivity(), txtBgm, 16);
	}


}
