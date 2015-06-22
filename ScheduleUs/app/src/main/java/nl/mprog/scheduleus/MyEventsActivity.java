package nl.mprog.scheduleus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyEventsActivity extends ActionBarActivity {

    private TextView myEventsView;
    private ListView myEvents_ListView;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    ArrayList eventNameList;
    ArrayList EventsIdList;
    ArrayList EventsPointerList;

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

        Intent activityThatCalled = getIntent();
        getSelectDaysScreen = new Intent(getApplicationContext(), SelectDaysActivity.class);
        global = (Application)getApplication();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        query.whereEqualTo("participants", ParseUser.getCurrentUser().getUsername());
        //query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList,
                             ParseException e) {
                if (e == null && eventList.size() > 0) {
                    myEventsView.setText("" + eventList.get(0).getObjectId());
                    eventNameList = new ArrayList<String>();
                    EventsIdList = new ArrayList<>();

                    for (int i = 0; i < eventList.size(); i++) {
                        eventNameList.add("" + eventList.get(i).getString("event_name"));
                        EventsIdList.add(eventList.get(i).getObjectId());
                    }

                    global.setMyEventsMap(EventsIdList,eventNameList);

                    ArrayAdapter<String> adapter_events = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_black_text, R.id.list_content, eventNameList);
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

                // Hier of pas in SelectDays ophalen van dagenlijst?
                //editor.putStringSet("shared_event_dates", ).apply();
                // Set up a progress dialog
                getSelectDaysScreen.putExtra("calling_event_id", EventsIdList.get(position).toString());
                getEventData(EventsIdList.get(position).toString());




                //if (!dialog.isShowing())

            }
        });
    }

    public void getEventData(String id) {
        // Show dialog while fetching Parse data
        dialog.setMessage(getString(R.string.progress_eventdata));
        dialog.show();

        // Clear the current map
        global.clearSharedAvailabilityMap();

        // Get shared event dates from Parse
        ParseQuery<ParseObject> query_eventdates = ParseQuery.getQuery("Events");
        query_eventdates.whereEqualTo("objectId", id);

        // Now we can get all time slots for days that match the previous query and selected event id
        ParseQuery<ParseObject> query_timeslots = ParseQuery.getQuery("SharedTimes");
        query_timeslots.whereMatchesKeyInQuery("parent_event", "objectId", query_eventdates);

        query_timeslots.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> timesObjectList,
                             ParseException e) {
                if (e == null) {
                    ArrayList<String> dayList = new ArrayList<String>();
                    for (int obj_n = 0; obj_n < timesObjectList.size(); obj_n++) {

                        final String day = timesObjectList.get(obj_n).getString("Day");
                        JSONArray jsonArrayTimes = timesObjectList.get(obj_n).getJSONArray("Times");
                        ArrayList<int[]> timesList = new ArrayList<int[]>();

                        if (jsonArrayTimes != null) {
                            try {

                                /*Toast.makeText(MyEventsActivity.this, jsonArrayTimes.toString(),
                                        Toast.LENGTH_LONG).show();*/

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
                                    //int[] intArray = {9,0,10,0};
                                    timesList.add(intArray);
                                }

                            } catch (Exception E) {
                                Toast.makeText(MyEventsActivity.this, "" + E.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        // For this day we can put the list of SharedTimes
                        global.putSharedAvailabilityList(day, timesList);
                        //dayList.add(day);
                        /*Toast.makeText(MyEventsActivity.this, "" + day,
                                Toast.LENGTH_SHORT).show();*/
                    }

                    // All data retrieved, close dialog, write log and go to SelectDays
                    dialog.dismiss();
                    Log.d("times", "Retrieved times: " + timesObjectList.size());
                    //Set sharedDaySet = new HashSet(dayList);
                    //global.putSharedDaySet(sharedDaySet);
                    startActivity(getSelectDaysScreen);


                } else {
                    // Data retrieval error
                    Log.d("times", "Error, times: " + e.getMessage());
                }
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
