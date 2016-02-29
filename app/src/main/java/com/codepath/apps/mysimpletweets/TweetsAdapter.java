package com.codepath.apps.mysimpletweets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter for the recycler view which holds tweets
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private final TwitterClient client;
    private List<Tweet> tweets;
    private Context context;

    public TweetsAdapter(Context context, List<Tweet> tweets, TwitterClient client) {
        this.tweets = tweets;
        this.context = context;
        this.client = client;
    }

    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(tweetView);
    }

    public void composeMessage(String replyAt) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        ComposeDialog composeDialog = ComposeDialog.newInstance(replyAt);

        composeDialog.show(fragmentManager, "fragment_compose");
    }

    @Override
    public void onBindViewHolder(final TweetsAdapter.ViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);
        holder.tvUserName.setText(tweet.getUser().getName());
        String handle = context.getResources().getString(R.string.handleTemplate);
        holder.tvHandle.setText(String.format(handle, tweet.getUser().getScreenName()));
        holder.tvTimestamp.setText(Tweet.getRelativeTimeAgo(tweet.getCreatedAt()));
        holder.tvLikes.setText(Long.toString(tweet.getLikes()));
        holder.tvRetweets.setText(Long.toString(tweet.getRetweets()));
        holder.tvBody.setText(tweet.getBody());

        colorLeftIcon(holder.tvRetweets, tweet.haveRetweeted() ? Color.RED : Color.LTGRAY);
        colorLeftIcon(holder.tvLikes, tweet.haveLiked() ? Color.RED: Color.LTGRAY);

        holder.tvReply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                composeMessage(tweet.getUser().getScreenName());
            }
        });

        // TODO: indicate retweets/likes when loading feed
        holder.tvRetweets.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                client.postRetweet(tweet.getUid(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        colorLeftIcon(holder.tvRetweets, Color.RED);
                        tweet.setHaveRetweeted(true);
                        holder.tvRetweets.setText(Long.toString(tweet.getRetweets() + 1));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse) {
                        if (errorResponse != null) {
                            Log.d("DEBUG", errorResponse.toString());
                        }
                        Toast.makeText(context, "Failed to retweet", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.tvLikes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                client.postLike(tweet.getUid(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // TODO: add animation
                        colorLeftIcon(holder.tvLikes, Color.RED);
                        tweet.setHaveLiked(true);
                        holder.tvLikes.setText(Long.toString(tweet.getLikes() + 1));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                            Throwable throwable, JSONObject errorResponse) {
                        if (errorResponse != null) {
                            Log.d("DEBUG", errorResponse.toString());
                        }
                        Toast.makeText(context, "Failed to like", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.ivProfileImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(TwitterClient.SCREEN_NAME, tweet.getUser().getScreenName());
                context.startActivity(intent);
            }
        });

        holder.ivProfileImage.setImageResource(android.R.color.transparent);
        Glide.with(context).load(tweet.getUser().getProfileImageUrl()).into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ivProfileImage) ImageView ivProfileImage;
        @Bind(R.id.tvUserName) TextView tvUserName;
        @Bind(R.id.tvBody) TextView tvBody;
        @Bind(R.id.tvTimestamp) TextView tvTimestamp;
        @Bind(R.id.tvHandle) TextView tvHandle;
        @Bind(R.id.tvReply) TextView tvReply;
        @Bind(R.id.tvRetweets) TextView tvRetweets;
        @Bind(R.id.tvLikes) TextView tvLikes;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Drawable[] compoundDrawables = tvReply.getCompoundDrawables();
            Drawable leftDrawable = compoundDrawables[0];
            leftDrawable.setColorFilter(Color.LTGRAY, Mode.SRC_IN);
        }
    }

    public void colorLeftIcon(TextView textView, int color) {
        Drawable[] compoundDrawables = textView.getCompoundDrawables();
        Drawable leftDrawable = compoundDrawables[0];
        leftDrawable.setColorFilter(color, Mode.SRC_IN);
    }
}
