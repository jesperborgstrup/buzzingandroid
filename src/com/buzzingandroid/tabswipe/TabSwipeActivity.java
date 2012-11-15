package com.buzzingandroid.tabswipe;

import java.util.ArrayList;
import java.util.List;
  
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
  
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
  
public abstract class TabSwipeActivity extends SherlockFragmentActivity {
  
    private ViewPager mViewPager;
    private TabsAdapter adapter;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
         * Create the ViewPager and our custom adapter
         */
        mViewPager = new ViewPager(this);
        adapter = new TabsAdapter( this, mViewPager );
        mViewPager.setAdapter( adapter );
        mViewPager.setOnPageChangeListener( adapter );
         
        /*
         * We need to provide an ID for the ViewPager, otherwise we will get an exception like:
         * 
         * java.lang.IllegalArgumentException: No view found for id 0xffffffff for fragment TestFragment{40de5b90 #0 id=0xffffffff android:switcher:-1:0}
         * at android.support.v4.app.FragmentManagerImpl.moveToState(FragmentManager.java:864)
         * 
         * The ID 0x7F04FFF0 is large enough to probably never be used for anything else
         */
        mViewPager.setId( 0x7F04FFF0 );
  
        super.onCreate(savedInstanceState);
  
        /*
         * Set the ViewPager as the content view
         */
        setContentView(mViewPager);
    }
  
    /**
     * Add a tab with a backing Fragment to the action bar
     * @param titleRes A string resource pointing to the title for the tab
     * @param fragmentClass The class of the Fragment to instantiate for this tab
     * @param args An optional Bundle to pass along to the Fragment (may be null)
     */
    protected void addTab(int titleRes, Class<? extends Fragment> fragmentClass, Bundle args ) {
        adapter.addTab( getString( titleRes ), fragmentClass, args );
    }
    /**
     * Add a tab with a backing Fragment to the action bar
     * @param titleRes A string to be used as the title for the tab
     * @param fragmentClass The class of the Fragment to instantiate for this tab
     * @param args An optional Bundle to pass along to the Fragment (may be null)
     */
    protected void addTab(CharSequence title, Class<? extends Fragment> fragmentClass, Bundle args ) {
        adapter.addTab( title, fragmentClass, args );
    }
  
    private static class TabsAdapter extends FragmentPagerAdapter implements TabListener, ViewPager.OnPageChangeListener {
  
        private final SherlockFragmentActivity mActivity;
        private final ActionBar mActionBar;
        private final ViewPager mPager;
  
        /**
         * @param fm
         * @param fragments
         */
        public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            this.mActivity = activity;
            this.mActionBar = activity.getSupportActionBar();
            this.mPager = pager;
  
            mActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
        }
  
        private static class TabInfo {
            public final Class<? extends Fragment> fragmentClass;
            public final Bundle args;
            public TabInfo(Class<? extends Fragment> fragmentClass,
                    Bundle args) {
                this.fragmentClass = fragmentClass;
                this.args = args;
            }
        }
  
        private List<TabInfo> mTabs = new ArrayList<TabInfo>();
  
        public void addTab( CharSequence title, Class<? extends Fragment> fragmentClass, Bundle args ) {
            final TabInfo tabInfo = new TabInfo( fragmentClass, args );
  
            Tab tab = mActionBar.newTab();
            tab.setText( title );
            tab.setTabListener( this );
            tab.setTag( tabInfo );
  
            mTabs.add( tabInfo );
  
            mActionBar.addTab( tab );
            notifyDataSetChanged();
        }
  
        @Override
        public Fragment getItem(int position) {
            final TabInfo tabInfo = mTabs.get( position );
            return (Fragment) Fragment.instantiate( mActivity, tabInfo.fragmentClass.getName(), tabInfo.args );
        }
  
        @Override
        public int getCount() {
            return mTabs.size();
        }
  
        public void onPageScrollStateChanged(int arg0) {
        }
  
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
  
        public void onPageSelected(int position) {
        	/*
        	 * Select tab when user swiped
        	 */
            mActionBar.setSelectedNavigationItem( position );
        }
  
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
        	/*
        	 * Slide to selected fragment when user selected tab
        	 */
            TabInfo tabInfo = (TabInfo) tab.getTag();
            for ( int i = 0; i < mTabs.size(); i++ ) {
                if ( mTabs.get( i ) == tabInfo ) {
                    mPager.setCurrentItem( i );
                }
            }
        }
  
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }
  
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }
}