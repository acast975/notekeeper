package rs.elfak.diplomski.aleksa.notekeeper.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rs.elfak.diplomski.aleksa.notekeeper.R;;
import rs.elfak.diplomski.aleksa.notekeeper.Session;
import rs.elfak.diplomski.aleksa.notekeeper.activity.FriendActivity;
import rs.elfak.diplomski.aleksa.notekeeper.http.CommonVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.http.HttpConnector;
import rs.elfak.diplomski.aleksa.notekeeper.http.UserVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.model.User;
import rs.elfak.diplomski.aleksa.notekeeper.model.UserList;

/**
 * Created by aleks on 18.9.2016..
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapterModel> {
    private List<User> friendList;
    private List<Integer> colors;
    private Context context;

    public FriendAdapter(Context context, List<User> users) {
        friendList = users;

        colors = new ArrayList<Integer>();
        colors.add(Color.argb(255, 216, 191, 216));
        colors.add(Color.argb(255, 221, 160, 221));
        colors.add(Color.argb(255, 218, 112, 214));
        colors.add(Color.argb(255, 186, 85, 211));
        colors.add(Color.argb(255, 147, 112, 219));

        this.context = context;
    }

    public FriendAdapterModel onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row, parent, false);
        RelativeLayout wrapper = (RelativeLayout) itemView.findViewById(R.id.friend_wrapper);
        ImageView image = (ImageView) itemView.findViewById(R.id.friend_action_image);
        image.setImageResource(R.drawable.ic_clear);
        Random rnd = new Random();
        int color = colors.get(randomWithRange(0, colors.size() - 1));
        wrapper.setBackgroundColor(color);
        return new FriendAdapterModel(itemView);
    }

    @Override
    public void onBindViewHolder(final FriendAdapterModel holder, final int position) {
        final User friend = friendList.get(position);
        holder.getusername().setText(friend.getUsername());
        String txt = "Gaudeamus igitur, iuvenesmus sumus\n";

        holder.getEmail().setText(friend.getEmail());
        holder.getCard().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, FriendActivity.class);
                i.putExtra("user_id", friend.getId());
                context.startActivity(i);
            }
        });
        holder.getDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to remove this friend?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        HttpConnector.deleteFriendship((new Session(context)).getId(), friendList.get(holder.getAdapterPosition()).getId(), context, new CommonVolleyCallback() {
                            @Override
                            public void success() {

                            }

                            @Override
                            public void error(String message) {
                                if(message != null)
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                        int i = holder.getAdapterPosition();
                        friendList.remove(i);
                        notifyItemRemoved(i);
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifyItemChanged(holder.getAdapterPosition());
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                builder.setCancelable(false);

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(friendList == null) return 0;
        else return friendList.size();
    }

    int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    public void removeItem(int position) {
        friendList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(User user) {
        friendList.add(user);
        notifyDataSetChanged();
    }
}
