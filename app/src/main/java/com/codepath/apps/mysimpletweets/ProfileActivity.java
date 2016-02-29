package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.codepath.apps.mysimpletweets.fragments.TweetsListFragment.LoadingListener;
import com.codepath.apps.mysimpletweets.fragments.UserProfileFragment;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;

public class ProfileActivity extends AppCompatActivity implements LoadingListener {

    private MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String screenName = getIntent().getStringExtra(TwitterClient.SCREEN_NAME);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            UserTimelineFragment userTweetsFragment = UserTimelineFragment.newInstance(screenName);
            ft.replace(R.id.flContainer, userTweetsFragment);

            UserProfileFragment userProfileFragment = UserProfileFragment.newInstance(screenName);
            ft.replace(R.id.flProfileContainer, userProfileFragment);

            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        return super.onPrepareOptionsMenu(menu);
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
}
