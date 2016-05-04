package com.mac.chris.threadhandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {
    // Static so that the thread access the latest attribute
    private static ProgressDialog dialog;
    private static Bitmap downloadBitmap;
    private static Handler handler;
    private ImageView imageView;
    private Thread downloadThread;


    /** Called when the activity is first created. */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // create a handler to update the UI
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                imageView.setImageBitmap(downloadBitmap);
                dialog.dismiss();
            }

        };
        // get the latest imageView after restart of the application
        imageView = (ImageView) findViewById(R.id.imageView1);
        Context context = imageView.getContext();
        System.out.println(context);
        // Did we already download the image?
        if (downloadBitmap != null) {
            imageView.setImageBitmap(downloadBitmap);
        }
        // check if the thread is already running
        downloadThread = (Thread) getLastNonConfigurationInstance();
        if (downloadThread != null && downloadThread.isAlive()) {
            dialog = ProgressDialog.show(this, "Download", "downloading");
        }
    }

    public void resetPicture(View view) {
        if (downloadBitmap != null) {
            downloadBitmap = null;
        }
        imageView.setImageResource(R.drawable.icon);
    }

    public void downloadPicture(View view) {
        dialog = ProgressDialog.show(this, "Download", "downloading");
        downloadThread = new MyThread();
        downloadThread.start();
    }

    // save the thread
    @Override
    public Object onRetainNonConfigurationInstance() {
        return downloadThread;
    }

    // dismiss dialog if activity is destroyed
    @Override
    protected void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
    }

    // Utiliy method to download image from the internet
    private static Bitmap downloadBitmap(String url) throws IOException {
        InputStream is = null;

        URL myurl= new URL(url);
        HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.connect();
        is = conn.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;

    }

    public static class MyThread extends Thread {
        @Override
        public void run() {
            try {
                // Simulate a slow network
                try {
                    new Thread().sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                downloadBitmap = downloadBitmap("http://potrace.sourceforge.net/logo/potrace-logo-150.png");
                // Updates the user interface
                handler.sendEmptyMessage(0);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }
    }
}
