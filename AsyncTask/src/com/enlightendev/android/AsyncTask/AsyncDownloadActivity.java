package com.enlightendev.android.AsyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

public class AsyncDownloadActivity extends Activity {

    /**
     * Debug Tag for logging debug output to LogCat
     */
    private final static String TAG = AsyncDownloadActivity.class.getSimpleName();

    /**
     * Default URL to download
     */
    private final static String mDefaultURL = "http://www.wired.com/images_blogs/gadgetlab/2011/12/new-prof.png";


    /**
     * User's selection of URL to download
     */
    private EditText mUrlEditText;

    /**
     * Image that's been downloaded
     */
    private ImageView mImageView;

    /**
     * Display progress of download
     */
    private ProgressDialog mProgressDialog;

    /**
     * @class DownloadTask
     *
     * @brief This class downloads a bitmap image in the background
     *        using AsyncTask.
     */
    private class DownloadTask extends AsyncTask<String, Integer, Bitmap> {

        /**
         * Called by the AsyncTask framework in the UI Thread to
         * perform initialization actions.
         */
        protected void onPreExecute() {
            /**
             * Show the progress dialog before starting the download
             * in a Background Thread.
             */
            showDialog("downloading via AsyncTask");
        }

        /**
         * Downloads bitmap in an AsyncTask background thread.
         *
         * @param urls The url of a bitmap image
         */
        protected Bitmap doInBackground(String... urls) {
            return downloadImage(urls[0]);
        }

        /**
         * Called after an operation executing in the background is
         * completed. It sets the bitmap image to an image view and
         * dismisses the progress dialog.
         *
         * @param image The bitmap image
         */
        protected void onPostExecute(Bitmap image) {
            /**
             * Dismiss the progress dialog.
             */
            dismissDialog();

            /**
             * Display the downloaded image to the user.
             */
            displayImage(image);
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        /**
         * Caches references to the EditText and ImageView objects in data members to optimize subsequent access.
         */
        mUrlEditText = (EditText) findViewById(R.id.mUrlEditText);
        mImageView = (ImageView) findViewById(R.id.mImageView);


    }

    /**
     * Display the Dialog to the User.
     *
     * @param message The String to display what download method was used.
     */
    public void showDialog(String message) {
        mProgressDialog = ProgressDialog.show(this, "Download", message);
    }

    /**
     * Dismiss the Dialog
     */
    public void dismissDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }


    /**
     * Show a toast, notifying a user of an error when retrieving a bitmap.
     */
    void showErrorToast(String errorString) {
        Toast.makeText(this, errorString, Toast.LENGTH_LONG).show();
    }

    /**
     * Display a downloaded bitmap image if it's non-null; otherwise,it reports an error via a Toast.
     *
     * @param image The bitmap image
     */
    void displayImage(Bitmap image)
    {
        if (mImageView == null)
            showErrorToast("Problem image view = null.");
        else if (image != null)
            mImageView.setImageBitmap(image);
        else
            showErrorToast("image is corrupted, please check the requested URL.");
    }

    /**
     * Download a bitmap image from the URL provided by the user.
     *
     * @param url
     *            The url where a bitmap image is located
     * @return the image bitmap or null if there was an error
     */
    private Bitmap downloadImage(String url) {
        /**
         * Use the default URL if the user doesn't supply one.
         */
        if (url.equals(""))
            url = mDefaultURL;

        try {
            /**
             * Connect to a remote server, download the contents of
             * the image, and provide access to it via an Input
             * Stream. */
            InputStream is = (InputStream) new URL(url).getContent();

            /**
             * Decode an InputStream into a Bitmap.
             */
            Bitmap image = BitmapFactory.decodeStream(is);
            return image;
        } catch (Exception e) {
            /**
             * Post error reports to the UI Thread.
             */
            this.runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Use a Toast to inform user that something
                     * has gone wrong.
                     */
                    showErrorToast("Error downloading image, please check the requested URL.");
                }
            });

            Log.e(TAG, "Error downloading image");
            e.printStackTrace();
            return null;
        }
    }



    /**
     * Called when a user clicks a button to download an image with AsyncTask.
     *
     * @param view The "Run Async" button
     */
    public void runAsyncTask(View view) {

        String url = getUrlString();

        hideKeyboard();

        new DownloadTask().execute(url);
    }

    /**
     * Called when a user clicks a button to reset an image to default.
     *
     * @param view
     *            The "Reset Image" button
     */
    public void resetImage(View view) {
        mImageView.setImageResource(R.drawable.new_prof);
    }



    /**
     * Read the URL EditText and return the String it contains.
     *
     * @return String value in mUrlEditText
     */
    String getUrlString() {
        return mUrlEditText.getText().toString();
    }

    /**
     * Hide the keyboard after a user has finished typing the url.
     */
    private void hideKeyboard() {
        InputMethodManager mgr = (InputMethodManager) getSystemService (Context.INPUT_METHOD_SERVICE);

        mgr.hideSoftInputFromWindow(mUrlEditText.getWindowToken(), 0);
    }
}
