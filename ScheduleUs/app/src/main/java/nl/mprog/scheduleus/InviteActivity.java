package nl.mprog.scheduleus;

import nl.mprog.scheduleus.userListAdapter.customCheckBoxListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class InviteActivity extends Activity implements customCheckBoxListener {
    private Button inviteButton;
    private ListView userlistView;
    //private SearchView searchView;
    private userListAdapter adapter;
    private userListAdapter adapter2;


    private ArrayList<String> userList;
    private Set<String> participantsSet;
    JSONArray json_datesArray;
    JSONObject json_timesObject;
    JSONArray json_participantsArray;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        inviteButton = (Button) findViewById(R.id.inviteButton);
        userlistView = (ListView) findViewById(R.id.userlistView);

        final Application global = (Application)getApplication();
        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = prefs.edit();

        userList = new ArrayList<>();
        participantsSet = new HashSet<>();

        editor.putStringSet("participants_set",participantsSet).apply();

        // Get complete user list from parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> usernames, ParseException e) {
                if (e == null) {
                    if (usernames.size() > 0) {
                        for (int i = 0; i < usernames.size(); i++) {
                            userList.add("" + usernames.get(i).getString("username"));
                        }
                        adapter = new userListAdapter(getApplicationContext(), userList);
                        adapter.setCustomCheckBoxListener(InviteActivity.this);
                        adapter2 = new userListAdapter(getApplicationContext(), userList);
                        userlistView.setAdapter(adapter);
                    }

                    Log.d("user", "Retrieved " + usernames.size());
                } else
                    Log.d("user", "Error: " + e.getMessage());
            }
        });

        // Send all event and participant information to parse, trigger invites
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseObject Event = new ParseObject("Events");

                // Tell user who have been invited
                String message = "Invited: ";
                for (Object participant : participantsSet) message += participant + ", ";
                Toast.makeText(InviteActivity.this, message, Toast.LENGTH_SHORT).show();

                // Convert global data to json format
                json_datesArray = new JSONArray(global.getPersonalDaySet());
                json_participantsArray = new JSONArray(participantsSet);
                json_timesObject = new JSONObject();

                // Save our new event
                Event.put("dates", json_datesArray);
                Event.put("initiator", ParseUser.getCurrentUser());
                Event.put("participants", json_participantsArray);
                Event.put("event_name", global.getCurrentEventName());

                // Put al availability information for initiator (= current user)
                for (String day : global.getPersonalDaySet()) {
                    try {
                        JSONArray temp = new JSONArray(global.getPersonalAvailabilityList(day));

                        ParseObject AvailItem = new ParseObject("AvailItems");
                        ParseObject SharedTime = new ParseObject("SharedTimes");

                        AvailItem.put("User", ParseUser.getCurrentUser());
                        AvailItem.put("parent_event", Event);
                        AvailItem.put("Day", day);
                        AvailItem.put("Times", temp);
                        AvailItem.put("SharedTime", SharedTime);

                        // SharedTime will contain the overlap between user' availability, at start this is just the initiators data
                        SharedTime.put("Initiator", ParseUser.getCurrentUser());
                        SharedTime.put("parent_event", Event);
                        SharedTime.put("Day", day);
                        SharedTime.put("Times", temp);
                        AvailItem.saveInBackground();

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    } catch ( Exception e) {
                        Log.d("json excep", day);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    // User selected a participant to invite, add this person to participantsSet
    @Override
    public void onCheckBoxListener(int position, String value, Boolean is_Checked) {

        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = prefs.edit();
        participantsSet = prefs.getStringSet("participants_set", null);

        if (participantsSet != null)
            if (is_Checked) {
                Toast.makeText(InviteActivity.this, "checked " + value,
                        Toast.LENGTH_SHORT).show();
                participantsSet.add(value);
            }
            else {
                Toast.makeText(InviteActivity.this, "unchecked " + value,
                        Toast.LENGTH_SHORT).show();
                participantsSet.remove(value);
            }

        // The initiator is also a participant, add him as well.
        participantsSet.add(ParseUser.getCurrentUser().getUsername());
        editor.putStringSet("participants_set", participantsSet).apply();
    }
}
