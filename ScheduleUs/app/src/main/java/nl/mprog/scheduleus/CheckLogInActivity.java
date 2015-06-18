package nl.mprog.scheduleus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;


public class CheckLoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            // User is logged in, go to Main
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // User is not logged in, go to UserStart
            startActivity(new Intent(this, UserStartActivity.class));
        }
        finish();
    }
}
