package rs.elfak.diplomski.aleksa.notekeeper.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.Session;
import rs.elfak.diplomski.aleksa.notekeeper.http.HttpConnector;
import rs.elfak.diplomski.aleksa.notekeeper.http.NoteVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.model.Note;
import rs.elfak.diplomski.aleksa.notekeeper.type.NoteKeeperConstants;
import rs.elfak.diplomski.aleksa.notekeeper.type.Types;

public class FriendActivity extends AppCompatActivity {

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        userId = getIntent().getIntExtra("user_id", -1);
        if(userId == -1) {
            Toast.makeText(this, "Error loading friend", Toast.LENGTH_SHORT).show();
            finish();
        }

        NoteFragment fragment = new NoteFragment();
        fragment.setFriendId(userId);
        fragment.SetType(Types.NoteView.VIEW_ASSIGNED_FRIEND);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.friend_frame, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.friend_tab);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    NoteFragment fragment = new NoteFragment();
                    fragment.setFriendId(userId);
                    fragment.SetType(Types.NoteView.VIEW_ASSIGNED_FRIEND);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.friend_frame, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                } else if (tab.getPosition() == 1) {
                    NoteFragment fragment = new NoteFragment();
                    fragment.setFriendId(userId);
                    fragment.SetType(Types.NoteView.VIEW_SHARED_FRIEND);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.friend_frame, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                } else {
                    NoteFragment fragment = new NoteFragment();
                    fragment.SetType(Types.NoteView.VIEW_RECIEVED_FRIEND);
                    fragment.setFriendId(userId);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.friend_frame, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friends_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_assign_note) {
            Note note = new Note(0, "", new Timestamp((new Date(new java.util.Date().getTime())).getTime()), (new Session(FriendActivity.this)).getId(), userId, null);
            HttpConnector.addNote(note, FriendActivity.this, new NoteVolleyCallback() {
                @Override
                public void success(List<Note> noteList) {
                }

                @Override
                public void success(int insertId) {
                    Intent i = new Intent(FriendActivity.this, NoteActivity.class);
                    i.putExtra("view_type", NoteKeeperConstants.NOTE_CREATE_ASSIGNED);
                    i.putExtra("note_id", insertId);
                    i.putExtra("user_id", userId);
                    startActivityForResult(i, 0);
                }

                @Override
                public void error(String message) {
                    if(message != null)
                        Toast.makeText(FriendActivity.this, message, Toast.LENGTH_SHORT);
                }
            });
        }
        return true;
    }
}
