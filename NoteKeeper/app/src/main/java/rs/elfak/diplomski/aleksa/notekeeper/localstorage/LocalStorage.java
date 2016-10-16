package rs.elfak.diplomski.aleksa.notekeeper.localstorage;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by aleks on 1.10.2016..
 */

public class LocalStorage extends ContentProvider {

    public static final String PROVIDER_NAME = "rs.elfak.diplomski.aleksa.notekeeper.provider.Notes";
    public static final String NOTE_URL = "content://" + PROVIDER_NAME + "/notes";
    public static final String TASK_URL = "content://" + PROVIDER_NAME + "/tasks";

    public static final Uri CONTENT_NOTE_URI = Uri.parse(NOTE_URL);
    public static final Uri CONTENT_TASK_URI = Uri.parse(TASK_URL);

    /**NOTES**/
    public static final String NOTE_ID = "_id";
    public static final String NOTE_GLOBAL_ID = "global_id";
    public static final String NOTE_TITLE = "title";
    public static final String NOTE_DELETED = "deleted";

    /**TASKS**/
    public static final String TASK_ID = "_id";
    public static final String TASK_GLOBAL_ID = "global_id";
    public static final String TASK_TEXT = "task_text";
    public static final String TASK_LOCAL_NOTE_ID  = "local_note_id";
    public static final String TASK_GLOBAL_NOTE_ID = "global_note_id";
    public static final String TASK_DEADLINE = "deadline";
    public static final String TASK_CHECKED = "checked";
    public static final String TASK_DELETED = "deleted";

    private static HashMap<String, String> NOTES_PROJECTION_MAP;
    private static HashMap<String, String> TASKS_PROJECTION_MAP;

    static final int NOTES =  1;
    static final int NOTES_ID = 2;
    static final int TASKS = 3;
    static final int TASKS_ID = 4;

    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "notes", NOTES);
        uriMatcher.addURI(PROVIDER_NAME, "notes/#", NOTES_ID);
        uriMatcher.addURI(PROVIDER_NAME, "tasks", TASKS);
        uriMatcher.addURI(PROVIDER_NAME, "tasks/#", TASKS_ID);
    }

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "LocalStorage";
    public static final String NOTES_TABLE_NAME = "Notes";
    public static final String TASKS_TABLE_NAME = "Tasks";
    static final int DATABASE_VERSION = 20;
    static final String CREATE_NOTES_TABLE = "CREATE TABLE " + NOTES_TABLE_NAME + " "
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "global_id INTEGER, "
            + "deleted INTEGER DEFAULT 0, "
            + "title TEXT);";
    static final String CREATE_TASKS_TABLE = "CREATE TABLE " + TASKS_TABLE_NAME + " "
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "global_id INTEGER, "
            + "task_text TEXT, "
            + "local_note_id INTEGER, "
            + "global_note_id INTEGER, "
            + "deadline TEXT, "
            + "deleted INTEGER DEFAULT 0, "
            + "checked INTEGER);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_NOTES_TABLE);
            sqLiteDatabase.execSQL(CREATE_TASKS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE_NAME);
            onCreate(sqLiteDatabase);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        db = dbHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)){
            case NOTES:
                qb.setTables(NOTES_TABLE_NAME);
                qb.setProjectionMap(NOTES_PROJECTION_MAP);
                break;
            case NOTES_ID:
                qb.setTables(NOTES_TABLE_NAME);
                qb.appendWhere(NOTE_ID + "=" + uri.getPathSegments().get(1));
                break;

            case TASKS:
                qb.setTables(TASKS_TABLE_NAME);
                qb.setProjectionMap(TASKS_PROJECTION_MAP);
                break;
            case TASKS_ID:
                qb.setTables(TASKS_TABLE_NAME);
                qb.appendWhere(TASK_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri _uri = null;
        long rowId;
        switch (uriMatcher.match(uri)) {
            case NOTES:
                rowId = db.insert(NOTES_TABLE_NAME, "", contentValues);
                if(rowId > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_NOTE_URI, rowId);
                    getContext().getContentResolver().notifyChange(_uri, null, false);
                    return _uri;
                }
                throw new SQLException("Failed to add a record into " + uri);
            case TASKS:
                rowId = db.insert(TASKS_TABLE_NAME, "", contentValues);
                if(rowId > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_TASK_URI, rowId);
                    getContext().getContentResolver().notifyChange(_uri, null, false);
                    return _uri;
                }
                throw new SQLException("Failed to add a record into " + uri);
        }
        return _uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        String id;

        switch (uriMatcher.match(uri)) {
            case NOTES:
                count = db.delete(NOTES_TABLE_NAME, selection, selectionArgs);
                break;
            case NOTES_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(NOTES_TABLE_NAME, NOTES_ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
                break;
            case TASKS:
                count = db.delete(TASKS_TABLE_NAME, selection, selectionArgs);
                break;
            case TASKS_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(TASKS_TABLE_NAME, TASKS_ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
            }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case NOTES:
                count = db.update(NOTES_TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            case NOTES_ID:
                count = db.update(NOTES_TABLE_NAME, contentValues, NOTES_ID + " = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection) ? " AND " + selection + ")" : ""), selectionArgs);
                break;

            case TASKS:
                count = db.update(TASKS_TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            case TASKS_ID:
                count = db.update(TASKS_TABLE_NAME, contentValues, TASK_ID + " = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
