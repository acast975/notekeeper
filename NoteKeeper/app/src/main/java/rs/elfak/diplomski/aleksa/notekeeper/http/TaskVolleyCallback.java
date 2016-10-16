package rs.elfak.diplomski.aleksa.notekeeper.http;

import java.util.List;

import rs.elfak.diplomski.aleksa.notekeeper.model.Task;

/**
 * Created by aleks on 24.9.2016..
 */

public interface TaskVolleyCallback extends VolleyCallback {
    public void success(List<Task> taskList);
    public void success(int id);
}
