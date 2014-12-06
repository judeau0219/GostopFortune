package com.ndl.android.gostopfortune.fragment.popup;

import android.os.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.*;
import android.widget.*;

import com.judeau.util.*;
import com.ndl.android.gostopfortune.*;
import com.ndl.android.gostopfortune.datamodel.*;
import com.ndl.android.gostopfortune.manager.*;

public class FragmentTutorial extends Fragment implements OnPageChangeListener {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
		return view;
	}
	
	private MainActivity mActivity;
	
	private ViewPager mViewPager;
	private PagerAdapter mPagerAdapter;

	private TextView mExplain;
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		mActivity = (MainActivity) getActivity();
		
		mExplain = (TextView) getView().findViewById(R.id.txt_explain);
		mExplain.setTypeface(FontManager.getTypeFaceNanum(mActivity));
		
		TextUtil.setTextSizeByDensity(mActivity, mExplain, 18);
		
		int screenH = UnitUtil.getScreenHeight(mActivity) - UnitUtil.dpiToPixel(mActivity, 55);
		
		int pH = UnitUtil.dpiToPixel(mActivity, 502);
		
		float pagerH = screenH * 0.82f;
		float scale = pagerH/pH;
		
		mViewPager = (ViewPager) getView().findViewById(R.id.pager);
		mViewPager.setPivotX(UnitUtil.dpiToPixel(mActivity, 162));
		mViewPager.setPivotY(UnitUtil.dpiToPixel(mActivity, 502));
		mViewPager.setScaleX(scale);
		mViewPager.setScaleY(scale);
		
		LinearLayout linearDot = (LinearLayout) getView().findViewById(R.id.linear_dot);
		linearDot.setY(screenH - pagerH + UnitUtil.dpiToPixel(mActivity, 20));
		
		float explainY = screenH * 0.08f;
		mExplain.setY(explainY);
		
		mPagerAdapter = new PagerAdapter(getChildFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
		
		onPageSelected(mViewPager.getCurrentItem());
	}
	
	public void onClick(View v){
		int id = v.getId();
		if(id == R.id.btn_close_tutorial){
			// finish();
		}
		/*
		else if(id == R.id.btn_next){
			if(mViewPager.getCurrentItem() < 2){
				mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
			}
		}
		else if(id == R.id.btn_prev){
			if(mViewPager.getCurrentItem() > 0){
				mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
			}
		}
		*/
	}

	// 페이지가 선택될 때 호출
	@Override
	public void onPageSelected(int position) {
		for(int i=0; i<3; i++){
			ImageView iv = (ImageView) getView().findViewById(R.id.img_dot1+i);
			if(i == position){
				iv.setImageResource(R.drawable.img_dot_on);
			}else{
				iv.setImageResource(R.drawable.img_dot_off);
			}
		}
		
		TextUtil.setTextViewColorPartial(mExplain, Constants.TUTORIAL_EXPLAIN[position][0], Constants.TUTORIAL_EXPLAIN[position][1], 0xffffd259);
	}
	
	// 스크롤 시 위치값 호출
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		// position : 현재 페이지 넘버
		// positionOffset : 스크롤링 변경값( 0~1 )
		// positionOffsetPixels : 스크롤링 변경값( 픽셀 )
	}
	
	// 스크롤 시 상태값 호출
	@Override
	public void onPageScrollStateChanged(int state) {
		// 0 : 최종정착
		// 1 : 드래그중
		// 2 : 최종위치에 정착하는 과정에 있음을 알린다.
	}
	
	private class PagerAdapter extends FragmentStatePagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return PageFragment.getInstance(position);
		}

		@Override
		public int getCount() {
			return 3;
		}
	}
	
	public static class PageFragment extends Fragment {

		private int mPage;

		public static PageFragment getInstance(int pageNumber) {
			PageFragment fragment = new PageFragment();
			
			Bundle args = new Bundle();
			args.putInt("page", pageNumber);
			fragment.setArguments(args);
			
			return fragment;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mPage = getArguments().getInt("page");
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_page, container, false);
			((ImageView) rootView.findViewById(R.id.img_page)).setImageResource(R.drawable.tutorial_page_01 + mPage);
			return rootView;
		}
	}

}