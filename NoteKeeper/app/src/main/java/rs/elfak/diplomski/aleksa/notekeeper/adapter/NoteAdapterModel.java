package rs.elfak.diplomski.aleksa.notekeeper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.model.NoteList;

/**
 * Created by aleks on 18.9.2016..
 */
public class NoteAdapterModel extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView body;
    private View v;
    private TextView additionalText;

    public NoteAdapterModel(View v) {
        super(v);
        this.v = v;

        title = (TextView) v.findViewById(R.id.listItemnNoteTitle);
        body = (TextView) v.findViewById(R.id.listItemnNoteBody);
        additionalText = (TextView) v.findViewById(R.id.noteText);
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public TextView getBody() {
        return body;
    }

    public void setBody(TextView body) {
        this.body = body;
    }

    public TextView getAdditionalText() {
        return additionalText;
    }

    public View getV() {
        return v;
    }
}
