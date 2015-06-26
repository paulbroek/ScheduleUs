package nl.mprog.scheduleus;

/**
 * Created by Paul Broek on 22-6-2015.
 * pauliusbroek@hotmail.com
 * 10279741
 * Interface for different listeners inside SelectDaysActivity
 */
public interface ButtonListener {

    public void onButtonClickListener(int position, String value);
    public void onViewClickListener(int position, String value);
}
