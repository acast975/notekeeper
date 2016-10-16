package rs.elfak.diplomski.aleksa.notekeeper.model;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by aleks on 17.9.2016..
 */
public class Task {
    int id;
    private String text;
    private int noteId;
    private Timestamp deadline;
    private boolean checked;


    //Temporary
    Note note;

    public Task(String text, int noteId, Timestamp deadline, boolean checked, Note note) {
        this.text = text;
        this.noteId = noteId;
        this.deadline = deadline;
        this.checked = checked;
        this.note = note;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
