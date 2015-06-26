package nl.mprog.scheduleus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Paul Broek on 17-6-2015.
 * pauliusbroek@hotmail.com
 * 10279741
 * Home screen for logged out state, shows Log In and Sign Up buttons and ScheduleUs logo.
 */
public class UserStartActivity extends Activity {

    private TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_start);

        titleView = (TextView) findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getAssets(), "Unique.ttf");
        titleView.setTypeface(font);
    }

    // Either let user log in or create an account
    public void goLogIn(View view) {

        // Create intent for create Event activity
        Intent getLoginScreen = new Intent(this, LoginActivity.class);

        // Go to login screen
        startActivity(getLoginScreen);
    }
    public void goSignUp(View view) {

        // Create intent for create Event activity
        Intent getSignUpScreen = new Intent(this, SignUpActivity.class);

        // Go to go to sign up screen
        startActivity(getSignUpScreen);
    }
}
