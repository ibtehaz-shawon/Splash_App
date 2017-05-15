package ninja.ibtehaz.splash.background;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ninja.ibtehaz.splash.utility.RetrieveFeed;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibtehaz on 5/15/17.
 */

public class InternalAsyncDownload extends AsyncTask<String, Void, Void> {

    private final String TAG = RetrieveFeed.class.getSimpleName();
    private Context context;
    private Util util;
    private boolean flag;
    private long dataId;
    private int totalItem;
    private int currentIndex;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int notificationId = 102534;

    /**
     * @param context
     */
    public InternalAsyncDownload(Context context, long dataId,
                                 int totalItem, int currentIndex,
                                 NotificationCompat.Builder notificationBuilder,
                                 NotificationManager notificationManager) {
        this.context = context;
        this.util = new Util();
        this.flag = false;
        this.dataId = dataId;
        this.totalItem = totalItem;
        this.currentIndex = currentIndex;
        this.notificationBuilder = notificationBuilder;
        this.notificationManager = notificationManager;
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
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 2048);
            ArrayList<Byte> imageData = new ArrayList<>();
            int current;

            while ((current = bufferedInputStream.read()) != -1) {
                imageData.add((byte)current);
            }

            byte[] imageArray = new byte[imageData.size()];

            for (int i = 0; i < imageData.size(); i++) {
                imageArray[i] = imageData.get(i);
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inScaled = true;
            options.inSampleSize = 1;

            output = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length, options);

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
                new Util().storeImageInternalStorage(output, context, dataId);
            }
            else Log.d(TAG, "_log: output is null");
        }
        return null;
    }


    /**
     *
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    /**
     *
     * @param aVoid
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // When the loop is finished, updates the notification
        if (currentIndex >= totalItem - 1) {
            notificationBuilder.setContentTitle("Done.");
            notificationBuilder.setContentText("Download complete")
                    // Removes the progress bar
                    .setProgress(0, 0, false);
            notificationManager.notify(notificationId, notificationBuilder.build());
        } else {
            int per = (int) (((currentIndex + 1) / totalItem) * 100f);
            Log.i("Counter", "Counter : " + currentIndex + ", per : " + per);
            notificationBuilder.setContentText("Downloaded (" + per + "/100");
            notificationBuilder.setProgress(100, per, false);
            // Displays the progress bar for the first time.
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }

}
