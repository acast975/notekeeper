package rs.elfak.diplomski.aleksa.notekeeper.http;

import java.util.List;

import rs.elfak.diplomski.aleksa.notekeeper.model.Note;

/**
 * Created by aleks on 24.9.2016..
 */

public interface NoteVolleyCallback extends VolleyCallback {
    void success(List<Note> noteList);
    void success(int insertId);
}
