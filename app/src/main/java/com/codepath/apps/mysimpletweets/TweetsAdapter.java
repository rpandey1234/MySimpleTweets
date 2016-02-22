package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.models.Tweet;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter for the recycler view which holds tweets
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private List<Tweet> tweets;
    private Context context;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.tweets = tweets;
        this.context = context;
    }

    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(tweetView);
    }

    @Override
    public void onBindViewHolder(TweetsAdapter.ViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);
        holder.tvUserName.setText(tweet.getUser().getName());
        String handle = context.getResources().getString(R.string.handleTemplate);
        holder.tvHandle.setText(String.format(handle, tweet.getUser().getScreenName()));
        holder.tvTimestamp.setText(Tweet.getRelativeTimeAgo(tweet.getCreatedAt()));
        holder.tvLikes.setText(Long.toString(tweet.getLikes()));
        holder.tvRetweets.setText(Long.toString(tweet.getRetweets()));
        holder.tvBody.setText(tweet.getBody());

        holder.tvReply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof TimelineActivity) {
                    ((TimelineActivity) context).composeMessage(tweet.getUser().getScreenName());
                }
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
            TextView views[] = new TextView[]{tvRetweets, tvReply, tvLikes};
            for (TextView textview : views) {
                Drawable[] compoundDrawables = textview.getCompoundDrawables();
                Drawable leftDrawable = compoundDrawables[0];
                leftDrawable.setColorFilter(Color.LTGRAY, Mode.SRC_IN);
            }
        }
    }
}
