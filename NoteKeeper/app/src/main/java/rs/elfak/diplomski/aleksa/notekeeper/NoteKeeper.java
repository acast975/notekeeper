package rs.elfak.diplomski.aleksa.notekeeper;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by aleks on 24.9.2016..
 */

public class NoteKeeper extends Application {
    public Session session = new Session(this);
}
