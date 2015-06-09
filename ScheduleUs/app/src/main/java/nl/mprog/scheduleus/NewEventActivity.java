package nl.mprog.scheduleus;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NewEventActivity extends ActionBarActivity {

    private Button SelectDateButton, AddDateButton;
    private DatePicker datePicker;
    private ListView datelistView;
    private List dateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        AddDateButton = (Button) findViewById(R.id.AddDateButton);
        SelectDateButton = (Button) findViewById(R.id.SelectDateButton);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        datelistView = (ListView) findViewById(R.id.datelistView);
        final Intent getSelectTimesScreen = new Intent(this, SelectTimesActivity.class);

        dateList = new ArrayList();

        // Adding a date to a temporary list, which is displayed below the DatePicker
        AddDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selected_day = "" + datePicker.getDayOfMonth();
                String selected_month = textMonth(datePicker.getMonth());
                String selected_year = "" + datePicker.getYear();
                String display_date = selected_day + " " +  selected_month + ", " + selected_year;

                // Check whether date was added earlier
                if (!dateList.contains(display_date))
                    dateList.add(display_date);
                else
                    Toast.makeText(getApplicationContext(), "Datum is al toegevoegd", Toast.LENGTH_LONG).show();

                // Copy ArrayList and sort it, newest up
                ArrayList outputList = new ArrayList<String>(dateList);
                Collections.reverse(outputList);
                ArrayAdapter<String> adapter_dates = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_black_text, R.id.list_content, outputList);
                datelistView.setAdapter(adapter_dates);
            }
        });

        // Check for presence of temporary date list, if so, go to SelectTimesActivity
        SelectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dateList.isEmpty())
                    startActivity(getSelectTimesScreen);
                else
                    Toast.makeText(getApplicationContext(), "Voeg tenminste één datum toe", Toast.LENGTH_LONG).show();
            }
        });
    }

    public String textMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
