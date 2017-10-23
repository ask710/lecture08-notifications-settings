package edu.uw.notifysettingdemo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int NOTIFY_INTENT_ID = 1;
    public static final int NOTIFICATION_ID = 3;
    private int notifyCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        notifyCount = prefs.getInt("count", 0); //0 is the default
    }

    @Override
    protected void onStop() {
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("count", notifyCount);
        editor.commit();

        super.onStop();
    }

    //to handle the launch button
    public void launchActivity(View v){
        startActivity(new Intent(this, SecondActivity.class)); //quick launch
    }

    //to handle the notification button
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification(View v) {
        Log.d(TAG, "Notify button pressed");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("pref_notify", false)) {

            notifyCount++;

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.ic_notificarion);
            builder.setContentTitle("Notification");
            builder.setContentText("You are on notice! This is #" + notifyCount);
            builder.setChannel("default_channel"); //for Oreo devices
            builder.setAutoCancel(true); //dismiss on click
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            builder.setVibrate(new long[]{0, 250, 250, 250});

            Intent resultIntent = new Intent(this, SecondActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(SecondActivity.class);
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(NOTIFY_INTENT_ID, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);

            Notification note = builder.build();
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(NOTIFICATION_ID, note); // can update later
        }else {
        //notifications turned off!
        Toast.makeText(this, "This notice has been generated "+notifyCount+" times", Toast.LENGTH_LONG).show();
        }
    }

    //to handle the alert button
    public void showAlert(View v){
        Log.d(TAG, "Alert button pressed");
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Alert!");
//        builder.setMessage("Danger Will Robinson");
//        builder.setPositiveButton("I see it!", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(MainActivity.this, "Clicked okay", Toast.LENGTH_LONG).show();
//            }
//        });
//        builder.setNegativeButton("AHHHH", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(MainActivity.this, "Clicked AHHH", Toast.LENGTH_LONG).show();
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
        AlertDialogFragment.newInstance().show(getSupportFragmentManager(), null);
    }

    public static class AlertDialogFragment extends android.support.v4.app.DialogFragment {

        public static AlertDialogFragment newInstance() {

            Bundle args = new Bundle();
            AlertDialogFragment fragment = new AlertDialogFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Alert!")
                    .setMessage("Danger Will Robinson!"); //note chaining
            builder.setPositiveButton("I see it!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.v(TAG, "You clicked okay! Good times :)");
                }
            });
            builder.setNegativeButton("Noooo...", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.v(TAG, "You clicked cancel! Sad times :(");
                }
            });

            AlertDialog dialog = builder.create();
            return dialog;
        }
    }

    /* Menu handling */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_settings:
                Log.d(TAG, "Settings menu pressed");
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
