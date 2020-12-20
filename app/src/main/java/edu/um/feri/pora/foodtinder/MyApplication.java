package edu.um.feri.pora.foodtinder;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import edu.um.feri.pora.lib.User;

public class MyApplication extends Application {
    private User user;

    public MyApplication() {
        //FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
