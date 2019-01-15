package com.click.gaurav.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    public static final int GET_FROM_GALLERY = 3;
    String mCurrentPhotoPath;
    private ImageView mImageView;
    private TextView mTextView;
    private TextView apptitle;
    private static Context context;
    private static FileOutputStream fo;
    private static File file;
    private static File photoFile;
    private static ImageLoader imageLoader;
    Drawable myDrawable;
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.imagevid);
        mTextView = findViewById(R.id.testText);
        apptitle = findViewById(R.id.apptitle);
        apptitle.setTypeface(null, Typeface.BOLD);
        MainActivity.context = getApplicationContext();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        myDrawable = getResources().getDrawable(R.drawable.ic_action_name);
        final Button saveS3 = this.findViewById(R.id.backUp);
        final Button photoButton = this.findViewById(R.id.bAcc);
        final Button uploadPhotoButton = this.findViewById(R.id.upload_btn);
        saveS3.setEnabled(false);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                saveS3.setEnabled(true);
                saveS3.setText("Cloud Backup");
                mTextView.setText("");
            }
        });

        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchUploadPictureIntent();
                saveS3.setEnabled(true);
                saveS3.setText("Cloud Backup");
                mTextView.setText("");
            }
        });

        saveS3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.isInternetAvailable(context)) {
                    UploadTask upTask = new UploadTask();
                    upTask.execute();
                    saveS3.setEnabled(false);
                    saveS3.setText("Saved");
                } else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button btn = findViewById(R.id.gallery);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.isInternetAvailable(context)) {
                    mImageView.setImageDrawable(myDrawable);
                    startActivity(new Intent(MainActivity.this, GalleryActivity.class));
                } else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == REQUEST_TAKE_PHOTO || requestCode == GET_FROM_GALLERY) && resultCode == RESULT_OK) {
            if (photoFile.exists()) {
                if (requestCode == GET_FROM_GALLERY) {
                    Uri selectedImage = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        mImageView.setImageBitmap(bitmap);

                        InputStream in = getContentResolver().openInputStream(selectedImage);
                        OutputStream out = new FileOutputStream(new File(mCurrentPhotoPath));
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        out.close();
                        in.close();


                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

//                    try {
//                        FileOutputStream out = new FileOutputStream(mCurrentPhotoPath);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                        out.flush();
//                        out.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                } else if (requestCode == REQUEST_TAKE_PHOTO) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    mImageView.setImageBitmap(myBitmap);
                    mImageView.refreshDrawableState();
                }
                Toast.makeText(context, "CAPTURED " + i,
                        Toast.LENGTH_SHORT).show();
                i++;
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.click.android.fileprovider",
                        photoFile);
                // Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void dispatchUploadPictureIntent() {
        Intent uploadPicIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        // Ensure that there's a camera activity to handle the intent
        if (uploadPicIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.click.android.fileprovider",
                        photoFile);

                //  Uri photoURI = Uri.fromFile(photoFile);

                uploadPicIntent.putExtra(MediaStore.EXTRA_FULL_SCREEN, photoURI);
                startActivityForResult(uploadPicIntent, GET_FROM_GALLERY);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
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
        return null;
    }


    private class UploadTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object... urlss) {
            uploadImagePost(photoFile);
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            Toast.makeText(getApplicationContext(), "Uploaded successfully ", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadImagePost(File file) {

//        SimpleMultiPartRequest request = new SimpleMultiPartRequest(Request.Method.POST, "http:/13.232.31.237/upload/image",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        mTextView.setText("Response is: " + response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        mTextView.setText("That didn't work!" + error.toString());
//                    }
//                });
//        RequestQueue queue = Volley.newRequestQueue(this);
//        request.addMultipartParam("content-type", "text", "multipart/form-data");
//        request.addMultipartParam("boundary", "text", "----WebKitFormBoundary7MA4YWxkTrZu0gW");
//        request.addFile("file", file.getPath());
//
//        request.setFixedStreamingMode(true);
//
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                50000,
//                2,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        queue.add(request);


        Uri uri = new Uri.Builder()
                .scheme("http")
                .authority("13.232.31.237")
                .path("upload/image")
                .build();
        String url = uri.toString();
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        HttpResponse response = null;
        try {

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            FileBody fileBody = new FileBody(file);
            builder.addPart("file", fileBody);

            builder.addTextBody("content-type", "multipart/form-data");
            builder.addTextBody("boundary", "----WebKitFormBoundary7MA4YWxkTrZu0gW");
            HttpEntity entity = builder.build();

            httppost.setEntity(entity);

// Execute HTTP Post Request
            response = httpclient.execute(httppost);
// Log.i( "HttpManager:", "======> response: "
// + response.getEntity().getContent() );

        } catch (ClientProtocolException e) {
            Log.e("HttpManager", "ClientProtocolException thrown" + e);
        } catch (IOException e) {
            Log.e("HttpManager", "IOException thrown" + e);
        }
    }
}