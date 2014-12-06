package com.ndl.android.gostopfortune.adapter;

import java.util.*;

import android.content.*;
import android.view.*;
import android.widget.*;

import com.judeau.util.*;
import com.ndl.android.gostopfortune.*;
import com.ndl.android.gostopfortune.datamodel.*;
import com.ndl.android.gostopfortune.manager.*;
import com.ndl.android.gostopfortune.task.*;
import com.ndl.android.gostopfortune.view.*;

public class ResultAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Integer> mResults;
	
	public ResultAdapter(Context context, ArrayList<Integer> results) {
		mContext = context;
		mResults = results;
	}

	@Override
	public int getCount() {
		return mResults.size();
	}

	@Override
	public Object getItem(int position) {
		return mResults.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mResults.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.result_list_item, parent, false);
		}
		
		ImageView imgResult = (ImageView) ViewHolder.get(convertView, R.id.img_result);
		DashPath dashPath = (DashPath) ViewHolder.get(convertView, R.id.dash_path);
		
		TextView tv1 = (TextView) ViewHolder.get(convertView, R.id.txt_result1);
		TextView tv2 = (TextView) ViewHolder.get(convertView, R.id.txt_result2);
		
		tv1.setTypeface(FontManager.getTypeFaceNanum(mContext));
		tv2.setTypeface(FontManager.getTypeFaceNanum(mContext));
		
		TextUtil.setTextSizeByDensity(mContext, tv1, 13);
		TextUtil.setTextSizeByDensity(mContext, tv2, 13);
		
		if(position == mResults.size() - 1){
			dashPath.setVisibility(View.GONE);
		}else{
			dashPath.setVisibility(View.VISIBLE);
		}
		
		int index = mResults.get(position)-1;
		
		imgResult.setImageResource(Constants.RESOURCE[index]);
		/*
		BitmapWorkerTask task = new BitmapWorkerTask(mContext, imgResult);
	    task.execute(Constants.RESOURCE[index]);
		*/
		String title  = mContext.getResources().getStringArray(R.array.result_title)[index];
		String explain = mContext.getResources().getStringArray(R.array.result_explain)[index];
		
		tv1.setText(title);
		tv2.setText(explain);
		
		return convertView;
	}
	
}
