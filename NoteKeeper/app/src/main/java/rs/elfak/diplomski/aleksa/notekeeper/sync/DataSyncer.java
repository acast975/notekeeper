package rs.elfak.diplomski.aleksa.notekeeper.sync;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.util.List;

import rs.elfak.diplomski.aleksa.notekeeper.Session;
import rs.elfak.diplomski.aleksa.notekeeper.http.CommonVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.http.HttpConnector;
import rs.elfak.diplomski.aleksa.notekeeper.http.NoteVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.http.TaskVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.localstorage.LocalStorage;
import rs.elfak.diplomski.aleksa.notekeeper.model.DataMapper;
import rs.elfak.diplomski.aleksa.notekeeper.model.Note;
import rs.elfak.diplomski.aleksa.notekeeper.model.Task;

/**
 * Created by aleks on 2.10.2016..
 */

public class DataSyncer {

    public static void populateInternalStorage(final Context context, final Fragment currentFragment) {
        if (!isNetworkAvailable(context)) return;

        HttpConnector.getNotes(String.valueOf((new Session(context)).getId()), context, new NoteVolleyCallback() {
            @Override
            public void success(List<Note> noteList) {
                Uri uri = Uri.parse("content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/notes");
                for (final Note note : noteList) {
                    Cursor c = context.getContentResolver().query(uri, null, LocalStorage.NOTE_GLOBAL_ID + " = " + note.getId(), null, null);
                    if (c != null && c.moveToFirst() && c.getInt(c.getColumnIndex(LocalStorage.NOTE_DELETED)) == 0)
                        continue; //or update?

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(LocalStorage.NOTE_GLOBAL_ID, note.getId());
                    contentValues.put(LocalStorage.NOTE_TITLE, note.getTitle());

                    Uri inserted = context.getContentResolver().insert(LocalStorage.CONTENT_NOTE_URI, contentValues);
                    final int localId = Integer.parseInt(inserted.getPathSegments().get(1));

                    HttpConnector.getTasks(note.getId(), context, new TaskVolleyCallback() {
                        @Override
                        public void success(List<Task> taskList) {
                            Uri uri = Uri.parse("content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/tasks");
                            for (Task task : taskList) {
                                Cursor c = context.getContentResolver().query(uri, null, LocalStorage.TASK_GLOBAL_ID + " = " + task.getId(), null, null);
                                if (c != null && c.moveToFirst() && c.getInt(c.getColumnIndex(LocalStorage.TASK_DELETED)) == 0)
                                    continue; //or update?

                                ContentValues contentValues = new ContentValues();
                                contentValues.put(LocalStorage.TASK_GLOBAL_ID, task.getId());
                                contentValues.put(LocalStorage.TASK_TEXT, task.getText());
                                contentValues.put(LocalStorage.TASK_LOCAL_NOTE_ID, localId);
                                contentValues.put(LocalStorage.TASK_GLOBAL_NOTE_ID, note.getId());
                                contentValues.put(LocalStorage.TASK_DEADLINE, task.getDeadline() != null ? task.getDeadline().toString() : "");
                                contentValues.put(LocalStorage.TASK_CHECKED, task.isChecked() ? 1 : 0);

                                context.getContentResolver().insert(LocalStorage.CONTENT_TASK_URI, contentValues);
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

                FragmentTransaction fragTransaction = ((Activity) context).getFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();
            }

            @Override
            public void success(int insertId) {

            }

            @Override
            public void error(String message) {

            }
        });
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void sync(final Context context) {
        if (isNetworkAvailable(context)) {
            final String noteURL = "content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/notes";
            final Uri notes = Uri.parse(noteURL);

            String taskURL = "content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/tasks";
            final Uri tasks = Uri.parse(taskURL);

            Cursor taskCursor = context.getContentResolver().query(tasks, null, null, null, LocalStorage.TASK_ID + " DESC");
            if (taskCursor != null && taskCursor.moveToFirst()) {
                do {
                    final Cursor note = context.getContentResolver().query(notes, null, LocalStorage.NOTE_ID + " = " + taskCursor.getInt(taskCursor.getColumnIndex(LocalStorage.TASK_LOCAL_NOTE_ID)), null, null);

                    //if not attached to note, something went bad
                    if (note == null) continue;

                    //is this note already attached, or else it will be handled with noteCursor below
                    if (note.moveToFirst() && note.getInt(note.getColumnIndex(LocalStorage.NOTE_GLOBAL_ID)) < 0)
                        continue;

                    final Task task = DataMapper.taskFromCursor(context, taskCursor);

                    task.setNoteId(note.getInt(note.getColumnIndex(LocalStorage.NOTE_GLOBAL_ID)));
                    //if new task added while offline and meanwhile not deleted
                    if (taskCursor.getInt(taskCursor.getColumnIndex(LocalStorage.TASK_GLOBAL_ID)) < 0 && taskCursor.getInt(taskCursor.getColumnIndex(LocalStorage.TASK_DELETED)) == 0) {
                        HttpConnector.addTask(task, context, new TaskVolleyCallback() {
                            @Override
                            public void success(List<Task> taskList) {

                            }

                            @Override
                            public void success(int id) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(LocalStorage.TASK_GLOBAL_ID, id);
                                contentValues.put(LocalStorage.TASK_GLOBAL_NOTE_ID, note.getInt(note.getColumnIndex(LocalStorage.NOTE_GLOBAL_ID)));
                                int count = context.getContentResolver().update(tasks, contentValues, LocalStorage.TASK_ID + " = " + task.getId(), null);
                            }

                            @Override
                            public void error(String message) {

                            }
                        });
                    }
                    //if task was deleted offline but exists in database
                    else if (taskCursor.getInt(taskCursor.getColumnIndex(LocalStorage.TASK_DELETED)) == 1 && taskCursor.getInt(taskCursor.getColumnIndex(LocalStorage.TASK_GLOBAL_ID)) > 0) {
                        HttpConnector.deleteTask(taskCursor.getInt(taskCursor.getColumnIndex(LocalStorage.TASK_GLOBAL_ID)), context, new CommonVolleyCallback() {
                            @Override
                            public void success() {
                                context.getContentResolver().delete(tasks, LocalStorage.TASK_ID + " = " + task.getId(), null);
                            }

                            @Override
                            public void error(String message) {

                            }
                        });
                    }
                    //this means task was not created nor deleted, so it should be updated
                    else {
                        HttpConnector.updateTaskText(taskCursor.getInt(taskCursor.getColumnIndex(LocalStorage.TASK_GLOBAL_ID)), task.getText(), context, new CommonVolleyCallback() {
                            @Override
                            public void success() {

                            }

                            @Override
                            public void error(String message) {

                            }
                        });

                        if (task.getDeadline() != null)
                            HttpConnector.setTaskDeadline(taskCursor.getInt(taskCursor.getColumnIndex(LocalStorage.TASK_GLOBAL_ID)), task.getDeadline(), context, new CommonVolleyCallback() {
                                @Override
                                public void success() {

                                }

                                @Override
                                public void error(String message) {

                                }
                            });

                        if (task.isChecked()) {
                            HttpConnector.checkTask(taskCursor.getInt(taskCursor.getColumnIndex(LocalStorage.TASK_GLOBAL_ID)), context, new CommonVolleyCallback() {
                                @Override
                                public void success() {

                                }

                                @Override
                                public void error(String message) {

                                }
                            });
                        } else {
                            HttpConnector.uncheckTask(taskCursor.getInt(taskCursor.getColumnIndex(LocalStorage.TASK_GLOBAL_ID)), context, new CommonVolleyCallback() {
                                @Override
                                public void success() {

                                }

                                @Override
                                public void error(String message) {

                                }
                            });
                        }
                    }
                } while (taskCursor.moveToNext());
            }

            Cursor noteCursor = context.getContentResolver().query(notes, null, null, null, LocalStorage.NOTE_ID + " DESC");

            if (noteCursor != null && noteCursor.moveToFirst()) {
                do {
                    final Note note = DataMapper.noteFromCursor(context, noteCursor);

                    //if new note added while offline and meanwhile not deleted
                    if (noteCursor.getInt(noteCursor.getColumnIndex(LocalStorage.NOTE_GLOBAL_ID)) < 0 && noteCursor.getInt(noteCursor.getColumnIndex(LocalStorage.NOTE_DELETED)) == 0) {
                        HttpConnector.addNote(note, context, new NoteVolleyCallback() {
                            @Override
                            public void success(List<Note> noteList) {

                            }

                            @Override
                            public void success(final int insertId) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(LocalStorage.NOTE_GLOBAL_ID, insertId);
                                context.getContentResolver().update(notes, contentValues, LocalStorage.NOTE_ID + " = " + note.getId(), null);

                                Cursor taskCursor = context.getContentResolver().query(tasks, null, LocalStorage.TASK_LOCAL_NOTE_ID + " = " + note.getId() + " AND " + LocalStorage.TASK_DELETED + " = 0", null, LocalStorage.TASK_ID);
                                if (taskCursor != null && taskCursor.moveToFirst()) {
                                    do {
                                        final Task task = DataMapper.taskFromCursor(context, taskCursor);
                                        task.setNoteId(insertId);
                                        HttpConnector.addTask(task, context, new TaskVolleyCallback() {
                                            @Override
                                            public void success(List<Task> taskList) {

                                            }

                                            @Override
                                            public void success(int id) {
                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put(LocalStorage.TASK_GLOBAL_ID, id);
                                                contentValues.put(LocalStorage.TASK_GLOBAL_NOTE_ID, insertId);
                                                context.getContentResolver().update(tasks, contentValues, LocalStorage.TASK_ID + " = " + task.getId(), null);
                                            }

                                            @Override
                                            public void error(String message) {

                                            }
                                        });
                                    } while (taskCursor.moveToNext());
                                }
                            }

                            @Override
                            public void error(String message) {

                            }
                        });
                        //if note was deleted offline but exists in database
                    } else if (noteCursor.getInt(noteCursor.getColumnIndex(LocalStorage.NOTE_DELETED)) == 1 && noteCursor.getInt(noteCursor.getColumnIndex(LocalStorage.NOTE_GLOBAL_ID)) > 0) {
                        HttpConnector.deleteNote(String.valueOf(noteCursor.getInt(noteCursor.getColumnIndex(LocalStorage.NOTE_GLOBAL_ID))), context, new CommonVolleyCallback() {
                            @Override
                            public void success() {
                                context.getContentResolver().delete(notes, LocalStorage.NOTE_ID + " = " + note.getId(), null);
                            }

                            @Override
                            public void error(String message) {

                            }
                        });
                    }
                    //this means that the not was neither added while offline neither deleted, so it should be updated
                    else {
                        HttpConnector.updateNoteTitle(noteCursor.getInt(noteCursor.getColumnIndex(LocalStorage.NOTE_GLOBAL_ID)), note.getTitle(), context, new CommonVolleyCallback() {
                            @Override
                            public void success() {

                            }

                            @Override
                            public void error(String message) {

                            }
                        });
                    }
                }
                while (noteCursor.moveToNext());
            }
        }
    }

}
