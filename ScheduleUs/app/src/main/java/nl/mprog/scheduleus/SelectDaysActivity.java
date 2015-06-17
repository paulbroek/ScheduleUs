package nl.mprog.scheduleus;

import nl.mprog.scheduleus.dayListAdapter.customButtonListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class SelectDaysActivity extends ActionBarActivity implements customButtonListener{

    private TextView days_textView;
    private Button select_timeButton;
    private TwoWayView twListView;

    private dayListAdapter dayList_adapter;
    ArrayList<String> dayList;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private Set<String> dateSet;
    private String eventName;
    private String selected_day;

    Application global;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_days);

        days_textView = (TextView) findViewById(R.id.days_textView);
        select_timeButton = (Button) findViewById(R.id.select_timeButton);
        twListView = (TwoWayView) findViewById(R.id.lvItems);
        final Intent getSelectTimesScreen = new Intent(this, SelectTimesActivity.class);

        prefs = getSharedPreferences("nl.mprog.ScheduleUs", Context.MODE_PRIVATE);
        editor = prefs.edit();

        eventName = prefs.getString("event_name", null);
        dateSet = new HashSet<>(prefs.getStringSet("event_dates", null));

        dayList = new ArrayList<String>(dateSet);

        global = (Application) getApplication();
        dayList_adapter = new dayListAdapter(this, dayList, global.getAvailabilityMap());
        dayList_adapter.setCustomButtonListener(SelectDaysActivity.this);
        twListView.setAdapter(dayList_adapter);

        /*global.setData(300);
        ArrayList<Boolean> test = new ArrayList<>();
        test.add(true);
        test.add(true);
        global.putAvailabilityList("maandag", test);*/

        String message = "" + global.getAvailabilityMap().containsKey("17-05-2015");
        Toast.makeText(SelectDaysActivity.this, message,
                Toast.LENGTH_SHORT).show();

        select_timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getSelectTimesScreen);
            }
        });
    }

    // Deleting a day, so adapter and preferences need an update
    @Override
    public void onButtonClickListener(int position, String value) {

        dayList.remove(position);
        dateSet = new HashSet(dayList);
        editor.putStringSet("event_dates", dateSet).apply();
        dayList_adapter = new dayListAdapter(this, dayList, global.getAvailabilityMap());
        dayList_adapter.setCustomButtonListener(SelectDaysActivity.this);
        twListView.setAdapter(dayList_adapter);

        Toast.makeText(SelectDaysActivity.this, "Day " + value + " deleted",
                Toast.LENGTH_SHORT).show();
    }

    // Click on a day, go to SelectTimesActivity for this day
    @Override
    public void onViewClickListener(int position, String value) {

        selected_day = value;
        editor.putString("selected_day", selected_day).apply();
        final Intent getSelectTimesScreen = new Intent(this, SelectTimesActivity.class);
        startActivity(getSelectTimesScreen);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_days, menu);
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
