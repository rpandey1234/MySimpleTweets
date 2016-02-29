package com.codepath.apps.mysimpletweets.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.TweetsAdapter;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.utils.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment for list of tweets
 */
public abstract class TweetsListFragment extends Fragment {

    @Bind(R.id.rvTweets) RecyclerView rvTweets;
    @Bind(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    ArrayList<Tweet> tweets;
    TweetsAdapter aTweets;
    LinearLayoutManager layoutManager;
    Long oldestId;
    TwitterClient client;
    LoadingListener loadingListener;

    public interface LoadingListener {

        void startLoading();

        void endLoading();
    }

    public void sendTweet(String body) {
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

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof LoadingListener) {
            loadingListener = (LoadingListener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement LoadingListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oldestId = null;
        client = TwitterApplication.getRestClient();
        populateTweetList(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        ButterKnife.bind(this, view);
        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(getContext(), tweets, client);
        rvTweets.setAdapter(aTweets);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTweetList(false);
            }
        });

        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweets.clear();
                aTweets.notifyDataSetChanged();
                populateTweetList(true);
            }
        });

        return view;
    }

    public abstract void populateTweetList(boolean getNewest);

    public JsonHttpResponseHandler getJsonResponseHandler(final boolean getNewest) {
        loadingListener.startLoading();
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                int previousSize = aTweets.getItemCount();
                List<Tweet> apiTweets = Tweet.fromJson(response);
                if (getNewest) {
                    tweets.addAll(0, apiTweets);
                    aTweets.notifyItemRangeInserted(0, apiTweets.size());
                } else {
                    tweets.addAll(apiTweets);
                    aTweets.notifyItemRangeInserted(previousSize, apiTweets.size());
                }

                Log.d("DEBUG", "Found tweets: " + tweets.size());
                if (!tweets.isEmpty()) {
                    oldestId = tweets.get(tweets.size() - 1).getUid();
                }
                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }
                loadingListener.endLoading();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    Log.d("DEBUG", errorResponse.toString());
                }
                Toast.makeText(getContext(), "Failed to get Tweets", Toast.LENGTH_SHORT).show();
                loadingListener.endLoading();
            }
        };
    }
}
