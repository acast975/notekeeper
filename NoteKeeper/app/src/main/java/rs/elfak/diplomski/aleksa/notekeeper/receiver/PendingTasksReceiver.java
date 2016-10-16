package rs.elfak.diplomski.aleksa.notekeeper.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.List;
import java.util.Random;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.Session;
import rs.elfak.diplomski.aleksa.notekeeper.activity.MainActivity;
import rs.elfak.diplomski.aleksa.notekeeper.activity.NoteActivity;
import rs.elfak.diplomski.aleksa.notekeeper.http.HttpConnector;
import rs.elfak.diplomski.aleksa.notekeeper.http.NoteVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.http.TaskVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.model.Note;
import rs.elfak.diplomski.aleksa.notekeeper.model.Task;
import rs.elfak.diplomski.aleksa.notekeeper.type.NoteKeeperConstants;

/**
 * Created by aleks on 1.10.2016..
 */

public class PendingTasksReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        HttpConnector.getUpcomingTasks((new Session(context)).getId(), context, new TaskVolleyCallback() {
            @Override
            public void success(List<Task> taskList) {
                for(final Task task : taskList){
                    HttpConnector.getNote(task.getNoteId(), context, new NoteVolleyCallback() {
                        @Override
                        public void success(List<Note> noteList) {
                            sendNotification(context, "You should do task " + task.getText() + " within an hour!", noteList.get(0).getId(), noteList.get(0).getAssignedTo());
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

            @Override
            public void success(int id) {

            }

            @Override
            public void error(String message) {

            }
        });
    }

    private void sendNotification(Context context, String message, int noteId, int assigned) {
        int viewType;
        if(assigned == (new Session(context).getId())) {
            viewType = NoteKeeperConstants.NOTE_RECEIVED;
        } else {
            viewType = NoteKeeperConstants.NOTE_STANDARD;
        }
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra("note_id", noteId);
        intent.putExtra("view_type", viewType);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_name)
                .setContentTitle("NoteKeeper")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m, notificationBuilder.build());
    }
}
