package nl.mprog.scheduleus;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class userListAdapter extends ArrayAdapter<String> {
    customButtonListener customListener;

    public interface customButtonListener {
        public void onButtonClickListener(int position, String value);
    }

    public void setCustomButtonListener(customButtonListener listener) {
        this.customListener = listener;
    }

    private Context context;
    private ArrayList<String> data = new ArrayList<String>();

    public userListAdapter(Context context, ArrayList<String> dataItem) {
        super(context, R.layout.list_times, dataItem);
        this.data = dataItem;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_times, null);
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
        /*viewHolder.delete_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListener != null) {
                    customListener.onButtonClickListener(position, temp);
                }

            }
        });
        viewHolder.edit_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListener != null) {
                    customListener.onButtonClickListener(position, temp);
                }

            }
        });*/

        return convertView;
    }

    public class ViewHolder {
        TextView text;
        CheckBox checkBox;
    }
}  