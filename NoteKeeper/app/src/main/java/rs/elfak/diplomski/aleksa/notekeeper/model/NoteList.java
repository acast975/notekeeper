package rs.elfak.diplomski.aleksa.notekeeper.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aleks on 19.9.2016..
 */
public class NoteList {
    List<Note> noteList;
    private static NoteList instance;

    private NoteList() {
        noteList = new ArrayList<Note>();

    }

    public static NoteList getInstance() {
        if(instance == null)
            instance = new NoteList();
        return instance;
    }

    public void add(Note note) {
        noteList.add(note);
    }

    public void remove(int i) {
        noteList.remove(i);
    }

    public Note get(int i) {
        return noteList.get(i);
    }

    public List<Note> getList() {
        return noteList;
    }
}
