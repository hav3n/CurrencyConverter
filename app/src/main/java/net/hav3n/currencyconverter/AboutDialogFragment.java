package net.hav3n.currencyconverter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import net.hav3n.currencyconverter.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Nikhil Peter Raj
 */
public class AboutDialogFragment extends DialogFragment {

    private static final String TAG = "AboutDialogFragment";

    public static AboutDialogFragment newInstance() {

        return new AboutDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String lastUpdated = "NA";
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        long timestamp = prefs.getLong(MainActivity.KEY_TIMESTAMP, -1);
        Log.i(TAG, "Got timestamp: " + timestamp);
        if (timestamp != -1) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(timestamp * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date currentTime = calendar.getTime();
            Log.i(TAG, "Got current Date: " + currentTime.toString());
            lastUpdated = sdf.format(currentTime);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("About")
                .setIcon(R.drawable.ic_action_action_info_outline_dark)
                .setMessage(String.format(getString(R.string.message_about), lastUpdated))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }
                ).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }
}
