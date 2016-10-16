package rs.elfak.diplomski.aleksa.notekeeper.type;

/**
 * Created by aleks on 20.9.2016..
 */
public class Types {
    public enum NoteView {
        VIEW_STANDARD, VIEW_SHARED, VIEW_ASSIGNED, VIEW_RECIEVED, VIEW_SHARED_FRIEND, VIEW_ASSIGNED_FRIEND, VIEW_RECIEVED_FRIEND,
        VIEW_PENDING
    }

    public enum UserView {
        VIEW_FRIENDS, VIEW_SEARCH, VIEW_PENDING, VIEW_SHARE, VIEW_SHARED
    }
}
