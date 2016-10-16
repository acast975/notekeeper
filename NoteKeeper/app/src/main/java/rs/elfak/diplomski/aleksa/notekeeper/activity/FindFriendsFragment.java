package rs.elfak.diplomski.aleksa.notekeeper.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.adapter.FindFriendsAdapter;

/**
 * Created by aleks on 19.9.2016..
 */
public class FindFriendsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        RecyclerView recList = (RecyclerView) view.findViewById(R.id.cardList);
        final FindFriendsAdapter probe = new FindFriendsAdapter(getActivity(), null);
        recList.setAdapter(probe);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recList.setLayoutManager(llm);

        return view;
    }
}
