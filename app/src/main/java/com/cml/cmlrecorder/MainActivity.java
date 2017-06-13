package com.cml.cmlrecorder;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.cml.cmlrecorder.fragment.RecFileFragment;
import com.cml.cmlrecorder.fragment.RecorderFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private RecFileFragment recFileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        if(mToolBar != null)
            setSupportActionBar(mToolBar);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        MainAdapter mMainAdapter = new MainAdapter(getSupportFragmentManager());
        RecorderFragment recorderFragment = RecorderFragment.newsInstance();
        recFileFragment = RecFileFragment.newsInstance();


        mMainAdapter.addFragment(recorderFragment);
        mMainAdapter.addFragment(recFileFragment);

        mViewPager.setAdapter(mMainAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }


    class MainAdapter extends FragmentStatePagerAdapter{

        private List<Fragment> mFragments = new ArrayList<>();
        private List<String> mTitles = new ArrayList<>();

        public MainAdapter(FragmentManager fm) {
            super(fm);
            mTitles.add(MainActivity.this.getResources().getString(R.string.recorder));
            mTitles.add(MainActivity.this.getResources().getString(R.string.recorderFile));
        }

        public void addFragment(Fragment fragment){
            mFragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }


}
