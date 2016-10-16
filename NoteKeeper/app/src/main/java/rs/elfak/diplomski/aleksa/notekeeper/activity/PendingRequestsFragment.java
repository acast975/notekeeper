package rs.elfak.diplomski.aleksa.notekeeper.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.adapter.PendingFriendsAdapter;

/**
 * Created by aleks on 19.9.2016..
 */
public class PendingRequestsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        RecyclerView recList = (RecyclerView) view.findViewById(R.id.cardList);
        PendingFriendsAdapter probe = new PendingFriendsAdapter(getActivity(), null);
        recList.setAdapter(probe);
        GridLayoutManager llm = new GridLayoutManager(getActivity(), 2);
        recList.setLayoutManager(llm);
        return view;
    }
}
