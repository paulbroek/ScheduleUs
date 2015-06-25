package nl.mprog.scheduleus;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

/**
 * Created by Paul Broek on 20-6-2015.
 * pauliusbroek@hotmail.com
 * 10279741
 * Adapter for displaying a list of ScheduleUs users with a checkbox next to every user name
 */
public class userListAdapter extends ArrayAdapter<String> {
    customCheckBoxListener customListener;

    public interface customCheckBoxListener {
        public void onCheckBoxListener(int position, String value, Boolean is_Checked);
    }

    public void setCustomCheckBoxListener(customCheckBoxListener listener) {
        this.customListener = listener;
    }

    private Context context;

    public userListAdapter(Context context, ArrayList<String> dataItem) {
        super(context, R.layout.list_times, dataItem);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_users, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView
                    .findViewById(R.id.childTextView);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.childCheckBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp = getItem(position);
        viewHolder.text.setText(temp);
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (customListener != null)
                    customListener.onCheckBoxListener(position, temp, isChecked);
            }
        });

        return convertView;
    }

    public class ViewHolder {
        TextView text;
        CheckBox checkBox;
    }
}  