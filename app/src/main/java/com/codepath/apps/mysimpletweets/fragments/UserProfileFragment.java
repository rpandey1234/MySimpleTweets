package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TweetsAdapter;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment for profile information of user
 */
public class UserProfileFragment extends Fragment {

    TwitterClient client;
    User user;

    @Bind(R.id.ivProfileImage) ImageView ivProfileImage;
    @Bind(R.id.tvName) TextView tvName;
    @Bind(R.id.tvTagline) TextView tvTagline;
    @Bind(R.id.followers) TextView tvFollowers;
    @Bind(R.id.following) TextView tvFollowing;

    public static UserProfileFragment newInstance(String screenName) {
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(TwitterClient.SCREEN_NAME, screenName);
        userProfileFragment.setArguments(args);
        return userProfileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        String screenName = getArguments().getString(TwitterClient.SCREEN_NAME);
        View view = inflater.inflate(R.layout.fragment_profile_info, container, false);
        ButterKnife.bind(this, view);
        client.getUserInfo(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJson(response);
                populateProfileHeader(user);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseUsers) {
                try {
                    user = User.fromJson(responseUsers.getJSONObject(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                populateProfileHeader(user);
            }
        });

        return view;
    }

    private void populateProfileHeader(User user) {
        Glide.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
        tvName.setText(user.getName());
        tvTagline.setText(user.getTagline());

        String followerFormat = getString(R.string.follower_format);
        tvFollowers.setText(String.format(followerFormat, user.getNumFollowers()));
        String followingFormat = getString(R.string.following_format);
        tvFollowing.setText(String.format(followingFormat, user.getNumFollowing()));

        // set action bar title
        String handle = getString(R.string.handleTemplate);
        getActivity().setTitle(String.format(handle, user.getScreenName()));

    }
}
