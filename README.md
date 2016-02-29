# Simple Tweet Viewer (with Fragments)

- Android Codepath
- Assignment 4
- Due Sunday, February 28, 2016

## Usage

This application uses OAuth 1.0 to sign the user into their Twitter account, see their home feed, and either compose or reply to a tweet. The application is built with fragments. 

Time spent: 12 hours

Completed user stories:

* Required User Stories
	* User can sign in to Twitter using OAuth login (2 points)
	* User can view the tweets from their home timeline
		* User should be displayed the username, name, and body for each tweet (2 points)
		* User should be displayed the relative timestamp for each tweet "8m", "7h" (1 point)
		* User can view more tweets as they scroll with infinite pagination (1 point)
	* User can compose a new tweet
		* User can click a “Compose” icon in the Action Bar on the top right (1 point)
		* User can then enter a new tweet and post this to twitter (2 points)
		* User is taken back to home timeline with new tweet visible in timeline (1 point)
	* User can switch between Timeline and Mention views using tabs. (4 points)
	    * User can view their home timeline tweets.
        * User can view the recent mentions of their username.
    * User can navigate to view their own profile (3 points)
        * User can see picture, tagline, # of followers, # of following, and tweets on their profile.
    * User can click on the profile image in any tweet to see another user's profile. (2 points)
        * User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
        * Profile view should include that user's timeline
    * User can infinitely paginate any of these timelines (home, mentions, user) by scrolling to the bottom (1 point)

* Advanced User Stories
    * Advanced: Robust error handling, check if internet is available, network failures (1 point)
    * Advanced: While composing a tweet, user can see a character counter with characters remaining for tweet out of 140 (1 point)
    * Advanced: Links in tweets are clickable and will launch the web browser (see autolink) (1 point)
    * Advanced: User can refresh tweets timeline by pulling down to refresh (i.e pull-to-refresh) (1 point)
    * Advanced: Tweets are persisted into sqlite
    * Advanced: User can select "reply" to respond to a tweet (1 point)
    * Advanced: Improve the user interface and theme the app to feel "twitter branded" (1 to 5 points)
    * Advanced: User can take favorite (and unfavorite) or retweet actions on a tweet (1 point)
    * Advanced: When a network request is sent, user sees an indeterminate progress indicator (1 point)

* Bonus User Stories
    * Hide action bar when scrolling through feeds.
    * Bonus: Ability to log out.
    * Bonus: Compose activity is replaced with a modal overlay (2 points)
    * Bonus: Apply the popular Butterknife annotation library to reduce view boilerplate. (1 point)
    * Bonus: Leverage RecyclerView as a replacement for the ListView and ArrayAdapter for all lists of tweets. (2 points)
    * Bonus: Replace Picasso with Glide for more efficient image rendering. (1 point)

Walkthrough of user stories:

![Video Walkthrough](simple_twitter_walkthrough2.gif)

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Libraries used

 * [scribe-java](https://github.com/fernandezpablo85/scribe-java) - Simple OAuth library for handling the authentication flow.
 * [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
 * [codepath-oauth](https://github.com/thecodepath/android-oauth-handler) - Custom-built library for managing OAuth authentication and signing of requests
 * [Glide](https://github.com/bumptech/glide) - Used for async image loading and caching them in memory and on disk.
 * [ActiveAndroid](https://github.com/pardom/ActiveAndroid) - Simple ORM for persisting a local SQLite database on the Android device
 * [Butterknife](http://jakewharton.github.io/butterknife/) - Removes boilerplate code by binding view IDs to objects
 * [Joda Time Android](https://github.com/dlew/joda-time-android) - Date formatting and handling libarary


## License

Copyright 2016 Rahul Pandey

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.