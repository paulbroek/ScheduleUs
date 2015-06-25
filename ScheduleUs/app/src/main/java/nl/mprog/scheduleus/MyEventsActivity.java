package nl.mprog.scheduleus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul Broek on 1-6-2015.
 * 10279741
 * pauliusbroek@hotmail.com
 * Activities that lists user involved events, a click will create event to SelectDaysActivity
 */
public class MyEventsActivity extends Activity {

    private TextView myEventsView;
    private ListView myEvents_ListView;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    ArrayList eventNameList;
    ArrayList EventsIdList;

    Application global;
    ProgressDialog dialog;

    Intent getSelectDaysScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        myEventsView = (TextView) findViewById(R.id.myEventsView);
        myEvents_ListView = (ListView) findViewById(R.id.myEvents_ListView);
        dialog = new ProgressDialog(MyEventsActivity.this);

        prefs = getSharedPreferences("nl.mprog.ScheduleUs", Context.MODE_PRIVATE);
        editor = prefs.edit();
        getSelectDaysScreen = new Intent(getApplicationContext(), SelectDaysActivity.class);
        global = (Application)getApplication();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        query.whereEqualTo("participants", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList,
                             ParseException e) {
                if (e == null && eventList.size() > 0) {
                    eventNameList = new ArrayList<String>();
                    EventsIdList = new ArrayList<>();

                    for (int i = 0; i < eventList.size(); i++) {
                        eventNameList.add("" + eventList.get(i).getString("event_name"));
                        EventsIdList.add(eventList.get(i).getObjectId());
                    }

                    global.setMyEventsMap(EventsIdList,eventNameList);

                    ArrayAdapter<String> adapter_events = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_white_text, R.id.list_content, eventNameList);
                    myEvents_ListView.setAdapter(adapter_events);
                    Log.d("score", "Retrieved " + eventList.size());
                } else {
                    if (e != null)
                        Log.d("event", "Error: " + e.getMessage());
                    else
                        myEventsView.setText("You are not enlisted in an event, create one!");
                }
            }
        });

        // Go to SelectDaysActivity to see shared selected days
        myEvents_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Set up a progress dialog
                String event_id = EventsIdList.get(position).toString();
                editor.putString("event_id", event_id).apply();
                getSelectDaysScreen.putExtra("calling_event_id", event_id);
                getEventData(event_id);
            }
        });
    }

    public void getEventData(String id) {
        // Show dialog while fetching Parse data
        dialog.setMessage(getString(R.string.progress_eventdata));
        dialog.show();

        // Clear the current map
        global.clearSharedAvailabilityMap();

        // Get the event with id from Parse
        final ParseObject CurrentEvent = new ParseObject("Events");
        ParseQuery<ParseObject> query_event = ParseQuery.getQuery("Events");
        query_event.whereEqualTo("objectId", id);

        // Now we can get all time slots for days that match the previous query and selected event id
        ParseQuery<ParseObject> query_timeslots = ParseQuery.getQuery("SharedTimes");
        query_timeslots.whereMatchesKeyInQuery("parent_event", "objectId", query_event);

        query_timeslots.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> timesObjectList,
                             ParseException e) {
                if (e == null) {
                    global.clearSharedTimesIdMap();
                    for (int obj_n = 0; obj_n < timesObjectList.size(); obj_n++) {

                        final String day = timesObjectList.get(obj_n).getString("Day");
                        global.addSharedTimesId(day, timesObjectList.get(obj_n).getObjectId());

                        JSONArray jsonArrayTimes = timesObjectList.get(obj_n).getJSONArray("Times");
                        ArrayList<int[]> timesList = new ArrayList<int[]>();

                        if (jsonArrayTimes != null) {
                            try {

                                JsonParser jsonParser = new JsonParser();
                                JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonArrayTimes.toString());
                                Gson googleJson = new Gson();

                                final ArrayList<ArrayList> doubleList = googleJson.fromJson(jsonArray, ArrayList.class);
                                for (int i = 0; i < doubleList.size(); i++) {
                                    ArrayList<Double> dList = doubleList.get(i);
                                    ArrayList intList = new ArrayList();
                                    for (int j = 0; j < dList.size(); j++) {
                                        intList.add(dList.get(j).intValue());
                                    }
                                    int[] intArray = Ints.toArray(intList);
                                    timesList.add(intArray);
                                }

                            } catch (Exception E) {
                                Toast.makeText(MyEventsActivity.this, "" + E.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        // For this day we can put the list of SharedTimes
                        global.putSharedAvailabilityList(day, timesList);
                    }

                    // All data retrieved, close dialog, write log and go to SelectDays
                    dialog.dismiss();
                    // Clear the current personal map
                    global.clearPersonalAvailabilityMap();
                    Log.d("times", "Retrieved times: " + timesObjectList.size());
                    startActivity(getSelectDaysScreen);

                // Data retrieval error
                } else
                    Log.d("times", "Error " + e.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.main:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.log_out:
                ParseUser.logOut();
                startActivity(new Intent(this, CheckLoginActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
