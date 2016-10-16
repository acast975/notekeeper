package rs.elfak.diplomski.aleksa.notekeeper.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.Session;
import rs.elfak.diplomski.aleksa.notekeeper.activity.NoteActivity;
import rs.elfak.diplomski.aleksa.notekeeper.http.CommonVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.http.HttpConnector;
import rs.elfak.diplomski.aleksa.notekeeper.http.UserVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.model.Note;
import rs.elfak.diplomski.aleksa.notekeeper.model.User;
import rs.elfak.diplomski.aleksa.notekeeper.type.NoteKeeperConstants;
import rs.elfak.diplomski.aleksa.notekeeper.type.Types;

/**
 * Created by aleks on 18.9.2016..
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapterModel> {
    private List<Note> noteList;
    Types.NoteView type;
    Context context;

    public NoteAdapter(Types.NoteView type, Context context) {
        noteList = new ArrayList<Note>();
        this.context = context;
        this.type = type;
    }

    @Override
    public NoteAdapterModel onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_row, parent, false);

        return new NoteAdapterModel(itemView);
    }

    @Override
    public void onBindViewHolder(final NoteAdapterModel holder, int position) {
        Note note = noteList.get(position);
        if (note.getTitle() != null)
            holder.getTitle().setText(note.getTitle());

        if (type == Types.NoteView.VIEW_ASSIGNED) {
            HttpConnector.getUserById(note.getAssignedTo(), context, new UserVolleyCallback() {
                @Override
                public void success(List<User> users) {
                    if (users.size() == 1) {
                        String username = users.get(0).getUsername();
                        holder.getAdditionalText().setText("Assigned to " + username);
                    }
                }

                @Override
                public void error(String message) {

                }
            });


        } else if (type == Types.NoteView.VIEW_RECIEVED) {
            HttpConnector.getUserById(note.getUserId(), context, new UserVolleyCallback() {
                @Override
                public void success(List<User> users) {
                    if (users.size() == 1) {
                        String username = users.get(0).getUsername();
                        holder.getAdditionalText().setText("Received from " + username);
                    }
                }

                @Override
                public void error(String message) {

                }
            });
        } else if (type == Types.NoteView.VIEW_SHARED) {
            HttpConnector.getUserById(note.getUserId(), context, new UserVolleyCallback() {
                @Override
                public void success(List<User> users) {
                    if (users.size() == 1) {
                        String username = users.get(0).getUsername();
                        holder.getAdditionalText().setText("Shared by " + username);
                    }
                }

                @Override
                public void error(String message) {

                }
            });
        } else if (type == Types.NoteView.VIEW_PENDING) {
            HttpConnector.getUserById(note.getUserId(), context, new UserVolleyCallback() {
                @Override
                public void success(List<User> users) {
                    if (users.size() == 1) {
                        String username = users.get(0).getUsername();
                        holder.getAdditionalText().setText("Received from " + username);
                    }
                }

                @Override
                public void error(String message) {

                }
            });
        }

        //holder.getBody().setText(txt);

        if (type == Types.NoteView.VIEW_STANDARD) {
            holder.getV().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, NoteActivity.class);
                    i.putExtra("note_id", noteList.get(holder.getAdapterPosition()).getId());
                    i.putExtra("view_type", NoteKeeperConstants.NOTE_STANDARD);
                    context.startActivity(i);
                }
            });
        } else if (type == Types.NoteView.VIEW_SHARED || type == Types.NoteView.VIEW_SHARED_FRIEND) {
            holder.getV().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, NoteActivity.class);
                    i.putExtra("note_id", noteList.get(holder.getAdapterPosition()).getId());
                    i.putExtra("view_type", NoteKeeperConstants.NOTE_SHARED);
                    context.startActivity(i);
                }
            });
        } else if (type == Types.NoteView.VIEW_ASSIGNED || type == Types.NoteView.VIEW_ASSIGNED_FRIEND) {
            holder.getV().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, NoteActivity.class);
                    i.putExtra("note_id", noteList.get(holder.getAdapterPosition()).getId());
                    i.putExtra("view_type", NoteKeeperConstants.NOTE_ASSIGNED);
                    context.startActivity(i);
                }
            });
        } else if (type == Types.NoteView.VIEW_RECIEVED || type == Types.NoteView.VIEW_RECIEVED_FRIEND) {
            holder.getV().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, NoteActivity.class);
                    i.putExtra("note_id", noteList.get(holder.getAdapterPosition()).getId());
                    i.putExtra("view_type", NoteKeeperConstants.NOTE_RECEIVED);
                    context.startActivity(i);
                }
            });
        } else if (type == Types.NoteView.VIEW_PENDING) {
            holder.getV().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to accept this note?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Note noteRemoved = noteList.get(holder.getAdapterPosition());
                            HttpConnector.acceptNote((new Session(context)).getId(),noteList.get(holder.getAdapterPosition()).getId(), context, new CommonVolleyCallback() {
                                @Override
                                public void success() {
                                    removeItem(holder.getAdapterPosition());
                                    notifyItemChanged(holder.getAdapterPosition());
                                }



                                @Override
                                public void error(String message) {
                                    notifyItemChanged(holder.getAdapterPosition());
                                }
                            });
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setCancelable(true);

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void removeItem(int position) {
        noteList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(Note note) {
        noteList.add(note);
        notifyDataSetChanged();
    }

    public List<Note> getNoteList() {
        return noteList;
    }
}
