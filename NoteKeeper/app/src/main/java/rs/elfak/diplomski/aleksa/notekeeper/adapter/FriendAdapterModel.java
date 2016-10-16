package rs.elfak.diplomski.aleksa.notekeeper.adapter;

import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import rs.elfak.diplomski.aleksa.notekeeper.R;

/**
 * Created by aleks on 18.9.2016..
 */
public class FriendAdapterModel extends RecyclerView.ViewHolder {
    private TextView username;
    private TextView email;
    private ImageView delete;
    private CardView card;

    public FriendAdapterModel(View v) {
        super(v);
        username = (TextView) v.findViewById(R.id.listItemnFriendUsername);
        email = (TextView) v.findViewById(R.id.listItemnFriendEmail);
        delete = (ImageView) v.findViewById(R.id.friend_action_image);
        card = (CardView) v.findViewById(R.id.card_view);
    }

    public TextView getusername() {
        return username;
    }

    public TextView getEmail() {
        return email;
    }

    public ImageView getDelete() {
        return delete;
    }

    public CardView getCard() {
        return card;
    }
}
