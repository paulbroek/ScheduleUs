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

    private String current_user_name;
    private String current_event_name;
    private Set<String> daySet;

    private Map<String, ArrayList<int[]>> availabilityMap;

    public void putDaySet(Set<String> s) {
        this.daySet = s;
    }

    public void putAvailabilityList(String day, ArrayList<int[]> l) {
        this.daySet.add(day);
        this.availabilityMap.put(day, l);
    }

    public void removeDay(String day) {
        this.daySet.remove(day);
        this.availabilityMap.remove(day);
    }

    public ArrayList<int[]> getAvailabilityList(String day) {
        return availabilityMap.get(day);
    }

    public Map<String, ArrayList<int[]>> getAvailabilityMap() {
        return this.availabilityMap;
    }

    public void setAvailabilityMap(Map<String, ArrayList<int[]>> m) {
        this.availabilityMap = m;
    }

    public Set<String> getDaySet() {return this.daySet;
    }
    public String getCurrentEventName(){
        return this.current_event_name;
    }

    public void setCurrentEventName(String i){
        this.current_event_name = i;
    }

    public String getCurrentUserName(){
        return this.current_user_name;
    }

    public void setCurrentUserName(String i){
        this.current_user_name = i;
    }

    public Application() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.availabilityMap = new HashMap<String, ArrayList<int[]>>();
        this.daySet = new HashSet<String>();
        this.current_event_name = "";

        // Preferences
        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = prefs.edit();

        // Connect with Parse.com database
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "M32kF53NOX8ARR2z4lYaXsAbZMjkqgzvrG7WSXPC", "caYeMJrgizDJlAnZ30slp8d4yLTPjrpOscLFk2ik");
    }

}
