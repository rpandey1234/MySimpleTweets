package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;

import com.codepath.apps.mysimpletweets.TwitterClient;

/**
 * Fragment for user's list of tweets
 */
public class UserTimelineFragment extends TweetsListFragment {

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString(TwitterClient.SCREEN_NAME, screenName);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    @Override
    public void populateTweetList(boolean getNewest) {
        String screenName = getArguments().getString(TwitterClient.SCREEN_NAME);
        client.getUserTimeline(screenName, getJsonResponseHandler(getNewest));
    }
}
