package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.TweetsListFragment.LoadingListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity implements LoadingListener {

    @Bind(R.id.viewpager) ViewPager viewPager;
    @Bind(R.id.tabs) PagerSlidingTabStrip tabStrip;
    private MenuItem miActionProgressItem;
    public HomeTimelineFragment homeFragment;
    public MentionsTimelineFragment mentionsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        if (!isNetworkAvailable()) {
            Toast.makeText(this,
                    "Sorry, looks like internet is not available. Internet is required for this app to function properly.",
                    Toast.LENGTH_LONG).show();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setLogo(R.drawable.twitter_logo);
            supportActionBar.setDisplayUseLogoEnabled(true);
        }

        // set the viewpager for the adpater
        viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        // attach the tabstrip to the viewpager
        tabStrip.setViewPager(viewPager);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem composeItem = menu.findItem(R.id.miCompose);
        Drawable icon = composeItem.getIcon();
        icon.setColorFilter(getResources().getColor(R.color.white), Mode.SRC_IN);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                composeMessage();
                return true;
            case R.id.miProfile:
                profileView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void profileView() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void composeMessage() {
        composeMessage("");
    }

    public void composeMessage(String replyAt) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ComposeDialog composeDialog = ComposeDialog.newInstance(replyAt);

        composeDialog.show(fragmentManager, "fragment_compose");
    }

    @Override
    public void startLoading() {
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(true);
        }
    }

    @Override
    public void endLoading() {
        if (miActionProgressItem != null) {
            miActionProgressItem.setVisible(false);
        }
    }

    // return the order of the fragments in the view pager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else {
                return new MentionsTimelineFragment();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            switch (position) {
                case 0:
                    homeFragment = (HomeTimelineFragment) createdFragment;
                    break;
                case 1:
                    mentionsFragment = (MentionsTimelineFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
