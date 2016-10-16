package rs.elfak.diplomski.aleksa.notekeeper.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import rs.elfak.diplomski.aleksa.notekeeper.Preferences;
import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.Session;
import rs.elfak.diplomski.aleksa.notekeeper.gcm.RegistrationIntentService;
import rs.elfak.diplomski.aleksa.notekeeper.http.HttpConnector;
import rs.elfak.diplomski.aleksa.notekeeper.http.NoteVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.localstorage.LocalStorage;
import rs.elfak.diplomski.aleksa.notekeeper.model.Note;
import rs.elfak.diplomski.aleksa.notekeeper.receiver.PendingTasksReceiver;
import rs.elfak.diplomski.aleksa.notekeeper.sync.DataSyncer;
import rs.elfak.diplomski.aleksa.notekeeper.type.NoteKeeperConstants;
import rs.elfak.diplomski.aleksa.notekeeper.type.Types;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton fab;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;
    Fragment currentFragment;

    private BroadcastReceiver connectionChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            DataSyncer.sync(context);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Session session = new Session(this);
        if (session.getUsername() == "") {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 0);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note note = new Note(0, "", new Timestamp((new Date(new java.util.Date().getTime())).getTime()), (new Session(MainActivity.this)).getId(), null, null);

                ContentValues values = new ContentValues();
                values.put(LocalStorage.NOTE_TITLE, note.getTitle());
                values.put(LocalStorage.NOTE_GLOBAL_ID, -1);

                Uri uri = getContentResolver().insert(LocalStorage.CONTENT_NOTE_URI, values);

                final int noteId = Integer.parseInt(uri.getPathSegments().get(1));
                Log.w("MainActivity", String.valueOf(noteId));

                if(DataSyncer.isNetworkAvailable(MainActivity.this)) {
                    HttpConnector.addNote(note, MainActivity.this, new NoteVolleyCallback() {
                        @Override
                        public void success(int id) {
                            Uri uri = Uri.parse("content://rs.elfak.diplomski.aleksa.notekeeper.provider.Notes/notes");

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(LocalStorage.NOTE_GLOBAL_ID, id);
                            getContentResolver().update(uri, contentValues, LocalStorage.NOTE_ID + " = " + noteId, null);

                            Intent i = new Intent(MainActivity.this, NoteActivity.class);
                            i.putExtra("view_type", 0);
                            i.putExtra("note_id", noteId);
                            startActivityForResult(i, 0);
                        }

                        @Override
                        public void success(List<Note> noteList) {

                        }

                        @Override
                        public void error(String message) {
                            if (message != null) {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Intent i = new Intent(MainActivity.this, NoteActivity.class);
                    i.putExtra("view_type", NoteKeeperConstants.NOTE_CREATE);
                    i.putExtra("note_id", noteId);
                    startActivityForResult(i, 0);
                }
            }
        });

        EditText search = (EditText) findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                if (!editable.toString().equals(""))
                    ((SearchableFilter) currentFragment).setFilter(editable.toString());
                else ((SearchableFilter) currentFragment).setFilter(null);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent gotIntent = getIntent();
        if (gotIntent != null) {
            int view = gotIntent.getIntExtra("view", 0);

            if (view == NoteKeeperConstants.MAIN_VIEW_MY) {
                NoteFragment fragment = new NoteFragment();
                fragment.SetType(Types.NoteView.VIEW_STANDARD);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_frame, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
                currentFragment = fragment;

                DataSyncer.populateInternalStorage(this, currentFragment);
            } else if (view == NoteKeeperConstants.MAIN_VIEW_ASSIGNED) {
                NoteFragment fragment = new NoteFragment();
                fragment.SetType(Types.NoteView.VIEW_ASSIGNED);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_frame, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
                fab.setVisibility(View.INVISIBLE);
                currentFragment = fragment;
            } else if (view == NoteKeeperConstants.MAIN_VIEW_PENDING) {
                NoteFragment fragment = new NoteFragment();
                fragment.SetType(Types.NoteView.VIEW_PENDING);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_frame, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
                fab.setVisibility(View.INVISIBLE);
                currentFragment = fragment;
            } else if (view == NoteKeeperConstants.MAIN_VIEW_SHARED) {
                NoteFragment fragment = new NoteFragment();
                fragment.SetType(Types.NoteView.VIEW_SHARED);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_frame, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
                fab.setVisibility(View.INVISIBLE);
                currentFragment = fragment;
            } else if (view == NoteKeeperConstants.MAIN_VIEW_FRIENDS) {
                FriendsFragment fragment = new FriendsFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_frame, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
                fab.setVisibility(View.INVISIBLE);
                currentFragment = fragment;
            } else if (view == NoteKeeperConstants.MAIN_VIEW_FRIENDS_PENDING) {
                FriendsFragment fragment = new FriendsFragment();
                fragment.setType(Types.UserView.VIEW_PENDING);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.main_frame, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
                fab.setVisibility(View.INVISIBLE);
                currentFragment = fragment;
            }
        } else {
            NoteFragment fragment = new NoteFragment();
            fragment.SetType(Types.NoteView.VIEW_STANDARD);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
            currentFragment = fragment;
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(Preferences.SENT_TOKEN_TO_SERVER, false);
            }
        };

        registerReceiver();

        registerReceiver(connectionChangedReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        Intent intent = new Intent(this, PendingTasksReceiver.class);
        startService(intent);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent backgroundWorker = new Intent(this, PendingTasksReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, backgroundWorker, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 30000, AlarmManager.INTERVAL_HOUR, alarmIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, NoteActivity.class);
            startActivity(i);
        } else if (id == R.id.action_signout) {
            Session session = new Session(this);
            session.setUsername("");
            session.setId(-1);

            getContentResolver().delete(LocalStorage.CONTENT_NOTE_URI, null, null);
            getContentResolver().delete(LocalStorage.CONTENT_TASK_URI, null, null);

            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            NoteFragment fragment = new NoteFragment();
            fragment.SetType(Types.NoteView.VIEW_STANDARD);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
            fab.setVisibility(View.VISIBLE);
            currentFragment = fragment;
        } else if (id == R.id.nav_friends) {
            FriendsFragment fragment = new FriendsFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
            fab.setVisibility(View.INVISIBLE);
            currentFragment = fragment;
        } else if (id == R.id.nav_find_friends) {
            FriendsFragment fragment = new FriendsFragment();
            fragment.setType(Types.UserView.VIEW_SEARCH);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
            fab.setVisibility(View.INVISIBLE);
            currentFragment = fragment;
        } else if (id == R.id.nav_pending_requests) {
            FriendsFragment fragment = new FriendsFragment();
            fragment.setType(Types.UserView.VIEW_PENDING);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
            fab.setVisibility(View.INVISIBLE);
            currentFragment = fragment;
        } else if (id == R.id.nav_assigned_notes) {
            NoteFragment fragment = new NoteFragment();
            fragment.SetType(Types.NoteView.VIEW_ASSIGNED);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
            fab.setVisibility(View.INVISIBLE);
            currentFragment = fragment;
        } else if (id == R.id.nav_shared_notes) {
            NoteFragment fragment = new NoteFragment();
            fragment.SetType(Types.NoteView.VIEW_SHARED);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
            fab.setVisibility(View.INVISIBLE);
            currentFragment = fragment;
        } else if (id == R.id.nav_recieved_notes) {
            NoteFragment fragment = new NoteFragment();
            fragment.SetType(Types.NoteView.VIEW_RECIEVED);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
            fab.setVisibility(View.INVISIBLE);
            currentFragment = fragment;
        } else if (id == R.id.nav_pending_notes) {
            NoteFragment fragment = new NoteFragment();
            fragment.SetType(Types.NoteView.VIEW_PENDING);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
            fab.setVisibility(View.INVISIBLE);
            currentFragment = fragment;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startAssignedNotesActivity() {
        NoteFragment fragment = new NoteFragment();
        fragment.SetType(Types.NoteView.VIEW_ASSIGNED);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
        fab.setVisibility(View.INVISIBLE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_notes).setVisible(false);
        nav_Menu.findItem(R.id.nav_friends).setVisible(false);
        nav_Menu.findItem(R.id.nav_find_friends).setVisible(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Preferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvalibility = GoogleApiAvailability.getInstance();
        int resultCode = apiAvalibility.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvalibility.isUserResolvableError(resultCode)) {
                apiAvalibility.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("MainActivity", "This device is not supported");
                finish();
            }
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
    }
}
