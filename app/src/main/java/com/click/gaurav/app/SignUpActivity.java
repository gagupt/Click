package com.click.gaurav.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private static Context context;
    private TextView signupUserTextView;
    private AutoCompleteTextView mobileSignUp;
    private AutoCompleteTextView nameSignUp;
    HttpResponse response = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        SignUpActivity.context = getApplicationContext();
        final Button loginButton = this.findViewById(R.id.mobile_sign_in_button);
        signupUserTextView = findViewById(R.id.userSignUpText);

        mobileSignUp = findViewById(R.id.mobileSignUp);
        nameSignUp = findViewById(R.id.nameSignUp);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.isInternetAvailable(context)) {
                    SignUpTask signUpTask = new SignUpTask();
                    signUpTask.execute();
                } else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class SignUpTask extends AsyncTask {

        @Override
        protected Bitmap doInBackground(Object... urlss) {
            createUser(mobileSignUp.getText().toString(), nameSignUp.getText().toString());
            return null;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Object result) {

            boolean succ = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            signupUserTextView.setText("response=" + succ);

            if (!succ) {
                signupUserTextView.setTextColor(Color.RED);
                signupUserTextView.setText("Failed to create user");

            } else {
                signupUserTextView.setTextColor(Color.GREEN);
                signupUserTextView.setText("Account Created Successfully.\n" +
                        "Login With Your Mobile No:\n"
                        + mobileSignUp.getText());
            }
        }
    }

    private void createUser(String mobileNum, String name) {
        Uri uri = new Uri.Builder()
                .scheme("http")
                .authority("13.232.31.237")
                .path("create/user")
                .build();
        String url = uri.toString();
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        response = null;
        try {

// Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                    2);
            nameValuePairs.add(new BasicNameValuePair("name", name));
            nameValuePairs.add(new BasicNameValuePair("mobileNo", mobileNum));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

// Execute HTTP Post Request
            response = httpclient.execute(httppost);


            //signupUserTextView.setText("response"+response);

            //OBJECT_MAPPER.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);

            //successCreateUser = Boolean.valueOf(response.getEntity().getContent().toString());
            // signupUserTextView.setText("user="+user);


// Log.i( "HttpManager:", "======> response: "
// + response.getEntity().getContent() );

        } catch (ClientProtocolException e) {
            Log.e("HttpManager", "ClientProtocolException thrown" + e);
        } catch (IOException e) {
            Log.e("HttpManager", "IOException thrown" + e);
        }
    }
}
