package rs.elfak.diplomski.aleksa.notekeeper.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.adapter.NoteAdapter;
import rs.elfak.diplomski.aleksa.notekeeper.type.Types;

/**
 * Created by aleks on 20.9.2016..
 */
public class NoteFragmentCompat extends Fragment {
    private NoteAdapter adapter;
    private Types.NoteView type;

    /**
     * MUST CALL
     **/
    public void SetType(Types.NoteView type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        final RecyclerView recList = (RecyclerView) view.findViewById(R.id.cardList);
        adapter = new NoteAdapter(type, getActivity());
        recList.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                if(type == Types.NoteView.VIEW_STANDARD) {
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to delete this note?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
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
                } else if (type == Types.NoteView.VIEW_ASSIGNED) {
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to remove this note?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
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
                } else if (type == Types.NoteView.VIEW_SHARED) {
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to stop sharing this note?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
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
                } else if (type == Types.NoteView.VIEW_RECIEVED) {
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to remove this note?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
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
                }
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recList);

        return view;
    }
}
