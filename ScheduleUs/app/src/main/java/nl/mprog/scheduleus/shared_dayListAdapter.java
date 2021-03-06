package nl.mprog.scheduleus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Paul Broek on 20-6-2015.
 * pauliusbroek@hotmail.com
 * 10279741
 * Adapter for accurate display of  DrawingShowView and TextView in a TwoWayView list
 */
public class shared_dayListAdapter extends ArrayAdapter<String> {
    ButtonListener customListener;

    public void setCustomButtonListener(ButtonListener listener) {
        this.customListener = listener;
    }

    private Context context;
    private Map<String, ArrayList<int[]>> shared_availabilityMap = new HashMap<String, ArrayList<int[]>>();
    private Map<String, ArrayList<int[]>> personal_availabilityMap = new HashMap<String, ArrayList<int[]>>();

    public shared_dayListAdapter(Context context, ArrayList<String> dataItem, Map<String,ArrayList<int[]>> PersonalMap, Map<String,ArrayList<int[]>> SharedMap) {
        super(context, R.layout.list_times, dataItem);
        this.personal_availabilityMap = PersonalMap;
        this.shared_availabilityMap = SharedMap;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_days, null);
            viewHolder = new ViewHolder();
            viewHolder.dsv = (DrawingShowView) convertView
                    .findViewById(R.id.child_DrawingShowView);
            viewHolder.textView = (TextView) convertView
                    .findViewById(R.id.child_dayTextView);
            viewHolder.delete_button = (Button) convertView
                    .findViewById(R.id.child_delete_day_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp_day = getItem(position);

        viewHolder.dsv.setSharedAvailabilityList(shared_availabilityMap.get(temp_day));
        viewHolder.dsv.setPersonalAvailabilityList(personal_availabilityMap.get(temp_day));

        // Clicked on a day, go to SelectTimes
        viewHolder.dsv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListener != null)
                    customListener.onViewClickListener(position, temp_day);
            }
        });
        viewHolder.textView.setText(temp_day);
        viewHolder.textView.bringToFront();

        // Only show delete button when you're the initiator
        viewHolder.delete_button.setVisibility(View.INVISIBLE);

        // Clicked delete, remove the View
        viewHolder.delete_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListener != null) {
                    customListener.onButtonClickListener(position, temp_day);
                }
            }
        });
        return convertView;
    }

    public class ViewHolder {
        DrawingShowView dsv;
        TextView textView;
        Button delete_button;
    }
}  