package cullera.pere.tuitalloud;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by perecullera on 4/9/15.
 */
public class SpeakingDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Stop Speaking?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        TimelineActivity activity = (TimelineActivity )getActivity();
                        activity.stopSpeech();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public static DialogFragment newInstance(Integer message) {
        SpeakingDialogFragment frag = new SpeakingDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", message);
        frag.setArguments(args);
        return frag;
    }
}
