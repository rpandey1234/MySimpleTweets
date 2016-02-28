package com.codepath.apps.mysimpletweets.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TweetsAdapter;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
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
    private static final boolean SHOULD_MAKE_REQUEST = true;
    private static final String RESPONSE_FILE = "response.json";

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
        // test: add delay here
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        ButterKnife.bind(this, view);
        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(getContext(), tweets);
        rvTweets.setAdapter(aTweets);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvTweets.setLayoutManager(layoutManager);
        // populate tweets from local sqlite db until twitter API call returns
//        List<Tweet> localTweets = new Select().from(Tweet.class).limit(25).execute();
//        System.out.println("Local tweets from SQLite!!  " + localTweets.size());
//        tweets.addAll(localTweets);
//        aTweets.notifyDataSetChanged();
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
        // Check if we have response file already
//        if (!SHOULD_MAKE_REQUEST && fileExists(getContext(), RESPONSE_FILE)) {
//            BufferedReader input = null;
//            try {
//                input = new BufferedReader(new InputStreamReader(openFileInput(RESPONSE_FILE)));
//                String line;
//                StringBuffer buffer = new StringBuffer();
//                while ((line = input.readLine()) != null) {
//                    buffer.append(line + "\n");
//                }
//                String text = buffer.toString();
//                JSONArray response = new JSONArray(text);
//                tweets.addAll(Tweet.fromJson(response));
//                Log.d("DEBUG", "Found tweets: " + tweets.size());
//                aTweets.notifyDataSetChanged();
//            } catch (IOException | JSONException e) {
//                e.printStackTrace();
//            }
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
//                    Log.d("DEBUG", response.toString());
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
//                    try {
//                        // write response json to file which can be used for future debugging
//                        FileOutputStream fos = openFileOutput(RESPONSE_FILE, MODE_WORLD_WRITEABLE);
//                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
//                        writer.write(response.toString());
//                        writer.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                if (swipeContainer != null) {
                    swipeContainer.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    Log.d("DEBUG", errorResponse.toString());
                }
                Toast.makeText(getContext(), "Failed to get Tweets", Toast.LENGTH_SHORT).show();
            }
        };
    }


    private boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        return file != null && file.exists();
    }
}
