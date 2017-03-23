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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;

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

            HttpURLConnection  connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 2048);
            ArrayList<Byte> imageData = new ArrayList<>();
            int current;

            while ((current = bufferedInputStream.read()) != -1) {
                imageData.add((byte)current);
            }

            Log.d(TAG, "_log: imageData size:--> " +imageData.size());
            byte[] imageArray = new byte[imageData.size()];

            for (int i = 0; i < imageData.size(); i++) {
                imageArray[i] = imageData.get(i);
            }

            Log.d(TAG, "_log: imageArray size:--> " +imageArray.length);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Log.d(TAG, "_log: options.inSampleSize " + options.inSampleSize);
            options.inScaled = true;
            options.inSampleSize = 2;

            Log.d(TAG, "_log: options.inSampleSize Manually " + options.inSampleSize);
            output = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length, options);
            Log.d(TAG, "_log: output size " + output.getByteCount());

            inputStream.close();
            connection.disconnect();
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