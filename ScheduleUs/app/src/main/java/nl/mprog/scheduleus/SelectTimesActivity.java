package nl.mprog.scheduleus;

import nl.mprog.scheduleus.timeListAdapter.customButtonListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Paul Broek on 1-6-2015.
 * 10279741
 * pauliusbroek@hotmail.com
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
    private String eventName;

    private Set<String> participants;
    private Set<String> dates;
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
        final Intent getMyEventsScreen = new Intent(this, MyEventsActivity.class);
        final Intent getSelectDaysScreen = new Intent(this, SelectDaysActivity.class);

        final Application global = (Application)getApplication();

        prefs = getSharedPreferences("nl.mprog.ScheduleUs", Context.MODE_PRIVATE);
        selected_day = prefs.getString("selected_day",null);
        selected_dayView.setText(selected_day);
        participants = new HashSet<>();
        participants.add("Johan");
        participants.add("Erik");
        // Get user data from parse.com
        //ParseObject Users = new ParseObject("Users");
        //Users.put("Username", "piet");
        //Events.put("participants", participants);

        current_event_id = prefs.getString("current_event_id", null);
        if (current_event_id == null)
            Toast.makeText(getApplicationContext(), "No current event was found", Toast.LENGTH_LONG).show();

        ParseQuery<ParseObject> query_event = ParseQuery.getQuery("Events");
        query_event.fromPin("new event");
        query_event.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList, ParseException e) {
                if (e == null) {
                    String text = "" + eventList.size();
                    for (ParseObject elem : eventList) {
                        text += elem.getString("name");
                        if (eventList.size() == 1)
                            text += "";
                    }
                    //outputView.setText(text);

                } else {
                    outputView.setText("niet gelukt1");
                }
            }
        });

        dates = new HashSet<>(prefs.getStringSet("dates", null));
        timesList = new ArrayList<>();
        eventName = prefs.getString("eventName", null);

       /* ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Users");
        query2.whereEqualTo("Username", "piet");
        query2.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    outputView.setText("gelukt2");
                } else {
                    outputView.setText("niet gelukt2");
                }
            }
        });*/

        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dv.reDraw();

            }
        });

        ShowTimesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                outputView.setText("" + dv.getTimeSize());
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

                // Setting adapter and listView
                adapter = new timeListAdapter(getApplicationContext(), timesList);
                adapter.setCustomButtonListener(SelectTimesActivity.this);
                timesListView.setAdapter(adapter);
            }
        });

        ConfirmTimesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Update Availability Map and go to SelectDaysActivity
                global.putPersonalAvailabilityList(selected_day, new ArrayList<>(dv.getAvailabilityList()));
                startActivity(getSelectDaysScreen);
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

    @Override
    public void onButtonClickListener(int position, String value) {
        Toast.makeText(SelectTimesActivity.this, "Button click " + value,
                Toast.LENGTH_SHORT).show();
    }
}
