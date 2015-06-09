package nl.mprog.scheduleus;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;

/**
 * Created by Paul on 9-6-2015.
 * Global application vars and connection with Parse.com are defined here
 */
public class Application extends android.app.Application {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Preferences
        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = prefs.edit();

        // Connect with Parse.com database
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "M32kF53NOX8ARR2z4lYaXsAbZMjkqgzvrG7WSXPC", "caYeMJrgizDJlAnZ30slp8d4yLTPjrpOscLFk2ik");
    }
}
