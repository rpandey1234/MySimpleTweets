package com.codepath.apps.mysimpletweets.fragments;

/**
 * Fragment for mentions timeline
 */
public class MentionsTimelineFragment extends TweetsListFragment {

    @Override
    public void populateTweetList(boolean getNewest) {
        client.getMentionsTimeline(getJsonResponseHandler(getNewest));
    }
}
