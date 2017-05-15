package ninja.ibtehaz.splash.background;

import android.app.Notification;
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

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.utility.RetrieveFeed;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibtehaz on 5/15/17.
 * this class download the internal images from the server and stores as bitmap in the devices
 * notifications are enabled and given hard coded notification ids.
 *
 */

public class InternalAsyncDownload extends AsyncTask<String, Void, Void> {

    private final String TAG = "InternalStorage";
    private Context context;
    private Util util;
    private boolean flag;
    private long dataId;
    private int totalItem;
    private int currentIndex;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int [] notificationId = new int[] {
            102524, 102534, 101534, 902534, 332534
    };

    /**
     *
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

            notificationBuilder
                    .setContentTitle("Downloading Image "+(currentIndex + 1) + " from the list")
                    .setContentText("It's almost over!")
                    .setSmallIcon(R.drawable.placeholder_image)
                    .setProgress(0, 0, true)
                    .setAutoCancel(false);
            // Start a lengthy operation in a background thread
            notificationManager.notify(notificationId[currentIndex], notificationBuilder.build());

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
        notificationBuilder
                .setContentTitle("Download completed of Image "+ (currentIndex + 1))
                .setContentText("")
                .setProgress(0, 0, false)
                .setSmallIcon(R.drawable.placeholder_image)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND);

        notificationManager.notify(notificationId[currentIndex], notificationBuilder.build());
    }

}
