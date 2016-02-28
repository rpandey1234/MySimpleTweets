package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    @Bind(R.id.ivProfileImage) ImageView ivProfileImage;
    @Bind(R.id.tvName) TextView tvName;
    @Bind(R.id.tvTagline) TextView tvTagline;
    @Bind(R.id.followers) TextView tvFollowers;
    @Bind(R.id.following) TextView tvFollowing;

    TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // TODO: top portion should be separate fragment
        String screenName = getIntent().getStringExtra(TwitterClient.SCREEN_NAME);
        client = TwitterApplication.getRestClient();

        client.getUserInfo(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJson(response);
                String handle = getString(R.string.handleTemplate);
                getSupportActionBar().setTitle(String.format(handle, user.getScreenName()));
                populateProfileHeader(user);
            }
        });

        if (savedInstanceState == null) {
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
            // display user fragment within this activity dynamically
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }
    }

    private void populateProfileHeader(User user) {
        Glide.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
        tvName.setText(user.getName());
        tvTagline.setText(user.getTagline());

        String followerFormat = getString(R.string.follower_format);
        tvFollowers.setText(String.format(followerFormat, user.getNumFollowers()));
        String followingFormat = getString(R.string.following_format);
        tvFollowing.setText(String.format(followingFormat, user.getNumFollowing()));
    }

}
