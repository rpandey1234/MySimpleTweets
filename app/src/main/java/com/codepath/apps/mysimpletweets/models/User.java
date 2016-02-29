package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

@Table(name = "Users")
public class User extends Model {
    @Column(name = "name") private String name;
    @Column(name = "uid") private long uid;
    @Column(name = "screenName") private String screenName;
    @Column(name = "profileImageUrl") private String profileImageUrl;
    @Column(name = "tagline") private String tagline;
    @Column(name = "numFollowers") private Long numFollowers;
    @Column(name = "numFollowing") private Long numFollowing;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getTagline() {
        return tagline;
    }

    public Long getNumFollowers() {
        return numFollowers;
    }

    public Long getNumFollowing() {
        return numFollowing;
    }

    public User() {
        super();
    }

    public static User fromJson(JSONObject jsonObject) {
        User user = new User();
        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.tagline = jsonObject.getString("description");
            user.numFollowers = jsonObject.getLong("followers_count");
            user.numFollowing = jsonObject.getLong("friends_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        user.save();
        return user;
    }
}
