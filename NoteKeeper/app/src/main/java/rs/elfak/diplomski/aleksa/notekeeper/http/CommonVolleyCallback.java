package rs.elfak.diplomski.aleksa.notekeeper.http;

/**
 * Created by aleks on 23.9.2016..
 */

public interface CommonVolleyCallback extends VolleyCallback {
    void success();
    void error(String message);
}
