package rs.elfak.diplomski.aleksa.notekeeper.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.List;
import java.util.Random;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.activity.MainActivity;
import rs.elfak.diplomski.aleksa.notekeeper.activity.NoteActivity;
import rs.elfak.diplomski.aleksa.notekeeper.http.HttpConnector;
import rs.elfak.diplomski.aleksa.notekeeper.http.NoteVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.http.TaskVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.localstorage.LocalStorage;
import rs.elfak.diplomski.aleksa.notekeeper.model.Note;
import rs.elfak.diplomski.aleksa.notekeeper.model.Task;
import rs.elfak.diplomski.aleksa.notekeeper.type.NoteKeeperConstants;
import rs.elfak.diplomski.aleksa.notekeeper.type.Types;

/**
 * Created by aleks on 29.9.2016..
 */

public class NoteKeeperGcmListenerService extends GcmListenerService {

    private static String TAG = "GcmListenerService";
    private static int notificationCounter = 0;

    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        super.onMessageReceived(s, bundle);

        String message = bundle.getString("message");
        Log.d(TAG, "From: " + s);
        Log.d(TAG, "Message: " + message);

        if(message != null) {
            if (message.equals("friendship")) {
                sendNotification(bundle.getString("friend_username") + " wants to be your friend.", NoteKeeperConstants.MAIN_VIEW_FRIENDS_PENDING);
            }

            else if (message.equals("friendship_accepted")) {
                sendNotification(bundle.getString("friend_username") + " accepted your friend request.", NoteKeeperConstants.MAIN_VIEW_FRIENDS);
            }

            else if (message.equals("share")) {
                String noteId = bundle.getString("note_id");
                sendNotification(bundle.getString("friend_username") + " shared a note named " + bundle.getString("note_title") + " with you", NoteKeeperConstants.MAIN_VIEW_SHARED);
            }

            else if (message.equals("share_stop")) {
                sendNotification(bundle.getString("friend_username") + " stopped sharing a note named " + bundle.getString("note_title") + " with you", NoteKeeperConstants.MAIN_VIEW_SHARED);
            }

            else if (message.equals("assignment")) {
                sendNotification(bundle.getString("username") + " wants to assign you a note", NoteKeeperConstants.MAIN_VIEW_PENDING);
            }

            else if (message.equals("note_accepted")) {
                sendNotification(bundle.getString("username") + " accepted " + bundle.getString("notename"), NoteKeeperConstants.MAIN_VIEW_ASSIGNED);
            }

            else if (message.equals("note_refused")) {
                sendNotification(bundle.getString("username") + " refused " + bundle.getString("notename"), NoteKeeperConstants.MAIN_VIEW_ASSIGNED);
            }

            else if (message.equals("receive_cancel")) {
                sendNotification(bundle.getString("username") + " canceled using " + bundle.getString("notename"), NoteKeeperConstants.MAIN_VIEW_ASSIGNED);
            }

            else if (message.equals("task_done")) {
                sendNotification(bundle.getString("username") + " has done task " + bundle.getString("text") + " in note " + bundle.getString("note_title"), NoteKeeperConstants.MAIN_VIEW_ASSIGNED);
            }

            else if (message.equals("task_changed")) {
                int id = Integer.parseInt(bundle.getString("id"));
                HttpConnector.getTask(id, this, new TaskVolleyCallback() {
                    @Override
                    public void success(List<Task> taskList) {
                        if(taskList.size() == 0) return;

                        Task task = taskList.get(0);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(LocalStorage.TASK_TEXT, task.getText());
                        contentValues.put(LocalStorage.TASK_DEADLINE, task.getDeadline() != null ? task.getDeadline().toString() : "");
                        contentValues.put(LocalStorage.TASK_CHECKED, task.isChecked() ? 1 : 0);

                        int count = getContentResolver().update(LocalStorage.CONTENT_TASK_URI, contentValues, LocalStorage.TASK_GLOBAL_ID + " = " + task.getId(), null);
                        Log.w("Updated: ", String.valueOf(count));
                    }

                    @Override
                    public void success(int id) {

                    }

                    @Override
                    public void error(String message) {

                    }
                });
            }

            else if (message.equals("note_updated")) {
                final int noteId = bundle.getInt("note_id");
                HttpConnector.getNote(noteId, this, new NoteVolleyCallback() {
                    @Override
                    public void success(List<Note> noteList) {
                        String title = noteList.get(0).getTitle();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(LocalStorage.NOTE_TITLE, title);

                        getContentResolver().update(LocalStorage.CONTENT_NOTE_URI, contentValues, LocalStorage.NOTE_GLOBAL_ID + " = " + noteId, null);
                    }

                    @Override
                    public void success(int insertId) {

                    }

                    @Override
                    public void error(String message) {

                    }
                });
            }
        }
    }

    private void sendNotification(String message, int view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("view", view);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_action_name)
                .setContentTitle("NoteKeeper")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m, notificationBuilder.build());
    }
}
