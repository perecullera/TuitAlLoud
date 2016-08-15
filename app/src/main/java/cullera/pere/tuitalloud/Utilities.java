package cullera.pere.tuitalloud;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by perecullera on 4/9/15.
 */
public class Utilities {
    public String processTuit(Tweet tweet){
        String result = "";
        String [] words = tweet.text.split(" ");
        List<String> list =  new ArrayList<String>();
        Collections.addAll(list, words);
        list = findLinks(list);
        list = findHT(list);
        for (String s : list)
        {
            result += s + " ";
        }
        return result;
    }

    private List<String> findHT(List<String> list) {
        for (String word : list){
            if (word.startsWith("#")){
                word = "Hashtag " + word.substring(1);
            }
        }
        return list;
    }

    private List<String> findLinks(List<String> words) {
        ArrayList toRemove = new ArrayList();
        for (String word : words){
            if (word.startsWith("http")){
                toRemove.add(word);
            }
        }
        words.removeAll(toRemove);
        return words;
    }
}
