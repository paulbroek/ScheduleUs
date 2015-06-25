package nl.mprog.scheduleus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Paul Broek on 1-6-2015.
 * 10279741
 * pauliusbroek@hotmail.com
 * Activities that offers event name input and date input
 */
public class NewEventActivity extends Activity {

    private Button SelectDateButton, AddDateButton;
    private AlertDialog.Builder dialogBuilder;
    private DatePicker datePicker;
    private ListView datelistView;
    private List dateList;
    private List display_dateList;
    private String eventName = "";
    JSONArray dates;
    Application global;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        AddDateButton = (Button) findViewById(R.id.AddDateButton);
        SelectDateButton = (Button) findViewById(R.id.SelectDateButton);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        datelistView = (ListView) findViewById(R.id.datelistView);

        global = (Application) getApplication();
        final Intent getSelectDaysScreen = new Intent(this, SelectDaysActivity.class);

        dateList = new ArrayList();
        display_dateList = new ArrayList();

        prefs = getSharedPreferences("nl.mprog.ScheduleUs", Context.MODE_PRIVATE);
        editor = prefs.edit();

        eventNameDialog();

        // Adding a date to a temporary list, which is displayed below the DatePicker
        AddDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DecimalFormat date_formatter = new DecimalFormat("00");
                String selected_day = date_formatter.format(datePicker.getDayOfMonth());
                String selected_month = date_formatter.format(datePicker.getMonth());
                String selected_year = date_formatter.format(datePicker.getYear());
                String date = selected_day + "-" + selected_month + "-" + selected_year;
                String display_date = datePicker.getDayOfMonth() + " " +  textMonth(Integer.parseInt(selected_month)) + ", " + selected_year;

                // Check whether date was added earlier
                if (!dateList.contains(date)) {
                    dateList.add(date);
                    display_dateList.add(display_date);
                }
                else
                    Toast.makeText(getApplicationContext(), "Date is in list", Toast.LENGTH_LONG).show();

                // Copy ArrayList and sort it, newest up
                ArrayList outputList = new ArrayList<String>(display_dateList);

                Collections.reverse(outputList);
                ArrayAdapter<String> adapter_dates = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_white_text, R.id.list_content, outputList);
                datelistView.setAdapter(adapter_dates);
            }
        });

        // Check for presence of temporary date list, if so, go to SelectTimesActivity
        SelectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dateList.isEmpty()) {

                    // Saves (empty) days in prefs and global
                    Set dateSet = new HashSet(dateList);
                    final Application global = (Application)getApplication();
                    global.putPersonalDaySet(dateSet);
                    editor.putStringSet("event_dates", dateSet).apply();
                    editor.putString("event_name",eventName).apply();

                    ParseObject Events = new ParseObject("Events");
                    Events.put("number_of_dates", dateList.size());
                    dates = new JSONArray(dateList);
                    Events.put("dates", dates);
                    Events.put("eventName", eventName);

                    Events.pinInBackground("new event");

                    startActivity(getSelectDaysScreen);
                }
                else
                    Toast.makeText(getApplicationContext(), "Enter at least one date", Toast.LENGTH_LONG).show();
            }
        });

        // Delete a date from list when user taps on it
        datelistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Collections.reverse(dateList);
                Collections.reverse(display_dateList);
                dateList.remove(position);
                display_dateList.remove(position);
                Collections.reverse(dateList);
                Collections.reverse(display_dateList);
                ArrayList outputList = new ArrayList<String>(display_dateList);

                Collections.reverse(outputList);
                ArrayAdapter<String> adapter_dates = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_white_text, R.id.list_content, outputList);
                datelistView.setAdapter(adapter_dates);
            }
        });
    }

    // Convert digital number to String with month name
    public String textMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

    private void eventNameDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final EditText eventNameInput = new EditText(this);

        // Process
        dialogBuilder.setTitle("Event name");
        dialogBuilder.setMessage("Please enter an event name");
        dialogBuilder.setView(eventNameInput);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (eventNameInput.getText().toString().length() > 0) {

                    eventName = eventNameInput.getText().toString();
                    Toast.makeText(getApplicationContext(), "Event has been named.", Toast.LENGTH_SHORT).show();
                }
                else {
                    eventName = "unnamed";
                    Toast.makeText(getApplicationContext(), "Please enter an event name", Toast.LENGTH_SHORT).show();
                }

                global.setCurrentEventName(eventName);
                Display();

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventName = "unnamed";
                global.setCurrentEventName(eventName);
                Display();
                Toast.makeText(getApplicationContext(), "Event has not been named.", Toast.LENGTH_SHORT).show();
            }
        });

        // Output
        AlertDialog dialogEventName = dialogBuilder.create();
        dialogEventName.show();
    }

    public void Display() {
        Toast.makeText(getApplicationContext(), "Please pick some dates for " + eventName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_event, menu);
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
}
