package rs.elfak.diplomski.aleksa.notekeeper.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.Session;
import rs.elfak.diplomski.aleksa.notekeeper.http.CommonVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.http.HttpConnector;
import rs.elfak.diplomski.aleksa.notekeeper.model.User;
import rs.elfak.diplomski.aleksa.notekeeper.model.UserList;

/**
 * Created by aleks on 18.9.2016..
 */
public class FindFriendsAdapter extends RecyclerView.Adapter<FriendAdapterModel> {
    private List<User> friendList;
    Context context;

    public FindFriendsAdapter(Context context, List<User> users) {
        this.context = context;
        friendList = users;
    }

    public FriendAdapterModel onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row, parent, false);
        ImageView image = (ImageView) itemView.findViewById(R.id.friend_action_image);
        image.setImageResource(R.drawable.ic_add_one);
        TextView txt = (TextView) itemView.findViewById(R.id.listItemnFriendEmail);
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (30 * scale + 0.5f);
        txt.getLayoutParams().height = pixels;
        return new FriendAdapterModel(itemView);
    }

    @Override
    public void onBindViewHolder(final FriendAdapterModel holder, final int position) {
        User friend = friendList.get(position);
        holder.getusername().setText(friend.getUsername());
        holder.getEmail().setText(friend.getEmail());
        holder.getCard().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Add friend");
                builder.setMessage("Do you want to add this user as friend?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        HttpConnector.partialFriendship((new Session(context)).getId(), friendList.get(holder.getAdapterPosition()).getId(), false,  context, new CommonVolleyCallback(){
                            @Override
                            public void success() {

                            }

                            @Override
                            public void error(String message) {

                            }
                        });

                        removeItem(holder.getAdapterPosition());
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
        return friendList == null ? 0 : friendList.size();
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
