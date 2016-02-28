package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codepath.apps.mysimpletweets.fragments.UserProfileFragment;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;

public class ProfileActivity extends AppCompatActivity {

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
}
