package com.example.gaurav.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Main2Activity extends AppCompatActivity {

    private TextView mTextView;
    private ImageView imageView;
    private static Context context;
    private ImageView imageView2;
    private static ImageLoader imageLoader;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> LIST_TYPE_REF =
            new TypeReference<List<String>>() {
            };
    List<String> urls = new ArrayList<>();

    List<String> fullUrls = new ArrayList<>();
    ;
    List<Bitmap> listmap = new ArrayList<>();

    public static final String urlImg = "https://s3.ap-south-1.amazonaws.com/my-bucket-images/image1545832336555";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Main2Activity.context = getApplicationContext();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        mTextView = findViewById(R.id.test);
        imageView = findViewById(R.id.imagevid);
        // imageView2 = findViewById(R.id.imagevid2);


        // Create an object for subclass of AsyncTask
        GetXMLTask task = new GetXMLTask();
        // Execute the task
        task.execute(new String[]{urlImg});


        //imageView2.setImageBitmap(listmap.get(1));

    }

    private class GetXMLTask extends AsyncTask {

        @Override
        protected Bitmap doInBackground(Object... urlss) {
            getImageUrls();
            System.out.print("listmap.size() before" + listmap.size());
            for (String url : urls) {

                String cdnurl = "https://d2lr53p66nyh6o.cloudfront.net";
                String s3url = "https://s3.ap-south-1.amazonaws.com/my-bucket-images";
                url = cdnurl + "/" + url;
                fullUrls.add(url);
                System.out.print("url:"+ url + " ");
                //Bitmap map = downloadImage(url);
                //listmap.add(map);
                //System.out.println("map:" + map);
            }
            System.out.print("listmap.size() after" + listmap.size());

            return null;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Object result) {
            // imageView.setImageBitmap((Bitmap) result);
            //imageView.setImageBitmap(listmap.get(0));

            LinearLayout layout = (LinearLayout) findViewById(R.id.llout);
            for (String url : fullUrls) {
                //System.out.println("map:" + map);
                ImageView image = new ImageView(getApplicationContext());
                image.setLayoutParams(new android.view.ViewGroup.LayoutParams(700, 700));
                image.setMaxHeight(20);
                image.setMaxWidth(20);

                imageLoader.displayImage(url, image);
                layout.addView(image);
//                if (map != null) {
//                    image.setImageBitmap(map);
//                    layout.addView(image);
//                }
            }
            mTextView.setText("Gallery");
            System.out.println("finished");
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            byte[] b;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                if (stream != null)
                    bitmap = BitmapFactory.
                            decodeStream(stream);
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;

            //urlString = urlString.replaceAll("\\p{P}","");

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpsURLConnection httpConnection = (HttpsURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpsURLConnection.HTTP_OK ||
                        httpConnection.getResponseCode() == HttpsURLConnection.HTTP_NOT_MODIFIED) {

                    stream = httpConnection.getInputStream();
                } else { // just in case..

                    //log.d("Surprize HTTP status was: " ,httpConnection.getResponseCode());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }
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
//        setContentView(R.layout.activity_main2);
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
            Log.i("Server response", urls.get(0));
        } else {
            Log.i("Server response", "Failed to get server response");
        }


//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        GsonRequest<String[]> req =
//                new GsonRequest<String[]>(url, String[].class,
//
//                        new Response.Listener<String[]>() {
//                            @Override
//                            public void onResponse(String[] response) {
//                                List<String> urls = Arrays.asList(response);
//
//                            }
//
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        mTextView.setText("That didn't work!" + error);
//                    }
//                });
//
//        queue.add(req);


//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//
////                        String str = "";
////                        try {
////                            str = new String(response.getBytes(), "UTF-8");
////                        } catch (UnsupportedEncodingException e) {
////
////                            e.printStackTrace();
////                        }
////                        String decodedStr = Html.fromHtml(str).toString();
//                        System.out.print("urls="+response);
//
//
//                    //    mTextView.setText("Response is: " + response.getBytes());
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
    }
}

