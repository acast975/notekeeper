package rs.elfak.diplomski.aleksa.notekeeper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class InternetStateChangedReceiver extends BroadcastReceiver {
    public InternetStateChangedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Working!", Toast.LENGTH_SHORT).show();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if(isConnected) {
            Toast.makeText(context, "You've got internet!", Toast.LENGTH_SHORT).show();
            Log.w("Yeah!", "You've got internet!");
        }
    }
}
