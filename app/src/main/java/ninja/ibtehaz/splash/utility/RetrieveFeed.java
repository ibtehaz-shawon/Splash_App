package ninja.ibtehaz.splash.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ibteh on 2/21/2017.
 */


public class RetrieveFeed extends AsyncTask<String, Void, Void> {

    private final String TAG = RetrieveFeed.class.getSimpleName();
    private Context context;
    private ProgressDialog progressDialog;
    private Util util;
    private CircularFillableLoaders loaders;
    private int width, height;
    private boolean flag;

    /**
     * @param context
     */
    public RetrieveFeed(Context context, CircularFillableLoaders loader, int width, int height) {
        this.context = context;
        this.util = new Util();
        this.loaders = loader;
        this.width = width;
        this.height = height;
        this.flag = false;
    }


    @Override
    protected Void doInBackground(String... params) {
        Bitmap output = null;
        try {
            URL url = new URL(params[0]);
            Log.d(TAG, "_log: output URL is: " + url.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            Log.d(TAG, "_log: options.inSampleSize " + options.inSampleSize);
            Log.d(TAG, "_log: Device width " + width + " height "+height);
            Log.d(TAG, "_log: options width " + options.outWidth + " height "+options.outHeight);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, width, height);

            /*---------------------------------------------------------------*/
            /*---------------------------------------------------------------*/
            /*---------------------------------------------------------------*/
            HttpURLConnection  secondaryConnection = (HttpURLConnection) url.openConnection();
            secondaryConnection.connect();
            InputStream secondaryStream = secondaryConnection.getInputStream();
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Log.d(TAG, "_log: options.inSampleSize " + options.inSampleSize);
            options.inScaled = true;

            if (options.outHeight * options.outWidth >= (4096 * 4096)) {
                return null;
            }
            Log.d(TAG, "_log: options.inSampleSize Manually " + options.inSampleSize);
            output = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(secondaryStream, null, options)
            , width, height, true);

            Log.d(TAG, "_log: output size " + output.getByteCount());

            inputStream.close();
            secondaryStream.close();
            connection.disconnect();
            secondaryConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(TAG, "_log: output with MalformedURLException " + e.toString());
        } catch (IOException e) {
            Log.d(TAG, "_log: output with IOException " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "_log: output with Exception " + e.toString());
            e.printStackTrace();
        } finally {
            if (output != null) {
                util.setupWallpaper(context, output);
                flag = true;
            }
            else Log.d(TAG, "_log: output is null");
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        loaders.setVisibility(View.VISIBLE);
        loaders.setProgress(60);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (loaders != null) {
            loaders.setVisibility(View.GONE);
        }
        if (flag) util.makeToast(context, "Complete!");
        else util.makeToast(context, "Failed!");
    }


    /**
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        Log.d(TAG, "_log: inSampleSize [After calculi] " + inSampleSize);
        return inSampleSize;
    }
}