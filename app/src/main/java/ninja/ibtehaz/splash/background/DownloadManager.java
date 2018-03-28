package ninja.ibtehaz.splash.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ninja.ibtehaz.splash.R;
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
                if (new Util().isConnectionAvailable(context)) {
                    downloadPhoto(downloadUrl);
                } else {
                    String title = context.getString(R.string.wallpaper_setup_failed);
                    String message = context.getString(R.string.no_connection_available);
                    showFinalNotification(title, message, 2);
                }
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
            showFirstNotification();
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
                boolean isSuccess = new Util().setupWallpaper(context, output);
                if (isSuccess) {
                    String title = context.getString(R.string.wallpaper_setup_success);
                    String message = context.getString(R.string.wallpaper_message_success);

                    showFinalNotification(title, message, 2);
                } else {
                    String title = context.getString(R.string.wallpaper_setup_failed);
                    String message = context.getString(R.string.wallpaper_message);

                    showFinalNotification(title, message, 1);
                }
            } else {
                //show error message here
                Log.d(TAG, "_log: output is null. crashing issue occured!");

                String title = context.getString(R.string.wallpaper_setup_failed);
                String message = context.getString(R.string.wallpaper_message);

                showFinalNotification(title, message, 1);
            }
        }
    }


    /**
     * shows the first notification at the beginning of the download
     */
    private void showFirstNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Setting up a new wallpaper")
                .setContentText("Download is ongoing... It may take a while!")
                .setSmallIcon(R.drawable.placeholder_image)
                .setProgress(0, 0, true)
                .setAutoCancel(false)
                .setPriority(2)
                .setOngoing(true);

        notificationManager.notify(5005, notificationBuilder.build());

        ContentResolver contentResolver = context.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = context.getPackageName();

        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
            /**
             * asks for notification showing permission
             */
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Log.i("DownloadManager", "Have Notification access");
        }
    }


    /**
     * shows the final notification for the download status
     * @param title | title of the notification
     * @param message | message of the notification
     * @param priority | priority status ( 2 to -2)
     */
    private void showFinalNotification(String title, String message, int priority) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(title.trim())
                .setContentText(message.trim())
                .setProgress(0, 0, false)
                .setSmallIcon(R.drawable.placeholder_image)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(priority)
                .setOngoing(false);

        notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(5005, notificationBuilder.build());
    }
}
