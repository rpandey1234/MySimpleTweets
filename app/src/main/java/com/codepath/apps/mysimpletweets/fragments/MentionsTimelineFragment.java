package com.codepath.apps.mysimpletweets.fragments;

import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Fragment for mentions timeline
 */
public class MentionsTimelineFragment extends TweetsListFragment {

    @Override
    public void populateTweetList(boolean getNewest) {
        JsonHttpResponseHandler handler = getJsonResponseHandler(getNewest);
        if (getNewest) {
            client.getMentionsTimeline(handler);
        } else {
            client.getMentionsTimeline(oldestId, handler);
        }
    }
}
