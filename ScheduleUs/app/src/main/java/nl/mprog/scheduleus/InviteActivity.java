package nl.mprog.scheduleus;

import nl.mprog.scheduleus.userListAdapter.customCheckBoxListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class InviteActivity extends Activity implements customCheckBoxListener {
    private Button inviteButton;
    private ListView userlistView;
    private SearchView searchView;
    private userListAdapter adapter;
    private userListAdapter adapter2;


    private ArrayList<String> userList;
    private Set<String> participantsSet;
    JSONArray json_datesArray;
    JSONObject json_timesObject;
    JSONArray json_participantsArray;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    private static final int REQUEST_INVITE = 0;

    // Local Broadcast receiver for receiving invites
    private BroadcastReceiver mDeepLinkReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        inviteButton = (Button) findViewById(R.id.inviteButton);
        userlistView = (ListView) findViewById(R.id.userlistView);
        searchView = (SearchView) findViewById(R.id.searchView);

        final Application global = (Application)getApplication();
        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = prefs.edit();

        userList = new ArrayList<>();
        participantsSet = new HashSet<>();
        participantsSet.add("test_name");


        editor.putStringSet("participants_set",participantsSet).apply();

        int size = 0;
        // Get complete user list from parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        //query.whereEqualTo("username", "jo");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> usernames, ParseException e) {
                if (e == null) {
                    if (usernames.size() > 0) {
                        //Toast.makeText(InviteActivity.this, "gelukt, met" + usernames.size() + usernames.get(0).getString("username"), Toast.LENGTH_LONG).show();
                        for (int i = 0; i < usernames.size(); i++) {
                            userList.add("" + usernames.get(i).getString("username"));
                        }
                        adapter = new userListAdapter(getApplicationContext(), userList);
                        adapter.setCustomCheckBoxListener(InviteActivity.this);
                        adapter2 = new userListAdapter(getApplicationContext(), userList);
                        //searchView.setAdapter(adapter2); // moet met cursor
                        userlistView.setAdapter(adapter);
                    }


                    Toast.makeText(InviteActivity.this, "gelukt", Toast.LENGTH_LONG).show();
                    //Log.d("score", "Retrieved " + scoreList.size() + " scores");
                } else {
                    Toast.makeText(InviteActivity.this, "niet gelukt", Toast.LENGTH_LONG).show();
                    //Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

        if  (userList.size() > 0)
            Toast.makeText(InviteActivity.this, "" + size + userList.get(0), Toast.LENGTH_LONG).show();
        // Setting adapter and listView
        //adapter = new userListAdapter(getApplicationContext(), userList);
        //adapter.setCustomButtonListener(InviteActivity.this);
        //userlistView.setAdapter(adapter);

        // Send all event and participant information to parse, trigger invites
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                        .build();
                startActivityForResult(intent, REQUEST_INVITE);*/

                ParseObject Events = new ParseObject("Events");
                //json_datesArray = new JSONArray(dateList);
                //Events.put("number_of_dates", dateList.size());

                String message = "Invited: ";
                for (Object aParticipantsSet : participantsSet) message += aParticipantsSet + ", ";
                Toast.makeText(InviteActivity.this, message,
                        Toast.LENGTH_SHORT).show();

                json_datesArray = new JSONArray(global.getDaySet());
                json_participantsArray = new JSONArray(participantsSet);
                json_timesObject = new JSONObject();
                for (String day : global.getDaySet()) {
                    try { json_timesObject.put(day, global.getAvailabilityList(day)); }
                    catch (JSONException e) {
                        Toast.makeText(InviteActivity.this, "json excep, " + day,
                                Toast.LENGTH_SHORT).show();
                    }
                }
                ParseObject timesObject = new ParseObject("timesObject");
                //timesObject.put("json_timesObject", json_timesObject);
                timesObject.put("heujj", "jahoor");
                timesObject.saveInBackground();
                Events.put("dates", json_datesArray);
                Events.put("participants", json_participantsArray);
                Events.put("name", global.getName());

                //Events.pinInBackground("new event");
                Events.saveInBackground();

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
                /*String message = "";
                for (Object aParticipantsSet : participantsSet) message += aParticipantsSet + ", ";
                Toast.makeText(InviteActivity.this, message,
                        Toast.LENGTH_SHORT).show();*/
            }
            else {
                Toast.makeText(InviteActivity.this, "unchecked " + value,
                        Toast.LENGTH_SHORT).show();
                participantsSet.remove(value);
                /*String message = "";
                for (Object aParticipantsSet : participantsSet) message += aParticipantsSet + ", ";
                Toast.makeText(InviteActivity.this, message,
                        Toast.LENGTH_SHORT).show();*/
            }





        editor.putStringSet("participants_set", participantsSet).apply();
    }


}
