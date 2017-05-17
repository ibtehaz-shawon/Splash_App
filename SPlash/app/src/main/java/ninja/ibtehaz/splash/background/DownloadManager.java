package ninja.ibtehaz.splash.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ninja.ibtehaz.splash.utility.Util;

/**
 * this class will be the new download manager to download files from the heroku server
 * without the uses of Async Call.
 * Will use Notification Manager to notifiy the user the download has commenced and finally stopped.
 * Created by ibtehaz on 5/17/17.
 * @author ibtehaz
 * @since 5/17/17
 */

public class DownloadManager extends Service {

    private final String TAG = "DownloadManager";
    private Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        final String downloadUrl = intent.getStringExtra(Util.EXTRA_SERVICE_CURRENT_DOWNLOAD_URL);

        new Thread() {
            @Override
            public void run() {
                //start notification here
                downloadPhoto(downloadUrl);
                context.stopService(intent);
            }
        }.start();

        return START_NOT_STICKY;
    }


    /**
     * Download the photo with the proper url
     * @param downloadUrl
     */
    private void downloadPhoto(String downloadUrl) {
        Bitmap output = null;
        try {
            URL url = new URL(downloadUrl);
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
                new Util().setupWallpaper(context, output);
            } else {
                Log.d(TAG, "_log: output is null. crashing issue occured!");
            }
        }
    }
}
