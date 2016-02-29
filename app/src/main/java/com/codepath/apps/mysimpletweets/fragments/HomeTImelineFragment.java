package com.codepath.apps.mysimpletweets.fragments;

import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Fragment for twitter home feed
 */
public class HomeTimelineFragment extends TweetsListFragment {

    @Override
    public void populateTweetList(boolean getNewest) {
        JsonHttpResponseHandler handler = getJsonResponseHandler(getNewest);
        if (getNewest) {
            client.getTimeline(handler);
        } else {
            client.getTimeline(null, oldestId, handler);
        }
    }
}
