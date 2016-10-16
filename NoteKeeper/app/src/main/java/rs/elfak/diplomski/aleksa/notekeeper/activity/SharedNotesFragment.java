package rs.elfak.diplomski.aleksa.notekeeper.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.adapter.NoteAdapter;
import rs.elfak.diplomski.aleksa.notekeeper.type.Types;

/**
 * Created by aleks on 20.9.2016..
 */
public class SharedNotesFragment extends Fragment {
    private NoteAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        final RecyclerView recList = (RecyclerView) view.findViewById(R.id.cardList);
        adapter = new NoteAdapter(Types.NoteView.VIEW_STANDARD, getActivity());
        recList.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        return  view;
    }
}
