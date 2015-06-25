package nl.mprog.scheduleus;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseObject;

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

    private String current_event_name;
    private Set<String> personal_daySet;
    private Set<String> shared_daySet;

    private Map<String, ArrayList<int[]>> personal_availabilityMap;
    private Map<String, ArrayList<int[]>> shared_availabilityMap;
    private Map<String, String> myEventsMap;

    private Map<String,String> SharedTimesIdMap;

    public void addSharedTimesId(String day, String id) {
        this.SharedTimesIdMap.put(day, id);
    }

    public String getSharedTimesId(String day) {
        return this.SharedTimesIdMap.get(day);
    }

    public void clearSharedTimesIdMap() {
        this.SharedTimesIdMap.clear();
    }

    public void putPersonalDaySet(Set<String> s) {
        this.personal_daySet = s;
    }

    public void putPersonalAvailabilityList(String day, ArrayList<int[]> l) {
        this.personal_daySet.add(day);
        this.personal_availabilityMap.put(day, l);
    }

    public ArrayList<int[]> getPersonalAvailabilityList(String day) {
        return personal_availabilityMap.get(day);
    }

    public void clearPersonalAvailabilityMap() {
        this.personal_availabilityMap.clear();
        this.personal_daySet.clear();
    }

    public void putSharedAvailabilityList(String day, ArrayList<int[]> l) {
        this.shared_daySet.add(day);
        this.shared_availabilityMap.put(day, l);
    }

    public void removeDay(String day) {
        this.personal_daySet.remove(day);
        this.personal_availabilityMap.remove(day);
    }

    public Map<String, ArrayList<int[]>> getPersonalAvailabilityMap() {
        return this.personal_availabilityMap;
    }

    public Map<String, ArrayList<int[]>> getSharedAvailabilityMap() {
        return this.shared_availabilityMap;
    }

    public void clearSharedAvailabilityMap() {
        this.shared_availabilityMap.clear();
        this.shared_daySet.clear();
    }

    public Map<String, String> getMyEventsMap() { return this.myEventsMap; }

    public void setMyEventsMap(ArrayList<String> k, ArrayList<String> v) {
        for (int i = 0; i < k.size(); i++) {
            this.myEventsMap.put(k.get(i),v.get(i));
        }
    }

    public Set<String> getPersonalDaySet() {return this.personal_daySet;
    }

    public Set<String> getSharedDaySet() {return this.shared_daySet;
    }
    public String getCurrentEventName(){
        return this.current_event_name;
    }

    public void setCurrentEventName(String i){
        this.current_event_name = i;
    }

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.personal_availabilityMap = new HashMap<String, ArrayList<int[]>>();
        this.shared_availabilityMap = new HashMap<String, ArrayList<int[]>>();
        this.myEventsMap = new HashMap<>();
        this.SharedTimesIdMap = new HashMap<>();
        this.personal_daySet = new HashSet<String>();
        this.shared_daySet = new HashSet<String>();
        this.current_event_name = "";

        // Preferences
        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = prefs.edit();

        // Connect with Parse.com database
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "M32kF53NOX8ARR2z4lYaXsAbZMjkqgzvrG7WSXPC", "caYeMJrgizDJlAnZ30slp8d4yLTPjrpOscLFk2ik");
    }

}
