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

public class shared_dayListAdapter extends ArrayAdapter<String> {
    customButtonListener customListener;

    public interface customButtonListener {
        public void onButtonClickListener(int position, String value);
        public void onViewClickListener(int position, String value);
    }

    public void setCustomButtonListener(customButtonListener listener) {
        this.customListener = listener;
    }

    private Context context;
    private ArrayList<String> data = new ArrayList<String>();
    private Map<String, ArrayList<int[]>> shared_availabilityMap = new HashMap<String, ArrayList<int[]>>();

    public shared_dayListAdapter(Context context, ArrayList<String> dataItem, Map<String,ArrayList<int[]>> mapItem) {
        super(context, R.layout.list_times, dataItem);
        this.data = dataItem;
        this.shared_availabilityMap = mapItem;
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
            viewHolder.text = (TextView) convertView
                    .findViewById(R.id.child_dayTextView);
            viewHolder.delete_button = (Button) convertView
                    .findViewById(R.id.child_delete_day_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp_day = getItem(position);
        ArrayList<int[]> temp_availList = shared_availabilityMap.get(temp_day);


        viewHolder.dsv.setAvailabilityList(temp_availList);

        // Clicked on a day, go to SelectTimes
        viewHolder.dsv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListener != null) {
                    customListener.onViewClickListener(position, temp_day);
                }

            }
        });
        String value = "";
        try {
            // value = "" + availabilityMap.get(temp_day).get(0)[0];
            //value = "" + viewHolder.dsv.AvailableSlots.get(0)[0];
            value = "" + viewHolder.dsv.height;
        }
        catch (NullPointerException e)
        {
            value = "avail list is empty";
        }
        viewHolder.text.setText(temp_day);

        // Only show delete button when you're the initiator
        viewHolder.delete_button.setVisibility(View.INVISIBLE);
        /*// Clicked delete, remove the View
        viewHolder.delete_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListener != null) {
                    customListener.onButtonClickListener(position, temp_day);
                }

            }
        });*/

        return convertView;
    }

    public class ViewHolder {
        DrawingShowView dsv;
        TextView text;
        Button delete_button;
    }
}  