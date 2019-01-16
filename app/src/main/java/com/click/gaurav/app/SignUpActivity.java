package com.click.gaurav.app;

import android.content.Context;
import android.content.Intent;
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

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private static Context context;
    private TextView signupUserTextView;
    private AutoCompleteTextView mobileSignUp;
    private AutoCompleteTextView nameSignUp;
    HttpResponse response = null;
    private static User user1 = null;
    private static String server_response = null;

    private static final ObjectMapper MAPPER = new ObjectMapper();

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
                    if (TextUtils.isEmpty(nameSignUp.getText().toString()) && TextUtils.isEmpty(mobileSignUp.getText().toString())) {
                        signupUserTextView.setText("Please enter phone number and name");
                    } else if (TextUtils.isEmpty(mobileSignUp.getText().toString())) {
                        signupUserTextView.setText("Please enter phone number");
                    } else if (TextUtils.isEmpty(nameSignUp.getText().toString())) {
                        signupUserTextView.setText("Please enter name");
                    } else {
                        if (validateNumber(Long.valueOf(mobileSignUp.getText().toString()), "+91")) {

                            SignUpTask signUpTask = new SignUpTask();
                            signUpTask.execute();

                        } else {
                            signupUserTextView.setTextColor(Color.RED);
                            signupUserTextView.setText("Please enter a valid phone number");
                        }
                    }
                } else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static boolean validateNumber(long number, String countryCode) {
        boolean isValid = false;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            String numberStr = countryCode.concat(String.valueOf(number));
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(numberStr, "");
            isValid = phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    private class SignUpTask extends AsyncTask {

        @Override
        protected Bitmap doInBackground(Object... urlss) {

            user1 = getUser(mobileSignUp.getText().toString());

            if (user1 == null) {
                createUser(mobileSignUp.getText().toString(), nameSignUp.getText().toString());
            }
            return null;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Object result) {
            if (user1 == null) {
                boolean succ = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
                if (!succ) {
                    signupUserTextView.setTextColor(Color.RED);
                    signupUserTextView.setText("Failed to create user");

                } else {
                    signupUserTextView.setTextColor(Color.GREEN);
                    signupUserTextView.setText("Account Created Successfully.\n" +
                            "Login With Your Mobile No:\n"
                            + mobileSignUp.getText());
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                }
            } else {
                signupUserTextView.setTextColor(Color.RED);
                signupUserTextView.setText("User already exist with this mobile number: " + user1.getMobileNo());
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


    public static User getUser(String mobileNum) {
        User userOld = null;
        Uri uri = new Uri.Builder()
                .scheme("http")
                .authority("13.232.31.237")
                .path("get/user")
                .build();
        String url = uri.toString();
        HttpClient httpclient = new DefaultHttpClient();

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                1);
        nameValuePairs.add(new BasicNameValuePair("phoneNo", mobileNum));

        String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");

        if (!url.endsWith("?")) {
            url += "?";
        }
        url += paramString;

        HttpGet httpget = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.getStatusLine().getStatusCode() == 200) {
            try {
                server_response = EntityUtils.toString(response.getEntity());
                userOld = MAPPER.readValue(server_response, User.class);
                if (userOld.getMobileNo() == null && userOld.getName() == null) {
                    userOld = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("Server response", String.valueOf(response));
        } else

        {
            Log.i("Server response", "Failed to get server response");
        }
        return userOld;
    }
}
