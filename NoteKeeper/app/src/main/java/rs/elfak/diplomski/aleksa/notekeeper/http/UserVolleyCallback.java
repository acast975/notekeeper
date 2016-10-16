package rs.elfak.diplomski.aleksa.notekeeper.http;

import java.util.List;

import rs.elfak.diplomski.aleksa.notekeeper.model.User;

/**
 * Created by aleks on 24.9.2016..
 */

public interface UserVolleyCallback extends VolleyCallback {
    void success(List<User> users);
}
