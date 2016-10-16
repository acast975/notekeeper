package rs.elfak.diplomski.aleksa.notekeeper.model;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by aleks on 17.9.2016..
 */
public class Note {
    int id;

    private String title;
    private Timestamp timeCreated;
    private int userId; //creator
    private Integer assignedTo;

    //temporary
    public User user;

    public Note(int id, String title, Timestamp timeCreated, int userId, Integer assignedTo, User user){
        this.id = id;
        this.title = title;
        this.timeCreated = timeCreated;
        this.userId = userId;
        this.assignedTo = assignedTo;
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
