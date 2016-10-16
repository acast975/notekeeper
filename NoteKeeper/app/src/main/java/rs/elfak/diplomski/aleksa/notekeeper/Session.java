package rs.elfak.diplomski.aleksa.notekeeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

import java.net.MalformedURLException;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

/**
 * Created by aleks on 24.9.2016..
 */

public class Session {
    private SharedPreferences prefs;

    public static SocketIO socket;

    static {
        try {
            socket = new SocketIO("http://192.168.0.102:3000/");
            socket.connect(new IOCallback() {
                @Override
                public void onDisconnect() {

                }

                @Override
                public void onConnect() {
                    Log.w("Success", "Success connecting to server");
                }

                @Override
                public void onMessage(String s, IOAcknowledge ioAcknowledge) {

                }

                @Override
                public void onMessage(JSONObject jsonObject, IOAcknowledge ioAcknowledge) {

                }

                @Override
                public void on(String s, IOAcknowledge ioAcknowledge, Object... objects) {

                }

                @Override
                public void onError(SocketIOException e) {
                    Log.w("Error", "Error connectiong");
                }
            });
        }
        catch(MalformedURLException exception){

        }
    }

    public Session(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setUsername(String username) {
        prefs.edit().putString("username", username).apply();
    }

    public String getUsername() {
        String username = prefs.getString("username", "");
        return username;
    }

    public void setId(int id) {
        prefs.edit().putInt("id", id).apply();
    }

    public int getId() {
        return prefs.getInt("id", -1);
    }
}
