package rs.elfak.diplomski.aleksa.notekeeper.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aleks on 19.9.2016..
 */
public class TaskList {
    private List<Task> taskList;
    private static TaskList instance;

    private TaskList() {
        taskList = new ArrayList<Task>();
    }

    public static TaskList getInstance() {
        if(instance == null)
            instance = new TaskList();
        return instance;
    }

    public void add(Task task) {
        taskList.add(task);
    }

    public void remove(int i) {
        taskList.remove(i);
    }

    public Task get(int i) {
        return taskList.get(i);
    }

    public List<Task> getTaskList() {
        return taskList;
    }
}
