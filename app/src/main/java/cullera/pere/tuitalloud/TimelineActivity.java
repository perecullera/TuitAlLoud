package cullera.pere.tuitalloud;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.FixedTweetTimeline;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetUi;

import java.util.List;

import io.fabric.sdk.android.Fabric;

public class TimelineActivity extends ListActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "d9A58jFxpQURROph3St6g";
    private static final String TWITTER_SECRET = "tEaAKgazoS6ADKOFUSnHYPKRAXKiBSGAPnO2FS2szE";

    public static Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        c = getBaseContext();

        setContentView(R.layout.activity_timeline);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new TweetUi());

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        twitterApiClient.getStatusesService().homeTimeline(null, null, null, null,null,null, null, new Callback <List<Tweet>>() {

            @Override
            public void success(Result<List<Tweet>> listResult) {
                final List<Tweet> tweets = listResult.data;
                final FixedTweetTimeline userTimeline = new FixedTweetTimeline.Builder()
                        .setTweets(tweets)
                        .build();
                final CustomTweetTimelineListAdapter adapter = new CustomTweetTimelineListAdapter(TimelineActivity.this, userTimeline);
                setListAdapter(adapter);
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
    }
    public static void showTweetActivity(long tweetId){
        Intent intent = new Intent(c ,TweetActivity.class);
        intent.putExtra("tweetID",tweetId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

class CustomTweetTimelineListAdapter extends TweetTimelineListAdapter {

    public CustomTweetTimelineListAdapter(Context context, Timeline<Tweet> timeline) {
        super(context, timeline);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        //disable subviews
        if(view instanceof ViewGroup){
            disableViewAndSubViews((ViewGroup) view);
        }

        //enable root view and attach custom listener
        view.setEnabled(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetId = "click tweetId:"+getItemId(position);
                Toast.makeText(context, tweetId, Toast.LENGTH_SHORT).show();
                TimelineActivity.showTweetActivity(getItemId(position));
            }
        });
        return view;
    }

    private void disableViewAndSubViews(ViewGroup layout) {
        layout.setEnabled(false);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                disableViewAndSubViews((ViewGroup) child);
            } else {
                child.setEnabled(false);
                child.setClickable(false);
                child.setLongClickable(false);
            }
        }
    }


}



