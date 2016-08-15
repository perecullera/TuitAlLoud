package cullera.pere.tuitalloud;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.FixedTweetTimeline;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class TimelineActivity extends ListActivity implements TextToSpeech.OnInitListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "d9A58jFxpQURROph3St6g";
    private static final String TWITTER_SECRET = "tEaAKgazoS6ADKOFUSnHYPKRAXKiBSGAPnO2FS2szE";

    public static Context c;

    CustomTweetTimelineListAdapter adapter;
    static List<Tweet> tweets;
    private TextToSpeech engine;

    Utilities ut = new Utilities();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        c = getBaseContext();

        setContentView(R.layout.activity_timeline);


        engine = new TextToSpeech(this, this);



        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);

        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Twitter(authConfig))
                .debuggable(true) // <----- set to true to see more logs
                .build();
        Fabric.with(fabric);
        //Fabric.with(this, new TwitterCore(authConfig), new TweetUi());

        getTweets();

        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                adapter.refresh(new Callback<TimelineResult<Tweet>>() {
                    @Override
                    public void success(Result<TimelineResult<Tweet>> result) {
                        swipeLayout.setRefreshing(false);
                        getTweets();
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        // Toast or some other action
                    }
                });
            }
        });

    }

    public void getTweets(){
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        twitterApiClient.getStatusesService().homeTimeline(null, null, null, null,null,null, null, new Callback <List<Tweet>>() {

            @Override
            public void success(Result<List<Tweet>> listResult) {
                tweets = listResult.data;
                final FixedTweetTimeline userTimeline = new FixedTweetTimeline.Builder()
                        .setTweets(tweets)
                        .build();
                adapter = new CustomTweetTimelineListAdapter(TimelineActivity.this, userTimeline);
                setListAdapter(adapter);
            }

            @Override
            public void failure(TwitterException e) {
                Log.d("Twitter","twitter " + e );
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:


            case R.id.action_read:
                speech();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //Log.d('Speech', 'Success!');
            Locale spanish = new Locale("es", "ES");
            engine.setLanguage(spanish);
        }

    }
    private void speech() {

        engine.speak("Hola Mundo", TextToSpeech.QUEUE_FLUSH, null);

        Tweet tweet;
        //getTweets();

        new SpeakingDialogFragment();
        showDialog();

       /* AlertDialog show = new AlertDialog.Builder(c)
                .setTitle("Speaking")
                .setMessage("Stop speaking?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        //para();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();*/
        for (int i = 0; i< TimelineActivity.tweets.size(); i++){
            tweet = TimelineActivity.tweets.get(i);


            engine.speak("Usuario"+ tweet.user.name + "dice", TextToSpeech.QUEUE_ADD, null);
            String processedTw = ut.processTuit(tweet);
            engine.speak(processedTw, TextToSpeech.QUEUE_ADD, null);
            while(engine.isSpeaking()) {
                Log.d("Speaking","Speaking");
            }
        }

    }
    public void stopSpeech(){
        if(engine !=null){
            engine.stop();
            engine.shutdown();
        }
    }
    void showDialog() {
        DialogFragment newFragment = SpeakingDialogFragment.newInstance(
                1);
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void doPositiveClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Positive click!");
    }

    public void doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
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



