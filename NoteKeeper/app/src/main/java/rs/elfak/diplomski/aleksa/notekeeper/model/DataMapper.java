package rs.elfak.diplomski.aleksa.notekeeper.model;

import android.content.Context;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import rs.elfak.diplomski.aleksa.notekeeper.Session;
import rs.elfak.diplomski.aleksa.notekeeper.localstorage.LocalStorage;

/**
 * Created by aleks on 24.9.2016..
 */

public class DataMapper {

    public static User userFromJSON(JSONObject object) {
        try {
            User user = new User(object.getInt("id"), object.getString("username"), object.getString("email"), object.getString("password"));
            return user;
        } catch (JSONException exception) {
            return null;
        }
    }

    public static Note noteFromJSON(JSONObject object) {
        try {
            String title = object.getString("title");
            if(title.equals("null")) title = "";
            Note note = new Note(object.getInt("id"), title, Timestamp.valueOf(object.getString("time_created").replace('T', ' ').replace('Z', '0')), object.getInt("user_id"), object.getString("assigned_to").equals("null") ? -1 : object.getInt("assigned_to"), null);
            return note;
        } catch (JSONException exception) {
            return null;
        }
    }

    public static Task taskFromJSON(JSONObject object) {
        try {
            Task task = new Task(object.getString("text"), object.getInt("note_id"), !object.getString("deadline").equals("null") ? Timestamp.valueOf(object.getString("deadline").replace('T', ' ').replace('Z', '0')) : null, object.getInt("checked") != 0, null);
            task.setId(object.getInt("id"));
            return task;
        } catch(JSONException exception) {
            return null;
        }
    }

    public static Note noteFromCursor(Context context, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(LocalStorage.NOTE_TITLE));
        int id = cursor.getInt(cursor.getColumnIndex(LocalStorage.NOTE_ID));
        Note note = new Note(id, title, null, (new Session(context)).getId(), null, null);
        return note;
    }

    public static Task taskFromCursor(Context context, Cursor cursor) {
        String text = cursor.getString(cursor.getColumnIndex(LocalStorage.TASK_TEXT));
        int note_id = cursor.getInt(cursor.getColumnIndex(LocalStorage.TASK_LOCAL_NOTE_ID));
        String s = cursor.getString(cursor.getColumnIndex(LocalStorage.TASK_DEADLINE));
        Timestamp timestamp = !cursor.getString(cursor.getColumnIndex(LocalStorage.TASK_DEADLINE)).equals("") ? Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(LocalStorage.TASK_DEADLINE))) : null;
        boolean checked = cursor.getInt(cursor.getColumnIndex(LocalStorage.TASK_CHECKED)) == 1;
        Task task = new Task(text, note_id, timestamp, checked, null);
        task.setId(cursor.getInt(cursor.getColumnIndex(LocalStorage.TASK_ID)));
        return task;
    }
}
