package rs.elfak.diplomski.aleksa.notekeeper.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by aleks on 19.9.2016..
 */
public class UserList {
    private static List<User> userList;
    private static UserList instance;

    private UserList() {
        userList = new ArrayList<User>();

        userList.add(new User(1, "aleksa993", "aleksa@mail.com", "password"));
        userList.add(new User(2, "kide", "dimcic@mail.com", "password"));
        userList.add(new User(3, "rogi", "igordj@mail.rs", "password"));
        userList.add(new User(4, "dragan", "draganstojanovic@mail.com", "password"));
        userList.add(new User(5, "stanimir", "stanimirovic@mail.com", "password"));
    }

    public static UserList getInstance() {
        if(instance == null)
            instance = new UserList();
        return instance;
    }

    public void add(User user){
        userList.add(user);
    }

    public void remove(int i) {
        userList.remove(i);
    }

    public User getUser(int i) {
        return userList.get(i);
    }

    public List<User> getList() {
        return userList;
    }
}
