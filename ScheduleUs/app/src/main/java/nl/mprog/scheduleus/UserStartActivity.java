package nl.mprog.scheduleus;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class UserStartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_start);
    }

    // Either let user log in or create an account
    public void goLogIn(View view) {

        // create intent for create Event activity
        Intent getLoginScreen = new Intent(this, LoginActivity.class);

        // Go to login screen
        startActivity(getLoginScreen);
    }
    public void goSignUp(View view) {

        // create intent for create Event activity
        Intent getSignUpScreen = new Intent(this, SignUpActivity.class);

        // Go to go to sign up screen
        startActivity(getSignUpScreen);
    }
}
