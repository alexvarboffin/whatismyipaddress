//package com.walhalla.whatismyipaddress.ui.fragment;
//
//import android.content.res.TypedArray;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.databinding.DataBindingUtil;
//import androidx.fragment.app.Fragment;
//
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.google.android.material.tabs.TabLayout;
//import com.google.android.material.tabs.TabLayoutMediator;
//import com.walhalla.ui.DLog;
//import com.walhalla.whatismyipaddress.Helpers0;
//import com.walhalla.whatismyipaddress.R;
//import com.walhalla.whatismyipaddress.ui.activities.Main.ViewPagerAdapter;
//import com.walhalla.whatismyipaddress.databinding.FragmentTabHolder2Binding;
//import com.walhalla.whatismyipaddress.ui.fragment.home.HomeFragment;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TabHolder2Fragment extends Fragment {
//
//
//    private FragmentTabHolder2Binding mBinding;
//    private ViewPagerAdapter mPagerAdapter;
//
//    private int mSelected;
////    private final ViewPager.OnPageChangeListener mmmm = new ViewPager.OnPageChangeListener() {
////        /**
////         * OnPageChangeListener
////         */
////        @Override
////        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
////
////        }
////
////        @Override
////        public void onPageSelected(int position) {
////            invalidateFragmentMenus(position);
////            mSelected = position;
////        }
////
////        @Override
////        public void onPageScrollStateChanged(int state) {
////
////        }
////    };
//
//
//    public TabHolder2Fragment() {
//        // Required empty public constructor
//    }
//
//    public static TabHolder2Fragment newInstance(String param1, String param2) {
//        return new TabHolder2Fragment();
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab_holder_2, container, false);
//        return mBinding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        List<String> titles = new ArrayList<>();
//        titles.add("My IP");
//        titles.add("Tools");
//        titles.add("Web");
//
//        titles.add("2");
//
//        TabLayout tabLayout = getActivity().findViewById(R.id.tabs);
//        //tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mBinding.viewpager));
//        //mBinding.viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//
//                mSelected = tab.getPosition();
//                invalidateFragmentMenus(mSelected); //api v2
//                //DLog.d("" + tab.getText() + " " + mSelected);
//
//                if (getActivity() != null) {
//                    Helpers0.hideKeyboardFrom(getActivity(),
//                            //getActivity().findViewById(R.id.et_user_input)
//                            getActivity().getWindow().getDecorView()
//                    );
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//
////        mPagerAdapter = new ViewPagerAdapter(this);//getChildFragmentManager(), 0
////
////        mPagerAdapter.addFragment(new Fragment1(), "Shell");//getString(R.string.tab_active)
////        mPagerAdapter.addFragment(new MiscellaneousFragment(), "Dev");
////        mPagerAdapter.addFragment(new HomeFragment(), "Dev");
////        mBinding.viewpager.setAdapter(mPagerAdapter);
//
//        //tabLayout.setupWithViewPager(mBinding.viewpager);
//        new TabLayoutMediator(tabLayout, mBinding.viewpager,
//                (tab, position) -> tab.setText(titles.get(position))).attach();
//
//
//        //@@@mBinding.viewpager.addOnPageChangeListener(mmmm);
//
//        //int[] icons = getResources().getIntArray(R.array.tab_icons);
//        TypedArray icons = getResources().obtainTypedArray(R.array.tab_icons);
//
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            tabLayout.getTabAt(i).setIcon(icons.getResourceId(i, -1));
//        }
//        icons.recycle();
//        mBinding.viewpager.setOffscreenPageLimit(
//                (tabLayout.getTabCount() > 0) ? tabLayout.getTabCount() : ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
//        );
//    }
//
//    private void invalidateFragmentMenus(int position) {
//        DLog.d("@invalidateFragmentMenus " + " " + position);
////        v1
////        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
////            mPagerAdapter.getItem(i).setHasOptionsMenu(i == position);
////        }
////        if (getActivity() != null) {
////            getActivity().invalidateOptionsMenu(); //or respectively its support method.
////        }
//        for (int i = 0; i < mPagerAdapter.getItemCount(); i++) {
//            //int item = mBinding.viewpager.getCurrentItem();
//            mPagerAdapter.getItem(i).setHasOptionsMenu(/*i == item && */i == position);
//            DLog.d("000000 " + i + " " + position);
//        }
//        if (getActivity() != null) {
//            getActivity().invalidateOptionsMenu(); //or respectively its support method.
//        }
//    }
//
//
//    public boolean onBackPressed() {
////        Fragment baseFragment = getChildFragmentManager().getFragments().get(mSelected);
//        return false;
//    }
//
//
////    public void onButtonPressed(Uri uri) {
////        if (mListener != null) {
////            mListener.onFragmentInteraction(uri);
////        }
////    }
////
////    @Override
////    public void onAttach(Context context) {
////        super.onAttach(context);
////        if (context instanceof OnFragmentInteractionListener) {
////            mListener = (OnFragmentInteractionListener) context;
////        } else {
////            throw new RuntimeException(context.toString()
////                    + " must implement OnFragmentInteractionListener");
////        }
////    }
////
////    @Override
////    public void onDetach() {
////        super.onDetach();
////        mListener = null;
////    }
//
////    @Override
////    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
////        super.onCreateOptionsMenu(menu, inflater);
////        invalidateFragmentMenus(mBinding.viewpager.getCurrentItem());
////    }
//}