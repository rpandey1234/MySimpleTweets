package com.codepath.apps.mysimpletweets.fragments;

import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.ComposeDialog.TweetListener;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Fragment for twitter home feed
 */
public class HomeTimelineFragment extends TweetsListFragment implements TweetListener {

    @Override
    public void populateTweetList(boolean getNewest) {
        JsonHttpResponseHandler handler = getJsonResponseHandler(getNewest);
        if (getNewest) {
            client.getTimeline(handler);
        } else {
            client.getTimeline(null, oldestId, handler);
        }
    }

    @Override
    public void setTweet(String body) {
        // Make API request
        client.postTweet(body, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // Inject the tweet into the tweets adapter so it is immediately visible
                Tweet tweet = Tweet.fromJson(response);
                tweets.add(0, tweet);
                aTweets.notifyItemInserted(0);
                rvTweets.scrollToPosition(0);
                Toast.makeText(getContext(), "Tweet published!", Toast.LENGTH_LONG).show();
                Log.d("DEBUG", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
