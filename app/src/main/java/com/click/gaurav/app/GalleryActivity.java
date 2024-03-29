package com.click.gaurav.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.HttpResponse;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryActivity extends AppCompatActivity {
    private static Context context;
    private static ImageLoader imageLoader;
    private ImageAdapter imageAdapter;
    private static List<String> deleteKeys;
    private static int sizeDelete = 0;
    private static boolean isMyUploads = true;
    private static GetXMLTask task;
    private long sizeurl = 0;
    int i = 0;
    View thumb1View;
    ArrayList<String> urls = new ArrayList<>();
    ArrayList<ImageAdapterPojo> imageAdapterPojos = new ArrayList<>();
    Map<String, ImagePojo> listImagePojo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);
        GalleryActivity.context = getApplicationContext();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        imageLoader.clearMemoryCache();
        imageLoader.clearDiskCache();
        thumb1View = findViewById(R.id.expanded_image);
        GridView gridview = (GridView) findViewById(R.id.gridview1);
        imageAdapter = new ImageAdapter(context, imageAdapterPojos, thumb1View);
        gridview.setAdapter(imageAdapter);
        Toast.makeText(context, "Loading...",
                Toast.LENGTH_SHORT).show();
        // Create an object for subclass of AsyncTask
        task = new GetXMLTask();
        // Execute the task
        task.execute();
    }

    @Override
    public void onBackPressed() {
        sizeDelete = imageAdapter.deletedapterPojos.size();
        if (MainActivity.isMyupload) {
            if (MainActivity.isLoggedin) {
                if (sizeDelete != 0) {
                    if (sizeDelete == 1) {
                        Toast.makeText(getApplicationContext(), "Deleting " + imageAdapter.deletedapterPojos.size() + " photo", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Deleting " + imageAdapter.deletedapterPojos.size() + " photos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            deleteKeys = new ArrayList<>();
            for (ImageAdapterPojo imageAdapterPojoTemp : imageAdapter.deletedapterPojos) {
                deleteKeys.add(listImagePojo.get(imageAdapterPojoTemp.getUrl()).getKey());
            }
            DeleteTask taskD = new DeleteTask();
            // Execute the task
            taskD.execute();
        }
        this.finish();
    }

    private class GetXMLTask extends AsyncTask {

        @Override
        protected Bitmap doInBackground(Object... urlss) {
            if (MainActivity.isMyupload) {
                getMyImageUrls();
            } else {
                getImageUrls();
            }
            sizeurl = urls.size();
            if (urls.size() == 1 && TextUtils.isEmpty(urls.get(0))) {
                isMyUploads = false;
//                startActivity(new Intent(GalleryActivity.this, MainActivity.class));
                cancel(true);
            } else {
                for (String url : urls) {
                    ImagePojo imagePojo = new ImagePojo();
                    imagePojo.setKey(url);
                    String cdnurl = "https://d2lr53p66nyh6o.cloudfront.net";
                    String s3url = "https://s3.ap-south-1.amazonaws.com/my-bucket-images";
                    url = cdnurl + "/" + url;
                    imagePojo.setUrl(url);
                    listImagePojo.put(imagePojo.getUrl(), imagePojo);
                    //System.out.print("url:" + url + " ");
                }
            }
            return null;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Object result) {
            for (Map.Entry<String, ImagePojo> entry : listImagePojo.entrySet()) {
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.drawable.ic_launcher_background) // resource or drawable
                        .showImageForEmptyUri(R.drawable.ic_launcher_background) // resource or drawable
                        .showImageOnFail(R.drawable.ic_launcher_background) // resource or drawable
                        .resetViewBeforeLoading(false) // default
                        .delayBeforeLoading(1)
                        .cacheInMemory(true) // default
                        .cacheOnDisk(false) // default
                        .considerExifParams(false) // default
                        .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                        .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                        .displayer(new SimpleBitmapDisplayer()) // default
                        .handler(new Handler()) // default
                        .build();

                imageLoader.loadImage(entry.getKey(), options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        i++;
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        ImagePojo imagePojoTemp = listImagePojo.get(imageUri);
                        imagePojoTemp.setBitmap(loadedImage);
                        listImagePojo.put(imageUri, imagePojoTemp);

                        ImageAdapterPojo imageAdapterPojo = new ImageAdapterPojo(imageUri, loadedImage);
                        imageAdapterPojos.add(imageAdapterPojo);

                        imageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }

                });

            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(context, "No uploads till now",
                    Toast.LENGTH_SHORT).show();
            // Toast.makeText(getApplicationContext(), "asynctack cancelled.....", Toast.LENGTH_SHORT).show();
            super.onCancelled();
            startActivity(new Intent(GalleryActivity.this, MainActivity.class));
        }
    }

    private void getImageUrls() {
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
                    urls.add(urlString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("Server response", String.valueOf(urls.size()));
        } else {
            Log.i("Server response", "Failed to get server response");
        }
    }

    private void getMyImageUrls() {
        Uri uri = new Uri.Builder()
                .scheme("http")
                .authority("13.232.31.237")
                .path("get/user/image")
                .build();
        String url = uri.toString();
        HttpClient httpclient = new DefaultHttpClient();

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                1);
        nameValuePairs.add(new BasicNameValuePair("phoneNo", MainActivity.mobileNum));

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
                    urls.add(urlString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("Server response", String.valueOf(urls.size()));
        } else {
            Log.i("Server response", "Failed to get server response");
        }
    }

    private class DeleteTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object... urlss) {
            if (MainActivity.isLoggedin) {
                deleteImagesPost(deleteKeys);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (sizeDelete != 0) {
                if (MainActivity.isLoggedin) {
                    Toast.makeText(getApplicationContext(), "Deleted successfully ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Login to Delete ", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void deleteImagesPost(List<String> deleteKeys) {
        Uri uri = new Uri.Builder()
                .scheme("http")
                .authority("13.232.31.237")
                .path("delete/images")
                .build();
        String url = uri.toString();
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        HttpResponse response = null;
        try {
            String keyjoined = TextUtils.join(", ", deleteKeys);
// Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                    1);
            nameValuePairs.add(new BasicNameValuePair("keys", keyjoined));
            nameValuePairs.add(new BasicNameValuePair("phoneNo", MainActivity.mobileNum));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

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
//        // Creates Bitmap from InputStream and returns it
//        private Bitmap downloadImage(String url) {
//            Bitmap bitmap = null;
//            InputStream stream = null;
//            byte[] b;
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            bmOptions.inSampleSize = 1;
//
//            try {
//                stream = getHttpConnection(url);
//                if (stream != null)
//                    bitmap = BitmapFactory.
//                            decodeStream(stream);
//                if (stream != null) {
//                    stream.close();
//                }
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//            return bitmap;
//        }

// Makes HttpURLConnection and returns InputStream
//        private InputStream getHttpConnection(String urlString)
//                throws IOException {
//            InputStream stream = null;
//
//            //urlString = urlString.replaceAll("\\p{P}","");
//
//            URL url = new URL(urlString);
//            URLConnection connection = url.openConnection();
//
//            try {
//                HttpsURLConnection httpConnection = (HttpsURLConnection) connection;
//                httpConnection.setRequestMethod("GET");
//                httpConnection.connect();
//
//                if (httpConnection.getResponseCode() == HttpsURLConnection.HTTP_OK ||
//                        httpConnection.getResponseCode() == HttpsURLConnection.HTTP_NOT_MODIFIED) {
//
//                    stream = httpConnection.getInputStream();
//                } else { // just in case..
//
//                    //log.d("Surprize HTTP status was: " ,httpConnection.getResponseCode());
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            return stream;
//        }

//

//
//            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//
//        Uri uri = new Uri.Builder()
//                .scheme("https")
//                .authority("s3.ap-south-1.amazonaws.com")
//                .path("my-bucket-images/image1545832336555")
//                .build();
//
//        String url = uri.toString();
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                         imageByte=response.getBytes();
//                        //mTextView.setText("Response is: " + response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                mTextView.setText("That didn't work!" + error);
//            }
//        });
//
////        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
////                200000,
////                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
////                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//// Add the request to the RequestQueue.
//        queue.add(stringRequest);
//        return imageByte;


//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.gallery_layout);
//        mTextView = findViewById(R.id.test);
//        mImageView = findViewById(R.id.imagevid);
//        LinearLayout layout = (LinearLayout) findViewById(R.id.llout);
//        getImages();
////        for (int i = 0; i < 10; i++) {
////            ImageView image = new ImageView(this);
////            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(80, 60));
////            image.setMaxHeight(20);
////            image.setMaxWidth(20);
//       // if (imageArr != null)
//            mImageView.setImageBitmap(BitmapFactory. decodeByteArray(imageArr, 0, imageArr.length));
//        // Adds the view to the layout
//        //  layout.addView(image);
//        //   }
//
//    }
//

