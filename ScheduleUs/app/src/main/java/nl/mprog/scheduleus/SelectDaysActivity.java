package nl.mprog.scheduleus;

import nl.mprog.scheduleus.dayListAdapter.customButtonListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectDaysActivity extends ActionBarActivity implements customButtonListener{

    private TextView days_textView;
    private Button select_timeButton;
    private TwoWayView twListView;

    private dayListAdapter dayList_adapter;
    private shared_dayListAdapter shared_dayListAdapter;
    ArrayList<String> dayList;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private Set<String> dateSet;
    private String eventName;
    private String selected_day;

    Application global;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_days);

        days_textView = (TextView) findViewById(R.id.days_textView);
        select_timeButton = (Button) findViewById(R.id.select_timeButton);
        twListView = (TwoWayView) findViewById(R.id.lvItems);

        global = (Application) getApplication();


        final Intent getSelectTimesScreen = new Intent(this, SelectTimesActivity.class);
        final Intent getInviteScreen = new Intent(this, InviteActivity.class);
        final Intent getMyEventsScreen = new Intent(this, MyEventsActivity.class);

        prefs = getSharedPreferences("nl.mprog.ScheduleUs", Context.MODE_PRIVATE);
        editor = prefs.edit();

        if  (getIntent().hasExtra("calling_event_id")) {
            // Intent from MyEventsActivity, a user wants to view the shared selected days

            final String calling_event_id = getIntent().getStringExtra("calling_event_id");
            String calling_event_name = global.getMyEventsMap().get(calling_event_id);
            Toast.makeText(SelectDaysActivity.this, "Came from MyEvents, extra: " + calling_event_name + " " + calling_event_id,
                    Toast.LENGTH_SHORT).show();

            days_textView.setText("These are the selected days for event " + calling_event_name);

            //dateSet = new HashSet<>(prefs.getStringSet("shared_event_dates", null));
            dateSet = new HashSet<String>();
            dayList = new ArrayList<String>();

            // Get shared event dates from Parse
            ArrayList<ParseObject> event_list = new ArrayList<>();
            ParseQuery<ParseObject> query_eventdates = ParseQuery.getQuery("Events");
            query_eventdates.whereEqualTo("objectId", calling_event_id);

            query_eventdates.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject eventObject,
                                 ParseException e) {
                    if (e == null) {

                        JSONArray jsonArrayDates = eventObject.getJSONArray("dates");
                        ParseObject test = eventObject;
                        //event_list.add(test);
                        if (jsonArrayDates != null)
                            for (int i = 0; i < jsonArrayDates.length(); i++) {
                                try {
                                    dayList.add(jsonArrayDates.get(i).toString());

                                } catch (JSONException E) {
                                    Toast.makeText(SelectDaysActivity.this, "" + E.toString(),
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        //dayList = new ArrayList<String>(eventList.get(0).getJSONArray("dates"));

                        dateSet = new HashSet<String>(dayList);
                        global.putSharedDaySet(dateSet);
                        //event = eventList.get(0);

                        Log.d("event", "Retrieved");
                    } else {
                        Log.d("event", "Eventlist error: " + e.getMessage());
                    }
                }
            });

            /*while ( dateSet != null) {
                try {
                    wait(200);
                }
                catch (InterruptedException E){
                    Toast.makeText(SelectDaysActivity.this, "" + E.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }*/


            final String day = "20-05-2015";
            // Now we can get all time slots for days in daySet
            ParseQuery<ParseObject> query_timeslots = ParseQuery.getQuery("SharedTimes");
            Toast.makeText(SelectDaysActivity.this, "" + event_list.size(),
                    Toast.LENGTH_SHORT).show();
            if (event_list.size() > 0)
                query_timeslots.whereEqualTo("parent_event", event_list.get(0));
            //query_timeslots.whereEqualTo("Initiator", ParseUser.getCurrentUser());

            // TIJDELIJK WORDT ALLEEN DE EERSTE DAG GEPAKT, DIT MOET FOR LOOP WORDEN
            query_timeslots.whereEqualTo("Day", day);
            query_timeslots.whereMatchesKeyInQuery("parent_event","objectId",query_eventdates);
            query_timeslots.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> timesObjectList,
                                 ParseException e) {
                    if (e == null) {
                        JSONArray jsonArrayTimes = timesObjectList.get(0).getJSONArray("Times");
                        ArrayList<int[]> tList = new ArrayList<int[]>();

                        if (jsonArrayTimes != null) {
                            try {

                                Toast.makeText(SelectDaysActivity.this, jsonArrayTimes.toString(),
                                        Toast.LENGTH_LONG).show();


                                JsonParser jsonParser = new JsonParser();
                                JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonArrayTimes.toString());
                                Gson googleJson = new Gson();

                                //tList = googleJson.fromJson(jsonArray, ArrayList.class);
                                final ArrayList<ArrayList> doubleList = googleJson.fromJson(jsonArray, ArrayList.class);
                                Double test = (Double) doubleList.get(0).get(0);
                                for (int i = 0; i < doubleList.size(); i++) {
                                    ArrayList <Double> dList = doubleList.get(i);
                                    ArrayList intList = new ArrayList();
                                    for (int j = 0; j < dList.size(); j++) {
                                        intList.add(dList.get(j).intValue());
                                    }
                                    int[] intArray = Ints.toArray(intList);
                                    tList.add(intArray);
                                }

                                Toast.makeText(SelectDaysActivity.this, tList.get(2)[2] + "",
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception E) {
                                Toast.makeText(SelectDaysActivity.this, "" + E.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                        global.putSharedAvailabilityList("201-05-2015", tList);

                        Log.d("times", "Retrieved times: " + timesObjectList.size());
                    } else {
                        Log.d("times", "Error, times: " + e.getMessage());

                    }
                }
            });


            int[] slot1 = {9,0,10,0};
            int[] slot2 = {12,15,15,30};
            ArrayList<int[]> test_list = new ArrayList<>();
            test_list.add(slot1);
            test_list.add(slot2);
            global.putSharedAvailabilityList("hoi", test_list);
            dayList = new ArrayList<String>(dateSet);
            dayList.add("hoi");
            //dayList.add("20-05-2015");

            shared_dayListAdapter = new shared_dayListAdapter(this, dayList, global.getSharedAvailabilityMap());
            twListView.setAdapter(shared_dayListAdapter);


            select_timeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(getMyEventsScreen);
                }
            });


        }
        // Intent directly from MainActivity, an initiator is creating a new event
        else {
            eventName = prefs.getString("event_name", null);
            dateSet = new HashSet<>(prefs.getStringSet("event_dates", null));

            dayList = new ArrayList<String>(dateSet);


            dayList_adapter = new dayListAdapter(this, dayList, global.getPersonalAvailabilityMap());
            dayList_adapter.setCustomButtonListener(SelectDaysActivity.this);
            twListView.setAdapter(dayList_adapter);


            select_timeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(getInviteScreen);
                }
            });
        }




    }

    int[] toIntArray(List<Double> list)  {
        int[] ret = new int[list.size()];
        int i = 0;
        for (Double d : list)
            ret[i++] = d.intValue();
        return ret;
    }

    // Gives shared available times, taken from personal user times
    public ArrayList<int[]> CompareTimes(List<ArrayList<int[]>> L) {

        int n_hours = 15;
        int start = 9;
        int end = 24;
        ArrayList<int[]> result = new ArrayList<int[]>();
        for (int hour = start; hour < n_hours; hour++) {

            Boolean time_is_in_all_lists = true;

            for (int person = 0; person < L.size(); person++) {

                Boolean time_is_in_his_list = false;

                for (int y = 0; y < L.get(person).size(); y++) {
                    if (L.get(person).get(y)[0] >= hour & L.get(person).get(y)[3] <= hour+1)
                        time_is_in_his_list = true;
                }
                if (!time_is_in_his_list)
                    time_is_in_all_lists = false;

            }

            int[] temp = {hour,0,hour+1,0};
            if (time_is_in_all_lists)
                result.add(temp);


        }

        return result;
    }
    // Deleting a day, so adapter, global and preferences need an update
    @Override
    public void onButtonClickListener(int position, String value) {

        global.removeDay(dayList.get(position));
        dayList.remove(position);
        dateSet = new HashSet(dayList);
        editor.putStringSet("event_dates", dateSet).apply();
        dayList_adapter = new dayListAdapter(this, dayList, global.getPersonalAvailabilityMap());
        dayList_adapter.setCustomButtonListener(SelectDaysActivity.this);
        twListView.setAdapter(dayList_adapter);

        Toast.makeText(SelectDaysActivity.this, "Day " + value + " deleted",
                Toast.LENGTH_SHORT).show();
    }

    // Clicked a day, go to SelectTimesActivity for this day
    @Override
    public void onViewClickListener(int position, String value) {

        selected_day = value;
        editor.putString("selected_day", selected_day).apply();
        final Intent getSelectTimesScreen = new Intent(this, SelectTimesActivity.class);
        startActivity(getSelectTimesScreen);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_days, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (id) {
            case R.id.main:
                startActivity(new Intent(this, MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    class Data {
        private String title;
        private Long id;
        private Boolean children;
        private List<Data> groups;

        public String getTitle() { return title; }
        public Long getId() { return id; }
        public Boolean getChildren() { return children; }
        public List<Data> getGroups() { return groups; }

        public void setTitle(String title) { this.title = title; }
        public void setId(Long id) { this.id = id; }
        public void setChildren(Boolean children) { this.children = children; }
        public void setGroups(List<Data> groups) { this.groups = groups; }

        public String toString() {
            return String.format("title:%s,id:%d,children:%s,groups:%s", title, id, children, groups);
        }
    }
}
