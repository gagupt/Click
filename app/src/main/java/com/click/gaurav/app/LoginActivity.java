package com.click.gaurav.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;

public class LoginActivity extends AppCompatActivity {

    private static Context context;
    private static User user = null;
    private TextView loginUserTextView;
    private AutoCompleteTextView mobileLogin;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginActivity.context = getApplicationContext();
        final Button loginButton = this.findViewById(R.id.mobile_sign_in_button);
        loginUserTextView = findViewById(R.id.userloginText);
        mobileLogin = findViewById(R.id.mobileSignin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.isInternetAvailable(context)) {
                    LoginTask loginTask = new LoginTask();
                    loginTask.execute();
                } else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class LoginTask extends AsyncTask {

        @Override
        protected Bitmap doInBackground(Object... urlss) {
            user = SignUpActivity.getUser(mobileLogin.getText().toString());
            return null;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Object result) {

            if (user == null) {
                loginUserTextView.setText("Phone number doesn't exist, \n" +
                        "please sign up to upload photos");
            } else {
                loginUserTextView.setText("Welcome " + user.getName());
                MainActivity.isLoggedin = true;
                MainActivity.mobileNum = user.getMobileNo();
                MainActivity.uname = user.getName();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        }

    }
}
