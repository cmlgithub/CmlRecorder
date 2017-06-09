package com.cml.cmlrecorder;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private RecFileFragment recFileFragment;
    private FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        if(mToolBar != null)
            setSupportActionBar(mToolBar);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mContainer = (FrameLayout) findViewById(R.id.container);
        MainAdapter mMainAdapter = new MainAdapter(getSupportFragmentManager());
        RecorderFragment recorderFragment = RecorderFragment.newsInstance();
        recFileFragment = RecFileFragment.newsInstance();

        recorderFragment.setUpdateFile(new BaseFragment.UpdateFile() {
            @Override
            public void updateFile() {
                recFileFragment.updateData();
            }
        });

        mMainAdapter.addFragment(recorderFragment);
        mMainAdapter.addFragment(recFileFragment);

        mViewPager.setAdapter(mMainAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.main_menu:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class MainAdapter extends FragmentStatePagerAdapter{

        private List<Fragment> mFragments = new ArrayList<>();
        private List<String> mTitles = new ArrayList<>();

        public MainAdapter(FragmentManager fm) {
            super(fm);
            mTitles.add("Recorder");
            mTitles.add("RecFile");
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
