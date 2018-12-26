package com.example.gaurav.myapplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    private ImageView mImageView;
    private TextView mTextView;
    private TextView apptitle;
    private Bitmap imageBitmap;
    private static Context context;
    private static FileOutputStream fo;
    private static File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.imagevid);
        mTextView = findViewById(R.id.testText);
        apptitle = findViewById(R.id.apptitle);
        apptitle.setTypeface(null, Typeface.BOLD);
        MainActivity.context = getApplicationContext();
        final Button saveS3 = this.findViewById(R.id.backUp);
        final Button photoButton = this.findViewById(R.id.bAcc);
        saveS3.setEnabled(false);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                saveS3.setEnabled(true);
            }
        });

        saveS3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageBitmap != null) {
                    uploadImagePost(file);
                    saveS3.setEnabled(false);
                    saveS3.setText("Saved");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_TAKE_PHOTO);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                //  ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // byte[] byteArray = stream.toByteArray(); // convert camera photo to byte array
                try {
                    // save it in your external storage.
                    file = new File(Environment.getExternalStorageDirectory() + "/_camera.jpeg");
                    fo = new FileOutputStream(file);
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fo);
                    //fo.write(byteArray);
                    fo.flush();
                    fo.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

//        if(resultCode != RESULT_CANCELED ){
//            if (requestCode == REQUEST_TAKE_PHOTO&& data!=null) {
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                mImageView.setImageBitmap(photo);
//            }
//        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

//    private void galleryAddPic() {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(mCurrentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }

    private void getReq() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
  //  String url ="https://click-env.karp5mqyjg.ap-south-1.elasticbeanstalk.com/hello?id=1";
        Uri uri = new Uri.Builder()
                .scheme("http")
                .authority("click-env.karp5mqyjg.ap-south-1.elasticbeanstalk.com")
                .path("hello")
                .appendQueryParameter("id", "1")
                .build();


        String url = uri.toString();
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mTextView.setText("Response is: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!" + error);
            }
        });

//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                20000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

// Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    public void uploadImagePost(File file) {

        SimpleMultiPartRequest request = new SimpleMultiPartRequest(Request.Method.POST, "http://click-env.karp5mqyjg.ap-south-1.elasticbeanstalk.com/upload/image",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mTextView.setText("Response is: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTextView.setText("That didn't work!" + error.toString());
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(this);
        request.addMultipartParam("content-type", "text", "multipart/form-data");
        request.addMultipartParam("boundary", "text", "----WebKitFormBoundary7MA4YWxkTrZu0gW");
        request.addFile("file", file.getPath());

        request.setFixedStreamingMode(true);
        queue.add(request);
    }
}
