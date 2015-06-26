package nl.mprog.scheduleus;

import nl.mprog.scheduleus.timeListAdapter.customButtonListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by Paul Broek on 1-6-2015.
 * 10279741
 * pauliusbroek@hotmail.com
 * Activities that offers user friendly time input by swiping over screen, can parse swipes to time
 */
public class SelectTimesActivity extends ActionBarActivity implements customButtonListener{
    private DrawingView dv;
    private TextView outputView;
    private TextView availabilityView;
    private TextView selected_dayView;
    private ListView timesListView;
    private Button ConfirmTimesButton;
    private Button ShowTimesButton;
    private Button ResetButton;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private String current_event_id;
    private String selected_day;

    private ArrayList<String> timesList;
    private timeListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_times);

        dv = (DrawingView) findViewById(R.id.mondayView);
        outputView = (TextView) findViewById(R.id.outputView);
        availabilityView = (TextView) findViewById(R.id.availabilityView);
        selected_dayView = (TextView) findViewById(R.id.selected_day_View);
        timesListView = (ListView) findViewById(R.id.listView);
        ShowTimesButton = (Button) findViewById(R.id.ShowTimesButton);
        ConfirmTimesButton = (Button) findViewById(R.id.ConfirmTimesButton);
        ResetButton = (Button) findViewById(R.id.ResetButton);
        final Intent getSelectDaysScreen = new Intent(this, SelectDaysActivity.class);

        final Application global = (Application)getApplication();

        prefs = getSharedPreferences("nl.mprog.ScheduleUs", Context.MODE_PRIVATE);
        selected_day = prefs.getString("selected_day",null);
        selected_dayView.setText(selected_day);

        // We either come from a participants or an initiators perspective
        current_event_id = prefs.getString("event_id", null);

        // Participant perspective
        if (current_event_id != null) {
            // Participant wants to enter his times
            Toast.makeText(getApplicationContext(), "Building on existing event", Toast.LENGTH_LONG).show();

            ConfirmTimesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Update Availability Map, merge with database and go to SelectDaysActivity
                    global.putPersonalAvailabilityList(selected_day, new ArrayList<>(dv.getAvailabilityList()));
                    getSelectDaysScreen.putExtra("calling_event_id", current_event_id);
                    startActivity(getSelectDaysScreen);
                }
            });
        }

        // Initiator perspective
        else {
            ConfirmTimesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Update Availability Map and go to SelectDaysActivity
                    global.putPersonalAvailabilityList(selected_day, new ArrayList<>(dv.getAvailabilityList()));
                    startActivity(getSelectDaysScreen);
                }
            });
        }

        timesList = new ArrayList<>();
        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {dv.reDraw();}});

        // Convert te swiped area to readable time slots in hours and minutes
        ShowTimesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timesList.clear();

                // Converting DrawingView output to Strings
                try {
                    for (int i = 0; i < dv.getAvailabilityList().size(); i++) {
                        String time1 = "" + dv.getAvailabilityList().get(i)[0] + dv.getAvailabilityList().get(i)[1];
                        if (time1.endsWith("0"))
                            time1 += "0";
                        if (time1.startsWith("9"))
                            time1 = "0" + time1;
                        String time2 = "" + dv.getAvailabilityList().get(i)[2] + dv.getAvailabilityList().get(i)[3];
                        if (time2.endsWith("0"))
                            time2 += "0";
                        if (time2.startsWith("9"))
                            time2 = "0" + time2;
                        time1 = time1.substring(0,2) + ":" + time1.substring(2,4);
                        time2 = time2.substring(0,2) + ":" + time2.substring(2,4);
                        timesList.add(time1 + " till " + time2);
                    }

                } catch (Exception e) {
                    outputView.setText("parse error");
                }

                // Setting adapter and ListView
                adapter = new timeListAdapter(getApplicationContext(), timesList);
                adapter.setCustomButtonListener(SelectTimesActivity.this);
                timesListView.setAdapter(adapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onButtonClickListener(int position, String value) {
        Toast.makeText(SelectTimesActivity.this, "Button click " + value,
                Toast.LENGTH_SHORT).show();
    }
}
