package nl.mprog.scheduleus;

import nl.mprog.scheduleus.ListAdapter.customButtonListener;
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
import com.parse.GetCallback;
import com.parse.Parse;
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
    private ListView timesListView;
    private Button ConfirmTimeButton;
    private Button ResetButton;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private String current_event_id;
    private String eventName;

    private Set<String> participants;
    private Set<String> dates;
    private ArrayList<String> timesList;
    private ListAdapter adapter;

    // private DrawingManager mDrawingManager=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_times);

        dv = (DrawingView) findViewById(R.id.mondayView);
        outputView = (TextView) findViewById(R.id.outputView);
        availabilityView = (TextView) findViewById(R.id.availabilityView);
        timesListView = (ListView) findViewById(R.id.listView);
        ConfirmTimeButton = (Button) findViewById(R.id.ConfirmTimeButton);
        ResetButton = (Button) findViewById(R.id.ResetButton);
        final Intent getMyEventsScreen = new Intent(this, MyEventsActivity.class);

        prefs = getSharedPreferences("nl.mprog.ScheduleUs", Context.MODE_PRIVATE);

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

        ConfirmTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                outputView.setText("" + dv.getTimeSize());
                timesList.clear();
                try {
                    String result = "";
                    /*for (int i = 0; i < dv.getTimesList().size(); i++) {
                        result += dv.getTimesList().get(i)[0] + " ";
                        result += dv.getTimesList().get(i)[1] + " ";
                    }*/

                    String availability = "Available between \n";
                    for (int i = 0; i < dv.getTimesList().size(); i++) {
                        String time1 = "" + dv.getTimesList().get(i)[0];
                        if (time1.startsWith("9"))
                            time1 = "0" + time1;
                        String time2 = "" + dv.getTimesList().get(i)[1];
                        if (time2.startsWith("9"))
                            time2 = "0" + time2;
                        time1 = time1.substring(0,2) + ":" + time1.substring(2,4);
                        time2 = time2.substring(0,2) + ":" + time2.substring(2,4);
                        availability += time1 + " till " + time2 + "\n";
                        timesList.add(time1 + " till " + time2);
                    }
                    //availabilityView.setText(availability);

                    outputView.setText("" + result);
                } catch (Exception e) {
                    outputView.setText("fail");
                }

                adapter = new ListAdapter(getApplicationContext(), timesList);
                adapter.setCustomButtonListener(SelectTimesActivity.this);
                timesListView.setAdapter(adapter);

                /*int test = 0;
                for (int i = 0; i < dv.getAvailabilityArray().length; i ++)
                    if (dv.getAvailabilityArray()[i])
                        test ++;
                outputView.setText("" + test);*/
                //startActivity(getMyEventsScreen);
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
