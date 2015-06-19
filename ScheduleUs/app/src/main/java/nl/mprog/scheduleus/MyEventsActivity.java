package nl.mprog.scheduleus;

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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MyEventsActivity extends ActionBarActivity {

    private TextView myEventsView;
    private ListView myEvents_ListView;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    ArrayList eventNameList;
    ArrayList EventsIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        myEventsView = (TextView) findViewById(R.id.myEventsView);
        myEvents_ListView = (ListView) findViewById(R.id.myEvents_ListView);

        Intent activityThatCalled = getIntent();
        final Application global = (Application)getApplication();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        query.whereEqualTo("participants", ParseUser.getCurrentUser().getUsername());
        //query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> eventList,
                             ParseException e) {
                if (e == null) {
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
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

        // Go to SelectDaysActivity to see shared selected days
        myEvents_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Hier of pas in SelectDays ophalen van dagenlijst?
                //editor.putStringSet("shared_event_dates", ).apply();

                Intent getSelectDaysScreen = new Intent(getApplicationContext(), SelectDaysActivity.class);

                getSelectDaysScreen.putExtra("calling_event_id", EventsIdList.get(position).toString());
                startActivity(getSelectDaysScreen);
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
