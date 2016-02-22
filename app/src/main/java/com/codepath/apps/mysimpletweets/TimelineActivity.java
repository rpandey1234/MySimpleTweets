package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.ComposeDialog.TweetListener;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity implements TweetListener {

    private static final boolean SHOULD_MAKE_REQUEST = true;
    private static final String RESPONSE_FILE = "response.json";
    private TwitterClient client;
    @Bind(R.id.rvTweets) RecyclerView rvTweets;
    @Bind(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    private ArrayList<Tweet> tweets;
    private TweetsAdapter aTweets;
    private Long newestId;
    private Long oldestId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        if (!isNetworkAvailable()) {
            Toast.makeText(this,
                    "Sorry, looks like internet is not available. Internet is required for this app to function properly.",
                    Toast.LENGTH_LONG).show();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setLogo(R.drawable.twitter_logo);
            supportActionBar.setDisplayUseLogoEnabled(true);
        }

        newestId = 1L;
        oldestId = null;
        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(this, tweets);
        rvTweets.setAdapter(aTweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(false);
            }
        });
        client = TwitterApplication.getRestClient();
        populateTimeline(true, true);
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweets.clear();
                aTweets.notifyDataSetChanged();
                populateTimeline(true);
            }
        });
        // populate tweets from local sqlite db until twitter API call returns
        List<Tweet> localTweets = new Select().from(Tweet.class).limit(25).execute();
        System.out.println("Local tweets from SQLite!!  " + localTweets.size());
        tweets.addAll(localTweets);
        aTweets.notifyDataSetChanged();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void populateTimeline(final boolean getNewest, final boolean firstTime) {
        // Check if we have response file already
        if (!SHOULD_MAKE_REQUEST && fileExists(getApplicationContext(), RESPONSE_FILE)) {
            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader(openFileInput(RESPONSE_FILE)));
                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = input.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                String text = buffer.toString();
                JSONArray response = new JSONArray(text);
                tweets.addAll(Tweet.fromJson(response));
                Log.d("DEBUG", "Found tweets: " + tweets.size());
                aTweets.notifyDataSetChanged();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
//                    Log.d("DEBUG", response.toString());
                    int previousSize = aTweets.getItemCount();
                    List<Tweet> apiTweets = Tweet.fromJson(response);
                    if (firstTime) {
                        // clear the items from sql lite db
                        tweets.clear();
                        aTweets.notifyDataSetChanged();
                    }
                    if (getNewest) {
                        tweets.addAll(0, apiTweets);
                        aTweets.notifyItemRangeInserted(0, apiTweets.size());
                    } else {
                        tweets.addAll(apiTweets);
                        aTweets.notifyItemRangeInserted(previousSize, apiTweets.size());
                    }

                    Log.d("DEBUG", "Found tweets: " + tweets.size());
                    if (!tweets.isEmpty()) {
                        newestId = tweets.get(0).getUid();
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
                }
                @Override
                public void onFailure(int statusCode, Header[] headers,
                        Throwable throwable, JSONObject errorResponse) {
                    if (errorResponse != null) {
                        Log.d("DEBUG", errorResponse.toString());
                    }
                }
            };
            if (getNewest) {
                client.getTimeline(handler);
            } else {
                client.getTimeline(null, oldestId, handler);
            }

        }
        swipeContainer.setRefreshing(false);
    }

    private void populateTimeline(final boolean getNewest) {
        populateTimeline(getNewest, false);
    }

    private boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        return file != null && file.exists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem composeItem = menu.findItem(R.id.miCompose);
        Drawable icon = composeItem.getIcon();
        icon.setColorFilter(getResources().getColor(R.color.white), Mode.SRC_IN);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                composeMessage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void composeMessage() {
        composeMessage("");
    }

    public void composeMessage(String replyAt) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ComposeDialog composeDialog = ComposeDialog.newInstance(replyAt);

        composeDialog.show(fragmentManager, "fragment_compose");
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
                Toast.makeText(getApplicationContext(), "Tweet published!", Toast.LENGTH_LONG).show();
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
