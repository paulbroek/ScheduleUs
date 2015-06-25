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

public class dayListAdapter extends ArrayAdapter<String> {
    ButtonListener customListener;

    public void setCustomButtonListener(ButtonListener listener) {
        this.customListener = listener;
    }

    private Context context;
    private Map<String, ArrayList<int[]>> availabilityMap = new HashMap<String, ArrayList<int[]>>();

    public dayListAdapter(Context context, ArrayList<String> dataItem, Map<String,ArrayList<int[]>> mapItem) {
        super(context, R.layout.list_times, dataItem);
        this.availabilityMap = mapItem;
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
        ArrayList<int[]> temp_availList = availabilityMap.get(temp_day);


        viewHolder.dsv.setPersonalAvailabilityList(temp_availList);

        // Clicked on a day, go to SelectTimes
        viewHolder.dsv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListener != null) {
                    customListener.onViewClickListener(position, temp_day);
                }

            }
        });

        viewHolder.text.setText(temp_day);
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
        TextView text;
        Button delete_button;
    }
}  