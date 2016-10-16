package rs.elfak.diplomski.aleksa.notekeeper.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.StateHolder;
import rs.elfak.diplomski.aleksa.notekeeper.http.CommonVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.http.HttpConnector;
import rs.elfak.diplomski.aleksa.notekeeper.http.NoteVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.http.TaskVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.localstorage.LocalStorage;
import rs.elfak.diplomski.aleksa.notekeeper.model.DataMapper;
import rs.elfak.diplomski.aleksa.notekeeper.model.Note;
import rs.elfak.diplomski.aleksa.notekeeper.model.Task;
import rs.elfak.diplomski.aleksa.notekeeper.sync.DataSyncer;
import rs.elfak.diplomski.aleksa.notekeeper.type.NoteKeeperConstants;
import rs.elfak.diplomski.aleksa.notekeeper.type.Types;

public class NoteActivity extends AppCompatActivity {

    List<Task> taskList;
    private int viewType;

    int noteId;
    int userId; //for assigned

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        noteId = getIntent().getIntExtra("note_id", -1);

        final EditText title = (EditText) findViewById(R.id.note_title);

        viewType = getIntent().getIntExtra("view_type", 0);
        if (viewType == NoteKeeperConstants.NOTE_CREATE) { //CREATE
            taskList = new ArrayList<>();
            startUp();
        } else if (viewType == NoteKeeperConstants.NOTE_STANDARD) {
            Uri uri = Uri.parse("content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/tasks");
            Cursor c = getContentResolver().query(uri, null, LocalStorage.TASK_LOCAL_NOTE_ID + " = " + noteId + " AND " + LocalStorage.TASK_DELETED + " = 0", null, LocalStorage.TASK_ID, null);
            List<Task> taskList = new ArrayList<>();
            if (c != null && c.moveToFirst()) {
                do {
                    taskList.add(DataMapper.taskFromCursor(this, c));
                } while (c.moveToNext());
            }
            NoteActivity.this.taskList = taskList;
            startUp();
        } else if (viewType == NoteKeeperConstants.NOTE_SHARED) {
            title.setEnabled(false);
            HttpConnector.getTasks(noteId, this, new TaskVolleyCallback() {
                @Override
                public void success(List<Task> taskList) {
                    NoteActivity.this.taskList = taskList;
                    startUp();
                }

                @Override
                public void success(int id) {

                }

                @Override
                public void error(String message) {

                }
            });
        } else if (viewType == NoteKeeperConstants.NOTE_CREATE_ASSIGNED) {
            taskList = new ArrayList<>();
            userId = getIntent().getIntExtra("user_id", -1);
            startUp();
        } else if (viewType == NoteKeeperConstants.NOTE_ASSIGNED) {
            HttpConnector.getTasks(noteId, this, new TaskVolleyCallback() {
                @Override
                public void success(List<Task> taskList) {
                    NoteActivity.this.taskList = taskList;
                    startUp();
                }

                @Override
                public void success(int id) {

                }

                @Override
                public void error(String message) {

                }
            });
        } else if (viewType == NoteKeeperConstants.NOTE_RECEIVED) {
            title.setEnabled(false);
            HttpConnector.getTasks(noteId, this, new TaskVolleyCallback() {
                @Override
                public void success(List<Task> taskList) {
                    NoteActivity.this.taskList = taskList;
                    startUp();
                }

                @Override
                public void success(int id) {

                }

                @Override
                public void error(String message) {

                }
            });
        }

        if(viewType != NoteKeeperConstants.NOTE_CREATE && viewType != NoteKeeperConstants.NOTE_STANDARD) {
            HttpConnector.getNote(noteId, this, new NoteVolleyCallback() {
                @Override
                public void success(List<Note> noteList) {
                    if (noteList.size() == 1)
                        title.setText(noteList.get(0).getTitle());
                }

                @Override
                public void success(int insertId) {

                }

                @Override
                public void error(String message) {
                    if (message != null)
                        Toast.makeText(NoteActivity.this, message, Toast.LENGTH_SHORT);
                }
            });
        } else {
            Uri uri = Uri.parse("content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/notes");
            Cursor c = getContentResolver().query(uri, null, LocalStorage.NOTE_ID + " = " + noteId, null, null, null);
            Note note = null;
            if (c != null && c.moveToFirst()) {
                note = DataMapper.noteFromCursor(this, c);
                if(note != null) {
                    title.setText(note.getTitle());
                }
            }
        }

        if (viewType != NoteKeeperConstants.NOTE_RECEIVED) {
            title.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String newText = editable.toString();
                    if (viewType != NoteKeeperConstants.NOTE_CREATE && viewType != NoteKeeperConstants.NOTE_STANDARD) {
                        HttpConnector.updateNoteTitle(noteId, newText, NoteActivity.this, new CommonVolleyCallback() {
                            @Override
                            public void success() {

                            }

                            @Override
                            public void error(String message) {
                                if (message != null)
                                    Toast.makeText(NoteActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(LocalStorage.NOTE_TITLE, newText);
                        int count = getContentResolver().update(LocalStorage.CONTENT_NOTE_URI, contentValues, LocalStorage.NOTE_ID + " = " + noteId, null);
                        Log.w("NoteActivity:", "Updated notes: " + count);

                        if(DataSyncer.isNetworkAvailable(NoteActivity.this)) {
                            Uri uri = Uri.parse("content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/notes");

                            Cursor c = getContentResolver().query(uri, null, LocalStorage.NOTE_ID + " = " + noteId, null, null);
                            int globalId = -1;
                            if(c != null && c.moveToFirst()) {
                                globalId = c.getInt(c.getColumnIndex(LocalStorage.NOTE_GLOBAL_ID));
                            }

                            HttpConnector.updateNoteTitle(globalId, newText, NoteActivity.this, new CommonVolleyCallback() {
                                @Override
                                public void success() {

                                }

                                @Override
                                public void error(String message) {
                                    if (message != null)
                                        Toast.makeText(NoteActivity.this, message, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void startUp() {
        final LinearLayout rootActive = (LinearLayout) findViewById(R.id.actives);
        final LinearLayout rootPassive = (LinearLayout) findViewById(R.id.passives);

        for (Task task : taskList) {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LinearLayout view = (LinearLayout) inflater.inflate(R.layout.task_template, null);
            LinearLayout content = (LinearLayout) view.findViewById(R.id.task_content);
            EditText text = (EditText) content.getChildAt(1);
            if(viewType == NoteKeeperConstants.NOTE_RECEIVED || viewType == NoteKeeperConstants.NOTE_SHARED) {
                text.setEnabled(false);
            }
            CheckBox checkBox = (CheckBox) content.getChildAt(0);
            text.setText(task.getText());
            TextView dateText = (TextView) view.findViewById(R.id.date);
            String dateString = task.getDeadline() != null ? task.getDeadline().toString().substring(0, 19) : "";
            dateText.setText(dateString);

            if (!task.isChecked()) {
                setUpTasks(view, rootActive, true, task.getId());
            } else {
                setUpTasks(view, rootPassive, false, task.getId());
            }
        }

        if (viewType != NoteKeeperConstants.NOTE_RECEIVED && viewType != NoteKeeperConstants.NOTE_SHARED) {
            final EditText text = new EditText(this);
            text.setHint("New task...");
            rootActive.addView(text);
            text.setSingleLine(true);
            text.setBackground(null);
            text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (keyEvent == null) {
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        final LinearLayout newView = (LinearLayout) inflater.inflate(R.layout.task_template, null);
                        LinearLayout content = (LinearLayout) newView.findViewById(R.id.task_content);

                        rootActive.addView(newView, rootActive.indexOfChild(text));

                        EditText editText = (EditText) content.getChildAt(1);

                        editText.setText(text.getText());
                        text.setText("");

                        editText.requestFocus();


                        setUpTasks(newView, rootActive, true, -1);

                        return true;
                    } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        final LinearLayout newView = (LinearLayout) inflater.inflate(R.layout.task_template, null);
                        LinearLayout content = (LinearLayout) newView.findViewById(R.id.task_content);

                        rootActive.addView(newView, rootActive.indexOfChild(text));

                        EditText editText = (EditText) content.getChildAt(1);

                        editText.setText(text.getText());
                        text.setText("");

                        editText.requestFocus();


                        setUpTasks(newView, rootActive, true, -1);

                        return true;
                    } else return false;

                }
            });
        }
    }

    private void setUpTasks(final LinearLayout view, final LinearLayout root, final boolean active, final int existingId) {

        final Integer taskId = new Integer(0);

        final LinearLayout content = (LinearLayout) view.findViewById(R.id.task_content);

        final TextView dateText = (TextView) view.findViewById(R.id.date);

        final EditText text = (EditText) content.getChildAt(1);
        final ImageView setTime = (ImageView) content.getChildAt(2);
        final ImageView remove = (ImageView) content.getChildAt(3);
        final CheckBox checkBox = (CheckBox) content.getChildAt(0);

        final LinearLayout rootActive = (LinearLayout) findViewById(R.id.actives);
        final LinearLayout rootPassive = (LinearLayout) findViewById(R.id.passives);

        final View delimiter = findViewById(R.id.delimiter);
        final TextView delimiterText = (TextView) findViewById(R.id.delimiterText);

        final StateHolder stateHolder = new StateHolder();
        stateHolder.setState(existingId);

        if (existingId < 0) {
            if (viewType != NoteKeeperConstants.NOTE_CREATE && viewType != NoteKeeperConstants.NOTE_STANDARD) {
                HttpConnector.addTask(new Task(text.getText().toString(), noteId, null /*TODO - SOLVE*/, checkBox.isChecked(), null), NoteActivity.this, new TaskVolleyCallback() {
                    @Override
                    public void success(List<Task> taskList) {

                    }

                    @Override
                    public void success(final int id) {
                        stateHolder.setState(id);
                    }

                    @Override
                    public void error(String message) {
                        if (message != null)
                            Toast.makeText(NoteActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(LocalStorage.TASK_TEXT, text.getText().toString());
                contentValues.put(LocalStorage.TASK_LOCAL_NOTE_ID, noteId);
                contentValues.put(LocalStorage.TASK_GLOBAL_ID, -1);
                contentValues.put(LocalStorage.TASK_DEADLINE, "");
                contentValues.put(LocalStorage.TASK_CHECKED, checkBox.isChecked() ? 1 : 0);
                final Uri uri = getContentResolver().insert(LocalStorage.CONTENT_TASK_URI, contentValues);

                int taskid = Integer.valueOf(uri.getPathSegments().get(1));
                stateHolder.setState(taskid);
                Log.w("NoteActivity", String.valueOf(taskid));

                if(DataSyncer.isNetworkAvailable(this)) {
                    Uri noteUri = Uri.parse("content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/notes");
                    Cursor c = getContentResolver().query(noteUri, null, LocalStorage.NOTE_ID + " = " + noteId, null, null);
                    int globalId = -1;
                    if(c != null && c.moveToFirst()) {
                        globalId = c.getInt(c.getColumnIndex(LocalStorage.NOTE_GLOBAL_ID));
                    }

                    final int globalNote = globalId;
                    HttpConnector.addTask(new Task(text.getText().toString(), globalId, null, checkBox.isChecked(), null), NoteActivity.this, new TaskVolleyCallback() {
                        @Override
                        public void success(List<Task> taskList) {

                        }

                        @Override
                        public void success(final int id) {


                            ContentValues update = new ContentValues();
                            update.put(LocalStorage.TASK_GLOBAL_ID, id);
                            update.put(LocalStorage.TASK_GLOBAL_NOTE_ID, globalNote);

                            int count = getContentResolver().update(uri, update, LocalStorage.TASK_ID + " = " + stateHolder.getState(), null);
                            Log.w("C:" , String.valueOf(count));
                        }

                        @Override
                        public void error(String message) {
                            if (message != null)
                                Toast.makeText(NoteActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

        if (viewType != NoteKeeperConstants.NOTE_RECEIVED) {
            text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (viewType != NoteKeeperConstants.NOTE_CREATE && viewType != NoteKeeperConstants.NOTE_STANDARD) {
                        HttpConnector.updateTaskText(stateHolder.getState(), editable.toString(), NoteActivity.this, new CommonVolleyCallback() {
                            @Override
                            public void success() {

                            }

                            @Override
                            public void error(String message) {
                                if (message != null)
                                    Toast.makeText(NoteActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(LocalStorage.TASK_TEXT, editable.toString());
                        int count = getContentResolver().update(LocalStorage.CONTENT_TASK_URI, contentValues, LocalStorage.TASK_ID + " = " + stateHolder.getState(), null);
                        Log.w("NoteActivity", "Updated task: " + stateHolder.getState());

                        if(DataSyncer.isNetworkAvailable(NoteActivity.this)) {
                            Uri taskUri = Uri.parse("content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/tasks");
                            Cursor c = getContentResolver().query(taskUri, null, LocalStorage.TASK_ID + " = " + stateHolder.getState(), null, null);
                            int globalId = -1;
                            if(c != null && c.moveToFirst()) {
                                globalId = c.getInt(c.getColumnIndex(LocalStorage.TASK_GLOBAL_ID));
                            }

                            HttpConnector.updateTaskText(globalId, editable.toString(), NoteActivity.this, new CommonVolleyCallback() {
                                @Override
                                public void success() {

                                }

                                @Override
                                public void error(String message) {
                                    if (message != null)
                                        Toast.makeText(NoteActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View affectedView) {
                    int index = root.indexOfChild(view);
                    root.removeViewAt(index);

                    //refreshViews(root, view, active);
                    if (index > 0)
                        root.getChildAt(index - 1).requestFocus();
                    if (!active && rootPassive.getChildCount() == 0) {
                        delimiter.setVisibility(View.INVISIBLE);
                        delimiterText.setVisibility(View.INVISIBLE);
                    }
                    if (viewType != NoteKeeperConstants.NOTE_CREATE && viewType != NoteKeeperConstants.NOTE_STANDARD) {
                        HttpConnector.deleteTask(stateHolder.getState(), NoteActivity.this, new CommonVolleyCallback() {
                            @Override
                            public void success() {

                            }

                            @Override
                            public void error(String message) {
                                if (message != null)
                                    Toast.makeText(NoteActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(LocalStorage.TASK_DELETED, 1);
                        int count = getContentResolver().update(LocalStorage.CONTENT_TASK_URI, contentValues, LocalStorage.NOTE_ID + " = " + stateHolder.getState(), null);
                        Log.w("Deleted tasks: ", String.valueOf(stateHolder.getState()));

                        if(DataSyncer.isNetworkAvailable(NoteActivity.this)) {
                            Uri taskUri = Uri.parse("content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/tasks");
                            Cursor c = getContentResolver().query(taskUri, null, LocalStorage.TASK_ID + " = " + stateHolder.getState(), null, null);
                            int globalId = -1;
                            if(c != null && c.moveToFirst()) {
                                globalId = c.getInt(c.getColumnIndex(LocalStorage.TASK_GLOBAL_ID));
                            }

                            HttpConnector.deleteTask(globalId, NoteActivity.this, new CommonVolleyCallback() {
                                @Override
                                public void success() {

                                }

                                @Override
                                public void error(String message) {
                                    if (message != null)
                                        Toast.makeText(NoteActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });


            setTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final View dialogView = View.inflate(NoteActivity.this, R.layout.date_time_picker, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(NoteActivity.this).create();

                    dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                            TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                            Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                    datePicker.getMonth(),
                                    datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(),
                                    timePicker.getCurrentMinute());

                            long time = calendar.getTimeInMillis();

                            final java.sql.Timestamp timestamp = new java.sql.Timestamp(time);
                            if (viewType != NoteKeeperConstants.NOTE_CREATE && viewType != NoteKeeperConstants.NOTE_STANDARD) {
                                HttpConnector.setTaskDeadline(stateHolder.getState(), timestamp, NoteActivity.this, new CommonVolleyCallback() {
                                    @Override
                                    public void success() {
                                        dateText.setText(timestamp.toString().substring(0, 19));
                                    }

                                    @Override
                                    public void error(String message) {

                                    }
                                });
                            } else {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(LocalStorage.TASK_DEADLINE, timestamp.toString());
                                int count = getContentResolver().update(LocalStorage.CONTENT_TASK_URI, contentValues, LocalStorage.TASK_ID + " = " + stateHolder.getState(), null);
                                Log.w("NoteActivity", "Updated task: " + stateHolder.getState());

                                Uri taskUri = Uri.parse("content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/tasks");
                                Cursor c = getContentResolver().query(taskUri, null, LocalStorage.TASK_ID + " = " + stateHolder.getState(), null, null);
                                int globalId = -1;
                                if(c != null && c.moveToFirst()) {
                                    globalId = c.getInt(c.getColumnIndex(LocalStorage.TASK_GLOBAL_ID));
                                }

                                if (count == 1) {
                                    dateText.setText(timestamp.toString().substring(0, 19));

                                    if(DataSyncer.isNetworkAvailable(NoteActivity.this)) {
                                        HttpConnector.setTaskDeadline(globalId, timestamp, NoteActivity.this, new CommonVolleyCallback() {
                                            @Override
                                            public void success() {

                                            }

                                            @Override
                                            public void error(String message) {

                                            }
                                        });
                                    }
                                }
                            }

                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.setView(dialogView);
                    alertDialog.show();
                }
            });
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Uri taskUri = Uri.parse("content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/tasks");
                if (b && active) {
                    root.removeViewAt(root.indexOfChild(view));
                    //refreshViews(root, view, active);
                    rootPassive.addView(view);

                    if (viewType != NoteKeeperConstants.NOTE_CREATE && viewType != NoteKeeperConstants.NOTE_STANDARD) {
                        HttpConnector.checkTask(stateHolder.getState(), NoteActivity.this, new CommonVolleyCallback() {
                            @Override
                            public void success() {

                            }

                            @Override
                            public void error(String message) {
                                if (message != null)
                                    Toast.makeText(NoteActivity.this, message, Toast.LENGTH_SHORT);
                            }
                        });
                    } else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(LocalStorage.TASK_CHECKED, checkBox.isChecked() ? 1 : 0);
                        int count = getContentResolver().update(LocalStorage.CONTENT_TASK_URI, contentValues, LocalStorage.TASK_ID + " = " + stateHolder.getState(), null);
                        Log.w("NoteActivity", "Updated task: " + stateHolder.getState());

                        Cursor c = getContentResolver().query(taskUri, null, LocalStorage.TASK_ID + " = " + stateHolder.getState(), null, null);
                        int globalIdCheck = -1;
                        if(c != null && c.moveToFirst()) {
                            globalIdCheck = c.getInt(c.getColumnIndex(LocalStorage.TASK_GLOBAL_ID));
                        }

                        HttpConnector.checkTask(globalIdCheck, NoteActivity.this, new CommonVolleyCallback() {
                            @Override
                            public void success() {

                            }

                            @Override
                            public void error(String message) {
                                if (message != null)
                                    Toast.makeText(NoteActivity.this, message, Toast.LENGTH_SHORT);
                            }
                        });
                    }
                    setUpTasks(view, rootPassive, false, stateHolder.getState());
                } else if (!b & !active) {
                    root.removeViewAt(root.indexOfChild(view));
                    //refreshViews(root, view, active)
                    rootActive.addView(view, rootActive.getChildCount() - 1);
                    setUpTasks(view, rootActive, true, stateHolder.getState());
                    if (rootPassive.getChildCount() == 0) {
                        delimiter.setVisibility(View.INVISIBLE);
                        delimiterText.setVisibility(View.INVISIBLE);
                    }
                    if (viewType != NoteKeeperConstants.NOTE_CREATE && viewType != NoteKeeperConstants.NOTE_STANDARD) {
                        HttpConnector.uncheckTask(stateHolder.getState(), NoteActivity.this, new CommonVolleyCallback() {
                            @Override
                            public void success() {

                            }

                            @Override
                            public void error(String message) {

                            }
                        });
                    } else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(LocalStorage.TASK_CHECKED, checkBox.isChecked() ? 1 : 0);
                        int count = getContentResolver().update(LocalStorage.CONTENT_TASK_URI, contentValues, LocalStorage.TASK_ID + " = " + stateHolder.getState(), null);
                        Log.w("NoteActivity", "Updated task: " + stateHolder.getState());


                        Cursor c = getContentResolver().query(taskUri, null, LocalStorage.TASK_ID + " = " + stateHolder.getState(), null, null);
                        int globalIdUncheck = -1;
                        if(c != null && c.moveToFirst()) {
                            globalIdUncheck = c.getInt(c.getColumnIndex(LocalStorage.TASK_GLOBAL_ID));
                        }

                        HttpConnector.uncheckTask(globalIdUncheck, NoteActivity.this, new CommonVolleyCallback() {
                            @Override
                            public void success() {

                            }

                            @Override
                            public void error(String message) {

                            }
                        });
                    }
                }
            }
        });

        if (!active)

        {
            checkBox.setChecked(true);
            text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            delimiter.setVisibility(View.VISIBLE);
            delimiterText.setVisibility(View.VISIBLE);
            text.setTextColor(Color.argb(255, 183, 183, 183));
        } else

        {
            text.setPaintFlags(text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            text.setTextColor(Color.argb(255, 0, 0, 0));
        }

        if (root.indexOfChild(view) < 0)
            root.addView(view);

        if (viewType != NoteKeeperConstants.NOTE_RECEIVED)

        {
            text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        remove.setVisibility(View.VISIBLE);
                        setTime.setVisibility(View.VISIBLE);
                    } else {
                        remove.setVisibility(View.INVISIBLE);
                        setTime.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

    }

    private void activateNewTasks(final LinearLayout view, final LinearLayout root, final boolean active) {
        LinearLayout content = (LinearLayout) view.findViewById(R.id.task_content);
        final EditText text = (EditText) content.getChildAt(1);
        final CheckBox checkBox = (CheckBox) content.getChildAt(0);
        TextView date = (TextView) view.findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View dialogView = View.inflate(NoteActivity.this, R.layout.date_time_picker, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(NoteActivity.this).create();

                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute());

                        long time = calendar.getTimeInMillis();
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setView(dialogView);
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        if (viewType == NoteKeeperConstants.NOTE_SHARED || viewType == NoteKeeperConstants.NOTE_CREATE_ASSIGNED || viewType == NoteKeeperConstants.NOTE_ASSIGNED || viewType == NoteKeeperConstants.NOTE_RECEIVED) {
            menu.getItem(1).setVisible(false);
            menu.getItem(0).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_people_shared) {
            Intent i = new Intent(this, SharedActivity.class);
            i.putExtra("note_id", noteId);
            startActivity(i);
        } else if (item.getItemId() == R.id.nav_people_share) {
            Intent i = new Intent(this, ShareActivity.class);
            i.putExtra("note_id", noteId);
            startActivity(i);
        }
        return true;
    }
}