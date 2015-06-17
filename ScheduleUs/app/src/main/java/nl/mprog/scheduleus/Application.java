package nl.mprog.scheduleus;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Paul on 9-6-2015.
 * Global application vars and connection with Parse.com are defined here
 */
public class Application extends android.app.Application {

    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;
    public static List current_dateList;

    private int data=200;
    private Set<String> daySet;
    //private boolean[] availabilityList;
    private Map<String, boolean[]> availabilityMap;

    /*public List<boolean[]> getAvailabilityList() {
        return this.availabilityList;
    }*/

    public void putAvailabilityArray(String day, boolean[] a) {
        this.daySet.add(day);
        //this.availabilityList = l;
        this.availabilityMap.put(day, a);
    }

    public boolean[] getAvailabilityArray(String day) {
        return availabilityMap.get(day);
    }

    public Map<String, boolean[]> getAvailabilityMap() {
        return this.availabilityMap;
    }

    public void setAvailabilityMap(Map<String, boolean[]> m) {
        this.availabilityMap = m;
    }

    public int getData(){
        return this.data;
    }

    public void setData(int i){
        this.data = i;
    }

    public Application() {
        this.availabilityMap = new HashMap<String, boolean[]>();
        this.daySet = new HashSet<String>();
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
