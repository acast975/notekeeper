package rs.elfak.diplomski.aleksa.notekeeper.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.Session;
import rs.elfak.diplomski.aleksa.notekeeper.http.CommonVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.http.HttpConnector;
import rs.elfak.diplomski.aleksa.notekeeper.model.User;

/**
 * Created by aleks on 22.9.2016..
 */
public class SharedAdapter extends RecyclerView.Adapter<FriendAdapterModel> {
    private List<User> userList;
    private Context context;
    int noteId;
    private List<Integer> colors;

    RelativeLayout wrapper;

    public SharedAdapter(Context context, List<User> users, int noteId) {
        userList = users;
        this.noteId = noteId;
        this.context = context;

        colors = new ArrayList<Integer>();
        colors.add(Color.argb(255, 216, 191, 216));
        colors.add(Color.argb(255, 221, 160, 221));
        colors.add(Color.argb(255, 218, 112, 214));
        colors.add(Color.argb(255, 186, 85, 211));
        colors.add(Color.argb(255, 147, 112, 219));
    }

    public FriendAdapterModel onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row, parent, false);
        wrapper = (RelativeLayout) itemView.findViewById(R.id.friend_wrapper);
        ImageView image = (ImageView) itemView.findViewById(R.id.friend_action_image);
        image.setImageResource(R.drawable.ic_clear);
        Random rnd = new Random();
        int color = colors.get(randomWithRange(0, colors.size() - 1));
        wrapper.setBackgroundColor(color);
        return new FriendAdapterModel(itemView);
    }

    @Override
    public void onBindViewHolder(final FriendAdapterModel holder, final int position) {
        User friend = userList.get(position);
        holder.getusername().setText(friend.getUsername());
        String txt = "Gaudeamus igitur, iuvenesmus sumus\n";

        holder.getEmail().setText(friend.getEmail());

        holder.getCard().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want share this note with " + holder.getusername().getText() + "?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        HttpConnector.startSharingNote((new Session(context)).getId(), noteId, userList.get(holder.getAdapterPosition()).getId(), context, new CommonVolleyCallback() {
                            @Override
                            public void success() {

                            }

                            @Override
                            public void error(String message) {
                                if(message != null)
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                        removeItem(holder.getAdapterPosition());
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                builder.setCancelable(true);

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        holder.getDelete().setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    int randomWithRange(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }

    public void removeItem(int position) {
        userList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(User user) {
        userList.add(user);
        notifyDataSetChanged();
    }
}
