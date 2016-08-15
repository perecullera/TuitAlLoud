package cullera.pere.tuitalloud;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.Locale;

/**
 * Created by perecullera on 2/9/15.
 */
public class ReadTuit implements TextToSpeech.OnInitListener {

    private static Locale local = null;
    private int result=0;
    TextToSpeech tts;
    Context c;


    public ReadTuit(Context con, TextToSpeech.OnInitListener OnIn){
        tts = new TextToSpeech(con, OnIn);
        local = Locale.UK;
        tts.setLanguage(local);
        c = con;

        //tts.speak("Text to say aloud", TextToSpeech.QUEUE_ADD, null);
    }


    public void onIn() {

        //set Language
        result = tts.setLanguage(local);
        // tts.setPitch(5); // set pitch level
        // tts.setSpeechRate(2); // set speech speed rate
        if (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.d("", "Language not supported");
        } else {

            speakOut();
        }


    }

    private void speakOut() {
        Tweet tweet;

        if(result!=tts.setLanguage(local))
        {
            Toast.makeText(null, "Hi Please enter the right Words......  ", Toast.LENGTH_LONG).show();
        }else
        {
            AlertDialog show = new AlertDialog.Builder(c)
                .setTitle("Speaking")
                .setMessage("Stop speaking?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        para();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
            for (int i = 0; i< TimelineActivity.tweets.size(); i++){
                tweet = TimelineActivity.tweets.get(i);

                tts.speak(tweet.text, TextToSpeech.QUEUE_FLUSH, null);
                while(tts.isSpeaking()) {
                    Log.d("", "speaking");

                }
            }

        }

    }

    public void para(){
        if (tts.isSpeaking()){
            tts.stop();
        }else {

        }

    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.speak("Hello World", TextToSpeech.QUEUE_FLUSH, null);

        }
    }
}
