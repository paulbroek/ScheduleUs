package nl.mprog.scheduleus;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Paul Broek on 1-6-2015.
 * 10279741
 * pauliusbroek@hotmail.com
 */

public class SelectTimesActivity extends ActionBarActivity {
    private DrawingView dv;
    private TextView outputView;
    private Button ConfirmTimeButton;

    // private DrawingManager mDrawingManager=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_times);

        dv = (DrawingView) findViewById(R.id.mondayView);
        outputView = (TextView) findViewById(R.id.outputView);
        ConfirmTimeButton = (Button) findViewById(R.id.ConfirmTimeButton);
        final Intent getMyEventsScreen = new Intent(this, MyEventsActivity.class);

        Set<String> participants = new HashSet<>();
        participants.add("Johan");
        participants.add("Erik");
        // Get user data from parse.com
        ParseObject Events = new ParseObject("Users");
        Events.put("Username", "piet");
        //Events.put("participants", participants);
        Events.saveInBackground();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
        query.getInBackground("xWMyZ4YEGZ", new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your game score
                    outputView.setText("gelukt1");
                } else {
                    // something went wrong
                    outputView.setText("niet gelukt1");
                }
            }
        });

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Users");
        query2.whereEqualTo("Username", "piet");
        query2.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your game score
                    outputView.setText("gelukt2");
                } else {
                    // something went wrong
                    outputView.setText("niet gelukt2");
                }
            }
        });

        //outputView.setText("" + dv.getTimeSize());
        ConfirmTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(getMyEventsScreen);
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


}
