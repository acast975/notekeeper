package rs.elfak.diplomski.aleksa.notekeeper.activity;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.Session;
import rs.elfak.diplomski.aleksa.notekeeper.adapter.NoteAdapter;
import rs.elfak.diplomski.aleksa.notekeeper.http.CommonVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.http.HttpConnector;
import rs.elfak.diplomski.aleksa.notekeeper.http.NoteVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.localstorage.LocalStorage;
import rs.elfak.diplomski.aleksa.notekeeper.model.DataMapper;
import rs.elfak.diplomski.aleksa.notekeeper.model.Note;
import rs.elfak.diplomski.aleksa.notekeeper.sync.DataSyncer;
import rs.elfak.diplomski.aleksa.notekeeper.type.Types;

/**
 * Created by aleks on 18.9.2016..
 */
public class NoteFragment extends Fragment implements NoteVolleyCallback, SearchableFilter {
    private NoteAdapter adapter;
    private Types.NoteView type;
    private int friendId;
    private String filter;

    @Override
    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    /**
     * MUST CALL
     **/
    public void SetType(Types.NoteView type) {
        this.type = type;
    }

    List<Note> notes;
    RecyclerView recList;

    @Override
    public void success(int insertId) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        recList = (RecyclerView) view.findViewById(R.id.cardList);
        adapter = new NoteAdapter(type, getActivity());
        recList.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        if (type == Types.NoteView.VIEW_STANDARD) {
            if (filter == null) {
                String URL = "content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/notes";
                Uri notes = Uri.parse(URL);
                Cursor c = getActivity().getContentResolver().query(notes, null, LocalStorage.NOTE_DELETED + " = 0", null, LocalStorage.NOTE_ID + " DESC");

                List<Note> noteList = new ArrayList<>();
                if (c != null && c.moveToFirst()) {
                    do {
                        Note note = DataMapper.noteFromCursor(getActivity(), c);
                        noteList.add(note);
                    }
                    while (c.moveToNext());
                }

                success(noteList);
            } else {
                String URL = "content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/notes";
                Uri notes = Uri.parse(URL);
                Cursor c = getActivity().getContentResolver().query(notes, null, LocalStorage.NOTE_TITLE + " LIKE '%" + filter + "%'" + " AND " + LocalStorage.NOTE_DELETED + " = 0", null, LocalStorage.NOTE_ID + " DESC");

                List<Note> noteList = new ArrayList<>();
                if (c != null && c.moveToFirst()) {
                    do {
                        Note note = DataMapper.noteFromCursor(getActivity(), c);
                        noteList.add(note);
                    }
                    while (c.moveToNext());
                }

                success(noteList);
            }
        } else if (type == Types.NoteView.VIEW_SHARED) {
            if (filter == null)
                HttpConnector.getSharedNote((new Session(getActivity())).getId(), getActivity(), this);
            else
                HttpConnector.getSharedNoteFiltered((new Session(getActivity()).getId()), filter, getActivity(), this);
        } else if (type == Types.NoteView.VIEW_ASSIGNED) {
            if (filter == null)
                HttpConnector.getAssignedNotes((new Session(getActivity())).getId(), getActivity(), this);
            else
                HttpConnector.getAssignedNotesFiltered((new Session(getActivity()).getId()), filter, getActivity(), this);
        } else if (type == Types.NoteView.VIEW_RECIEVED) {
            if (filter == null)
                HttpConnector.getReceivedNote((new Session(getActivity())).getId(), getActivity(), this);
            else
                HttpConnector.getReceivedNoteFiltered((new Session(getActivity()).getId()), filter, getActivity(), this);
        } else if (type == Types.NoteView.VIEW_SHARED_FRIEND) {
            HttpConnector.getSharedNotesWithFriend((new Session(getActivity())).getId(), friendId, getActivity(), this);
        } else if (type == Types.NoteView.VIEW_RECIEVED_FRIEND) {
            HttpConnector.getReceivedNotesFromFriend((new Session(getActivity())).getId(), friendId, getActivity(), this);
        } else if (type == Types.NoteView.VIEW_ASSIGNED_FRIEND) {
            HttpConnector.getAssignedNotesToFriend((new Session(getActivity())).getId(), friendId, getActivity(), this);
        } else if (type == Types.NoteView.VIEW_PENDING) {
            if (filter == null)
                HttpConnector.getPendingNotes((new Session(getActivity())).getId(), getActivity(), this);
            else
                HttpConnector.getPendingNotesFiltered((new Session(getActivity()).getId()), filter, getActivity(), this);
        }

        return view;
    }

    @Override
    public void success(final List<Note> noteList) {
        adapter.getNoteList().clear();
        for (Note note : noteList) {
            adapter.addItem(note);
            notes = noteList;
        }

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                if (type == Types.NoteView.VIEW_STANDARD) {
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to delete this note?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Note noteRemoved = notes.get(viewHolder.getAdapterPosition());
                            notes.remove(viewHolder.getAdapterPosition());
                            adapter.removeItem(viewHolder.getAdapterPosition());

                            Uri uri = Uri.parse("content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/notes");

                            if (DataSyncer.isNetworkAvailable(getActivity())) {
                                Cursor c = getActivity().getContentResolver().query(uri, null, LocalStorage.NOTE_ID + " = " + noteRemoved.getId(), null, null);
                                int globalId = -1;
                                if(c != null && c.moveToFirst()) {
                                    globalId = c.getInt(c.getColumnIndex(LocalStorage.NOTE_GLOBAL_ID));
                                }
                                HttpConnector.deleteNote(String.valueOf(globalId), getActivity(), new CommonVolleyCallback() {
                                    @Override
                                    public void success() {

                                    }

                                    @Override
                                    public void error(String message) {
                                        if (message != null)
                                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            ContentValues noteContentValues = new ContentValues();
                            noteContentValues.put(LocalStorage.NOTE_DELETED, 1);
                            getActivity().getContentResolver().update(uri, noteContentValues, LocalStorage.NOTE_ID + " = " + noteRemoved.getId(), null);

                            Uri taskUri =  Uri.parse("content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/tasks");
                            ContentValues taskContentValues = new ContentValues();
                            taskContentValues.put(LocalStorage.TASK_DELETED, 1);
                            int count = getActivity().getContentResolver().update(taskUri, taskContentValues, LocalStorage.TASK_LOCAL_NOTE_ID + " = " + noteRemoved.getId(), null);

                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            dialog.dismiss();
                        }
                    });

                    builder.setCancelable(false);

                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (type == Types.NoteView.VIEW_ASSIGNED || type == Types.NoteView.VIEW_ASSIGNED_FRIEND) {
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to delete this note?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Note noteRemoved = notes.get(viewHolder.getAdapterPosition());
                            notes.remove(viewHolder.getAdapterPosition());
                            adapter.removeItem(viewHolder.getAdapterPosition());
                            HttpConnector.deleteNote(String.valueOf(noteRemoved.getId()), getActivity(), new CommonVolleyCallback() {
                                @Override
                                public void success() {

                                }

                                @Override
                                public void error(String message) {
                                    if (message != null)
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            dialog.dismiss();
                        }
                    });

                    builder.setCancelable(false);

                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (type == Types.NoteView.VIEW_SHARED || type == Types.NoteView.VIEW_SHARED_FRIEND) {
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to stop sharing this note?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Note noteRemoved = notes.get(viewHolder.getAdapterPosition());
                            notes.remove(viewHolder.getAdapterPosition());

                            int userId = type == Types.NoteView.VIEW_SHARED ? (new Session(getActivity())).getId() : friendId;

                            HttpConnector.stopSharingNote((new Session(getActivity())).getId(), noteRemoved.getId(), userId, getActivity(), new CommonVolleyCallback() {
                                @Override
                                public void success() {

                                }

                                @Override
                                public void error(String message) {
                                    if (message != null)
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                }
                            });
                            adapter.removeItem(viewHolder.getAdapterPosition());
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            dialog.dismiss();
                        }
                    });

                    builder.setCancelable(false);

                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (type == Types.NoteView.VIEW_RECIEVED || type == Types.NoteView.VIEW_RECIEVED_FRIEND) {
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to remove this note?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Note noteRemoved = notes.get(viewHolder.getAdapterPosition());
                            notes.remove(viewHolder.getAdapterPosition());
                            HttpConnector.cancelReceivedNote(noteRemoved.getId(), (new Session(getActivity())).getId(), getActivity(), new CommonVolleyCallback() {
                                @Override
                                public void success() {

                                }

                                @Override
                                public void error(String message) {
                                    if (message != null)
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                }
                            });
                            adapter.removeItem(viewHolder.getAdapterPosition());
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    builder.setCancelable(false);

                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (type == Types.NoteView.VIEW_PENDING) {
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to refuse this note?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Note noteRemoved = notes.get(viewHolder.getAdapterPosition());
                            HttpConnector.refuseNote((new Session(getActivity())).getId(), noteRemoved.getId(), getActivity(), new CommonVolleyCallback() {
                                @Override
                                public void success() {
                                    notes.remove(viewHolder.getAdapterPosition());
                                    adapter.removeItem(viewHolder.getAdapterPosition());
                                }

                                @Override
                                public void error(String message) {
                                    if (message != null)
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                }
                            });
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            dialog.dismiss();
                        }
                    });

                    builder.setCancelable(false);

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recList);
    }

    @Override
    public void error(String message) {
        if (message != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
