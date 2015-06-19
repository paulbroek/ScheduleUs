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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.lucasr.twowayview.TwoWayView;

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

            String calling_event_id = getIntent().getStringExtra("calling_event_id");
            String calling_event_name = global.getMyEventsMap().get(calling_event_id);
            Toast.makeText(SelectDaysActivity.this, "Came from MyEvents, extra: " + calling_event_name,
                    Toast.LENGTH_SHORT).show();

            days_textView.setText("These are the selected days for event " + calling_event_name);

            //dateSet = new HashSet<>(prefs.getStringSet("shared_event_dates", null));
            dateSet = new HashSet<String>();
            dayList = new ArrayList<String>();

            // Get shared event data from Parse
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
            query.whereEqualTo("objectId", calling_event_id);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> eventList,
                                 ParseException e) {
                    if (e == null) {
                        //myEventsView.setText("" + eventList.get(0).getObjectId());

                        JSONArray jsonArrayDates = eventList.get(0).getJSONArray("dates");
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

                        Log.d("score", "Retrieved " + eventList.size());
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }
            });

            // Now we can get all time slots for days in daySet
            /*ParseQuery<ParseObject> query_timeslots = ParseQuery.getQuery("AvailItems");
            query_timeslots.whereEqualTo("parent_event", calling_event_id);
            //query.fromLocalDatastore();
            query_timeslots.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> eventList,
                                 ParseException e) {
                    if (e == null) {
                        //myEventsView.setText("" + eventList.get(0).getObjectId());

                        JSONArray jsonArrayDates = eventList.get(0).getJSONArray("dates");
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

                        Log.d("score", "Retrieved " + eventList.size());
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }
            });*/

            //dayList = new ArrayList<String>(dateSet);

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

    // Click on a day, go to SelectTimesActivity for this day
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
}
