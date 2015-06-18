package nl.mprog.scheduleus;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class InviteActivity extends ActionBarActivity {
    private Button inviteButton;
    private ListView userlistView;
    private userListAdapter adapter;

    private ArrayList<String> userList;

    private static final int REQUEST_INVITE = 0;

    // Local Broadcast receiver for receiving invites
    private BroadcastReceiver mDeepLinkReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        inviteButton = (Button) findViewById(R.id.inviteButton);
        userlistView = (ListView) findViewById(R.id.userlistView);

        userList = new ArrayList<>();

        int size = 0;
        // Get complete user list from parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        //query.whereEqualTo("username", "jo");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> usernames, ParseException e) {
                if (e == null) {
                    if (usernames.size() > 0) {
                        Toast.makeText(InviteActivity.this, "gelukt, met" + usernames.size() + usernames.get(0).getString("username"), Toast.LENGTH_LONG).show();
                        userList.add(usernames.get(0).toString());
                        for (int i = 0; i < usernames.size(); i++) {
                            userList.add("" + usernames.get(i).getString("username"));
                        }
                        adapter = new userListAdapter(getApplicationContext(), userList);
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

        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                        .build();
                startActivityForResult(intent, REQUEST_INVITE);
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
}
