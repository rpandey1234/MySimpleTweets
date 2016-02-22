package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

@Table(name = "Tweets")
public class Tweet extends Model {
    // Define database columns and associated fields
    @Column(name = "created_at") String created_at;
    @Column(name = "body") String body;
    @Column(name = "uid") long uid; // unique id for the tweet
    @Column(name = "retweets") long retweets;
    @Column(name = "likes") long likes;
    @Column(name = "User") User user;

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getBody() {
        return body;
    }

    public User getUser() {
        return user;
    }

    public long getRetweets() {
        return retweets;
    }

    public long getLikes() {
        return likes;
    }

    // Make sure to always define this constructor with no arguments
    public Tweet() {
        super();
    }

    // Add a constructor that creates an object from the JSON response
    public static Tweet fromJson(JSONObject object) {
        Tweet tweet = new Tweet();
        try {
            tweet.created_at = object.getString("created_at");
            tweet.body = object.getString("text");
            tweet.uid = object.getLong("id");
            tweet.retweets = object.getLong("retweet_count");
            tweet.likes = object.getLong("favorite_count");
            tweet.user = User.fromJson(object.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tweet.save();
        return tweet;
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = fromJson(tweetJson);
            tweets.add(tweet);
        }

        return tweets;
    }

    // Taken from https://github.com/franklinho/twitter-client-android/
    // blob/7fe6abba01700a44ab9a46be29c54ad9ea400a72/app/src/main/java/com/codepath/apps/
    // mysimpletweets/models/Status.java
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        try {
            DateTime createdDateTime = new DateTime(sf.parse(rawJsonDate).getTime());
            DateTime currentDateTime = new DateTime();

            int secondDifference = Seconds.secondsBetween(createdDateTime.toLocalDateTime(),
                    currentDateTime.toLocalDateTime()).getSeconds();
            if (secondDifference < 60) {
                relativeDate = Integer.toString(secondDifference) + "s";
            } else if (Minutes.minutesBetween(createdDateTime.toLocalDateTime(),
                    currentDateTime.toLocalDateTime()).getMinutes() < 60) {
                relativeDate = Integer.toString(Minutes.minutesBetween(createdDateTime.toLocalDateTime(), currentDateTime.toLocalDateTime()).getMinutes())+"m";
            } else if (Hours.hoursBetween(createdDateTime.toLocalDateTime(),
                    currentDateTime.toLocalDateTime()).getHours() < 24) {
                relativeDate = Integer.toString(Hours.hoursBetween(createdDateTime.toLocalDateTime(),currentDateTime.toLocalDateTime()).getHours())+"h";
            } else if (Months.monthsBetween(createdDateTime.toLocalDateTime(),
                    currentDateTime.toLocalDateTime()).getMonths() < 1) {
                relativeDate = Integer.toString(Weeks.weeksBetween(
                        createdDateTime.toLocalDateTime(), currentDateTime.toLocalDateTime()).getWeeks()) + "w";
            } else {
                relativeDate = Integer.toString(Months.monthsBetween(createdDateTime.toLocalDateTime(), currentDateTime.toLocalDateTime()).getMonths())+"M";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }
}