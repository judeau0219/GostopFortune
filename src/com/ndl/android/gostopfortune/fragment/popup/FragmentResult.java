package com.ndl.android.gostopfortune.fragment.popup;

import java.util.*;

import com.judeau.util.*;
import com.ndl.android.gostopfortune.MainActivity;
import com.ndl.android.gostopfortune.R;
import com.ndl.android.gostopfortune.adapter.ResultAdapter;
import com.ndl.android.gostopfortune.manager.FontManager;

import android.content.*;
import android.os.Bundle;
import android.support.v4.app.*;
import android.util.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentResult extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_result, container, false);
		return view;
	}
	
	private MainActivity mActivity;
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		mActivity = (MainActivity) getActivity();
		
		ListView list = (ListView) getView().findViewById(R.id.list_result);
		list.setOverScrollMode(View.OVER_SCROLL_NEVER);
		ResultAdapter adapter = new ResultAdapter(getActivity(), mActivity.mResults);
		list.setAdapter(adapter);
		
		String result1, result2;
		
		if(checkContainsList(R.array.result_group_01) || checkContainsList(R.array.result_group_02)){
			result1 = "눈빛 하나로 이성을 사로잡겠군요!";
			result2 = "애정운이 가득~ 맘에 드는 이성과\n피망 뉴맞고 한판 즐겨보세요!";
		}
		else if(checkContainsList(R.array.result_group_03) || checkContainsList(R.array.result_group_04)){
			result1 = "날아가는 새도 떨어뜨릴 운세!";
			result2 = "피망 뉴맞고 한판 하시면서\n당신의 행운을 시험해 보세요!";
		}
		else if(checkContainsList(R.array.result_group_05) || checkContainsList(R.array.result_group_06)){
			result1 = "지인들과의 즐겁고 행복한 시간!";
			result2 = "좋은 사람들과 맛난 음식과 술한잔 기울이시고\n피망 뉴맞고를 통해 친목도 다지세요!";
		}
		else if(checkContainsList(R.array.result_group_07) || checkContainsList(R.array.result_group_08)){
			result1 = "오늘은 조심~또 조심!";
			result2 = "근심, 걱정거리, 안 좋은 일들은 피망뉴맞고\n한판하시면서 훌훌 털어버리세요!";
		}
		else{
			result1 = "많은 일들로 가득한 우리네 인생사!";
			result2 = "모든 스트레스는 피망 뉴맞고 한판으로\n화끈하게 날려버리세요!";
		}
		
		TextView tv1 = (TextView) getView().findViewById(R.id.txt_result1);
		tv1.setTypeface(FontManager.getTypeFaceNanum(getActivity()));
		tv1.setText(result1);
		
		TextView tv2 = (TextView) getView().findViewById(R.id.txt_result2);
		tv2.setText(result2);
		
		TextUtil.setTextSizeByDensity(getActivity(), tv1, 16);
		TextUtil.setTextSizeByDensity(getActivity(), tv2, 14);
	}
	
	private boolean checkContainsList(int resId){
		int[] ary = getResources().getIntArray(resId);
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i=0; i<ary.length; i++) list.add(ary[i]);
		return mActivity.mResults.containsAll(list);
	}
	
}
