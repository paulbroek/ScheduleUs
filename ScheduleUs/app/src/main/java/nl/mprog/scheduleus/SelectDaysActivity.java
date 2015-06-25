package nl.mprog.scheduleus;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Paul Broek on 1-6-2015.
 * 10279741
 * pauliusbroek@hotmail.com
 * Activities that shows an overview of selected days and their filled in times.
 */
public class SelectDaysActivity extends Activity {

    private TextView days_textView;
    private Button select_timeButton;
    private TwoWayView twListView;

    private dayListAdapter dayList_adapter;
    private shared_dayListAdapter shared_dayListAdapter;
    ArrayList<String> dayList;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private Set<String> dateSet;
    private String selected_day;

    ProgressDialog dialog;
    Application global;

    class PersonalCustomListener implements ButtonListener {

        // Deleting a day, so adapter, global and preferences need an update
        @Override
        public void onButtonClickListener(int position, String value) {
            global.removeDay(dayList.get(position));
            dayList.remove(position);
            dateSet = new HashSet(dayList);
            editor.putStringSet("event_dates", dateSet).apply();
            dayList_adapter = new dayListAdapter(SelectDaysActivity.this, dayList, global.getPersonalAvailabilityMap());
            dayList_adapter.setCustomButtonListener(new PersonalCustomListener());
            twListView.setAdapter(dayList_adapter);

            Toast.makeText(SelectDaysActivity.this, "Day " + value + " deleted",
                    Toast.LENGTH_SHORT).show();
        }

        // Clicked a day, go to SelectTimesActivity for this day
        @Override
        public void onViewClickListener(int position, String value) {

            selected_day = value;
            editor.putString("selected_day", selected_day).apply();
            final Intent getSelectTimesScreen = new Intent(SelectDaysActivity.this, SelectTimesActivity.class);
            startActivity(getSelectTimesScreen);
        }
    }

    class SharedCustomListener implements ButtonListener {

        // Deleting a day, so adapter, global and preferences need an update
        @Override
        public void onButtonClickListener(int position, String value) {

            global.removeDay(dayList.get(position));
            dayList.remove(position);
            dateSet = new HashSet(dayList);
            editor.putStringSet("event_dates", dateSet).apply();
            shared_dayListAdapter = new shared_dayListAdapter(SelectDaysActivity.this, dayList, global.getPersonalAvailabilityMap(), global.getSharedAvailabilityMap());
            shared_dayListAdapter.setCustomButtonListener(new SharedCustomListener());
            twListView.setAdapter(shared_dayListAdapter);

            Toast.makeText(SelectDaysActivity.this, "Day " + value + " deleted",
                    Toast.LENGTH_SHORT).show();
        }

        // Clicked a day, go to SelectTimesActivity for this day
        @Override
        public void onViewClickListener(int position, String value) {
            selected_day = value;
            editor.putString("selected_day", selected_day).apply();
            final Intent getSelectTimesScreen = new Intent(SelectDaysActivity.this, SelectTimesActivity.class);
            startActivity(getSelectTimesScreen);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_days);

        days_textView = (TextView) findViewById(R.id.days_textView);
        select_timeButton = (Button) findViewById(R.id.select_timeButton);
        twListView = (TwoWayView) findViewById(R.id.lvItems);

        dialog = new ProgressDialog(SelectDaysActivity.this);
        global = (Application) getApplication();

        final Intent getInviteScreen = new Intent(this, InviteActivity.class);

        prefs = getSharedPreferences("nl.mprog.ScheduleUs", Context.MODE_PRIVATE);
        editor = prefs.edit();

        if (getIntent().hasExtra("calling_event_id")) {
            // Intent from MyEventsActivity, a user wants to view the shared selected days

            final String calling_event_id = getIntent().getStringExtra("calling_event_id");
            String calling_event_name = global.getMyEventsMap().get(calling_event_id);
            Toast.makeText(SelectDaysActivity.this, "Came from MyEvents, extra: " + calling_event_name + " " + calling_event_id,
                    Toast.LENGTH_SHORT).show();


            days_textView.setText("These are the selected days for event " + calling_event_name + ". The green blocks show when other participants of this event are available. Please click on one of them to enter your availability data.");

            dayList = new ArrayList<String>(global.getSharedDaySet());
            Toast.makeText(SelectDaysActivity.this, "" + dayList.size(),
                    Toast.LENGTH_SHORT).show();
            shared_dayListAdapter = new shared_dayListAdapter(getApplicationContext(), dayList, global.getPersonalAvailabilityMap(), global.getSharedAvailabilityMap());

            shared_dayListAdapter.setCustomButtonListener(new SharedCustomListener());
            twListView.setAdapter(shared_dayListAdapter);

            Toast.makeText(SelectDaysActivity.this, global.getSharedDaySet().toString(),
                    Toast.LENGTH_LONG).show();

            select_timeButton.setText("Merge data with cloud");

            select_timeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Show dialog while merging data with database
                    dialog.setMessage(getString(R.string.progress_eventdata));
                    dialog.show();

                    // Put al day information for current user
                    for (String day : global.getPersonalDaySet()) {
                        try {
                            JSONArray temp = new JSONArray(global.getPersonalAvailabilityList(day));

                            ParseObject AvailItem = new ParseObject("AvailItems");

                            AvailItem.put("User", ParseUser.getCurrentUser());
                            AvailItem.put("parent_event", ParseObject.createWithoutData("Events", calling_event_id));
                            AvailItem.put("Day", day);
                            AvailItem.put("Times", temp);
                            AvailItem.put("SharedTime", ParseObject.createWithoutData("SharedTimes",global.getSharedTimesId(day)));

                            AvailItem.saveInBackground();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } catch ( Exception e) {
                            Toast.makeText(SelectDaysActivity.this, "json excep, " + day,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    dialog.dismiss();
                }
            });


        }
        // Intent directly from MainActivity, an initiator is creating a new event
        else {
            dateSet = new HashSet<>(prefs.getStringSet("event_dates", null));

            // Make sure event_id is null
            editor.putString("event_id", null).apply();
            select_timeButton.setText("Select participants");
            dayList = new ArrayList<String>(dateSet);


            dayList_adapter = new dayListAdapter(this, dayList, global.getPersonalAvailabilityMap());
            dayList_adapter.setCustomButtonListener(new PersonalCustomListener());
            twListView.setAdapter(dayList_adapter);


            select_timeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(getInviteScreen);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_days, menu);
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
