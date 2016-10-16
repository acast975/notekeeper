package rs.elfak.diplomski.aleksa.notekeeper.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.Session;
import rs.elfak.diplomski.aleksa.notekeeper.adapter.FindFriendsAdapter;
import rs.elfak.diplomski.aleksa.notekeeper.adapter.FriendAdapter;
import rs.elfak.diplomski.aleksa.notekeeper.adapter.PendingFriendsAdapter;
import rs.elfak.diplomski.aleksa.notekeeper.adapter.ShareAdapter;
import rs.elfak.diplomski.aleksa.notekeeper.adapter.SharedAdapter;
import rs.elfak.diplomski.aleksa.notekeeper.http.HttpConnector;
import rs.elfak.diplomski.aleksa.notekeeper.http.UserVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.model.User;
import rs.elfak.diplomski.aleksa.notekeeper.type.Types;

/**
 * Created by aleks on 18.9.2016..
 */
public class FriendsFragment extends Fragment implements SearchableFilter {
    Types.UserView type = Types.UserView.VIEW_FRIENDS;

    private int noteId;
    private String filter;

    public void setType(Types.UserView type) {
        this.type = type;
    }

    @Override
    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        final RecyclerView recList = (RecyclerView) view.findViewById(R.id.cardList);

        if (type == Types.UserView.VIEW_FRIENDS) {
            if (filter == null)
                HttpConnector.getFriends((new Session(getActivity())).getId(), getActivity(), new UserVolleyCallback() {
                    @Override
                    public void success(List<User> users) {
                        FriendAdapter adapter = new FriendAdapter(FriendsFragment.this.getActivity(), users);
                        recList.setAdapter(adapter);
                        GridLayoutManager llm = new GridLayoutManager(getActivity(), 2);
                        recList.setLayoutManager(llm);
                    }

                    @Override
                    public void error(String message) {
                        if (message != null) {
                            Toast.makeText(FriendsFragment.this.getActivity(), message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            else HttpConnector.getFriendsFiltered((new Session(getActivity())).getId(), filter, getActivity(), new UserVolleyCallback() {
                @Override
                public void success(List<User> users) {
                    FriendAdapter adapter = new FriendAdapter(FriendsFragment.this.getActivity(), users);
                    recList.setAdapter(adapter);
                    GridLayoutManager llm = new GridLayoutManager(getActivity(), 2);
                    recList.setLayoutManager(llm);
                }

                @Override
                public void error(String message) {
                    if (message != null) {
                        Toast.makeText(FriendsFragment.this.getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (type == Types.UserView.VIEW_SEARCH) {
            if (filter == null)
                HttpConnector.getUsers((new Session(getActivity())).getId(), getActivity(), new UserVolleyCallback() {
                    @Override
                    public void success(List<User> users) {
                        final FindFriendsAdapter adapter = new FindFriendsAdapter(getActivity(), users);
                        recList.setAdapter(adapter);
                        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                        recList.setLayoutManager(llm);
                    }

                    @Override
                    public void error(String message) {
                        if (message != null) {
                            Toast.makeText(FriendsFragment.this.getActivity(), message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            else
                HttpConnector.getUsersFiltered((new Session(getActivity())).getId(), filter, getActivity(), new UserVolleyCallback() {
                    @Override
                    public void success(List<User> users) {
                        final FindFriendsAdapter adapter = new FindFriendsAdapter(getActivity(), users);
                        recList.setAdapter(adapter);
                        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                        recList.setLayoutManager(llm);
                    }

                    @Override
                    public void error(String message) {
                        if (message != null) {
                            Toast.makeText(FriendsFragment.this.getActivity(), message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        } else if (type == Types.UserView.VIEW_PENDING) {
            if (filter == null)
                HttpConnector.getPendingRequests((new Session(getActivity())).getId(), getActivity(), new UserVolleyCallback() {
                    @Override
                    public void success(List<User> users) {
                        PendingFriendsAdapter probe = new PendingFriendsAdapter(getActivity(), users);
                        recList.setAdapter(probe);
                        GridLayoutManager llm = new GridLayoutManager(getActivity(), 2);
                        recList.setLayoutManager(llm);
                    }

                    @Override
                    public void error(String message) {

                    }
                });
            else HttpConnector.getPendingRequestsFiltered((new Session(getActivity())).getId(), filter, getActivity(), new UserVolleyCallback() {
                @Override
                public void success(List<User> users) {
                    PendingFriendsAdapter probe = new PendingFriendsAdapter(getActivity(), users);
                    recList.setAdapter(probe);
                    GridLayoutManager llm = new GridLayoutManager(getActivity(), 2);
                    recList.setLayoutManager(llm);
                }

                @Override
                public void error(String message) {

                }
            });
        } else if (type == Types.UserView.VIEW_SHARED) {
            HttpConnector.getUsersForShare(noteId, (new Session(getActivity())).getId(), getActivity(), new UserVolleyCallback() {
                @Override
                public void success(List<User> users) {
                    SharedAdapter adapter = new SharedAdapter(getActivity(), users, noteId);
                    recList.setAdapter(adapter);
                    GridLayoutManager llm = new GridLayoutManager(getActivity(), 2);
                    recList.setLayoutManager(llm);
                }

                @Override
                public void error(String message) {

                }
            });
        } else if (type == Types.UserView.VIEW_SHARE) {
            HttpConnector.getSharedWithUsers(noteId, (new Session(getActivity())).getId(), getActivity(), new UserVolleyCallback() {
                @Override
                public void success(List<User> users) {
                    ShareAdapter adapter = new ShareAdapter(getActivity(), users, noteId);
                    recList.setAdapter(adapter);
                    GridLayoutManager llm = new GridLayoutManager(getActivity(), 2);
                    recList.setLayoutManager(llm);
                }

                @Override
                public void error(String message) {

                }
            });
        }

        return view;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }
}
