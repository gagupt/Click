package com.click.gaurav.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static Context context;
    private static User user;
    private TextView loginUserTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginActivity.context = getApplicationContext();
        final Button loginButton = this.findViewById(R.id.mobile_sign_in_button);
        loginUserTextView = findViewById(R.id.userloginText);

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
            user = getUser();
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
            }
        }

    }

    private User getUser() {
        Uri uri = new Uri.Builder()
                .scheme("http")
                .authority("13.232.31.237")
                .path("download/image")
                .build();
        String url = uri.toString();
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.getStatusLine().getStatusCode() == 200) {
            String server_response = null;
            try {
                server_response = EntityUtils.toString(response.getEntity());
                List<String> urlsTemp = Arrays.asList(server_response.split("\\s*,\\s*"));
                for (String urlString : urlsTemp) {
                    urlString = urlString.replaceAll("^\"|\"$", "");
                    urlString = urlString.startsWith("[") ? urlString.substring(1) : urlString;
                    urlString = urlString.replaceAll("^\"|\"$", "");
                    if (urlString != null && urlString.length() > 0 && urlString.charAt(urlString.length() - 1) == ']') {
                        urlString = urlString.substring(0, urlString.length() - 1);
                    }
                    urlString = urlString.replaceAll("^\"|\"$", "");
                    //urls.add(urlString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Log.i("Server response", String.valueOf(urls.size()));
        } else {
            Log.i("Server response", "Failed to get server response");
        }
        return null;
    }
}
