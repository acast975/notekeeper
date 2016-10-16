package rs.elfak.diplomski.aleksa.notekeeper.activity;

import android.app.FragmentTransaction;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.localstorage.LocalStorage;
import rs.elfak.diplomski.aleksa.notekeeper.type.Types;

public class SharedActivity extends AppCompatActivity {

    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        noteId = getIntent().getIntExtra("note_id", -1);

        Uri uri = Uri.parse("content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/notes");

        Cursor note = getContentResolver().query(uri, null, LocalStorage.NOTE_ID + " = " + noteId, null, null);

        int globalId = -1;
        if(note != null && note.moveToFirst())
            globalId = note.getInt(note.getColumnIndex(LocalStorage.NOTE_GLOBAL_ID));

        FriendsFragment fragment = new FriendsFragment();
        fragment.setType(Types.UserView.VIEW_SHARED);
        fragment.setNoteId(globalId);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
}
