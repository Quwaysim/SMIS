package com.smis.views.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.smis.R;
import com.smis.utils.CheckForSDCard;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class DownloadActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final int WRITE_REQUEST_CODE = 300;
    private static final String TAG = DownloadActivity.class.getSimpleName();
    private String url, fileName = "file";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        if (getIntent().hasExtra("url")) {
            url = getIntent().getStringExtra("url");
            //fileName = getIntent().getStringExtra("file");

            if (CheckForSDCard.isSDCardPresent()) {

                //check if app has permission to write to the external storage.
                if (EasyPermissions.hasPermissions(DownloadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Get the URL entered
                    new DownloadFile().execute(url);
                } else {
                    //If permission is not present request for the same.
                    EasyPermissions.requestPermissions(DownloadActivity.this, getString(R.string.write_file), WRITE_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                }


            } else {
                Toast.makeText(getApplicationContext(),
                        "SD Card not found", Toast.LENGTH_LONG).show();

            }


        }

    }


    /*
        @Override
        protected Dialog onCreateDialog(int id) {
            switch (id) {
                case progress_bar_type: // we set this to 0
                    pDialog = new ProgressDialog(this);
                    pDialog.setMessage("Downloading, please wait...");
                    pDialog.setIndeterminate(false);
                    pDialog.setMax(100);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pDialog.setCancelable(true);
                    pDialog.show();
                    return pDialog;
                default:
                    return null;
            }
        } */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, DownloadActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Download the file once permission is granted
        new DownloadFile().execute(url);
        // new DownloadFileFromURL().execute(url);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /*
    private class DownloadFileFromURL extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
    /*  @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DownloadActivity.this);
            pDialog.setMessage("Downloading, please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        /**
         * Downloading file in background thread
         * */
    /*  @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/smis");
                boolean success = true;
                if (!folder.exists()) {
                    folder.mkdir();
                }
                if (success) {
                    // Do something on success
                } else {
                    // Do something else on failure
                }
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                //extension must change (mp3,mp4,zip,apk etc.)
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() +"/smis/" +fileName+".extension");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
    /*  protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
    /* @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
           pDialog.dismiss();
           onBackPressed();
            Toast.makeText(getApplicationContext(),fileName+" downloaded. to" , Toast.LENGTH_LONG).show();
        }

    } */

    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(DownloadActivity.this);
            this.progressDialog.setMessage("Downloading, please wait...");
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name
                fileName = timestamp + "_" + fileName.substring(0, 5);

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "smis/";

                //Create smis folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            // Display File path after downloading
            Toast.makeText(getApplicationContext(),
                    message, Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }

}


