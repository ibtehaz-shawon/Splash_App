package ninja.ibtehaz.splash.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import ninja.ibtehaz.splash.activity.NotificationViewActivity;
import ninja.ibtehaz.splash.db_helper.SplashDb;
import ninja.ibtehaz.splash.models.SplashDbModel;
import ninja.ibtehaz.splash.utility.Constant;
import ninja.ibtehaz.splash.utility.RetrieveFeed;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibtehaz on 4/11/2017.
 */

public class InternalDownloadService extends Service {

    private ArrayList<SplashDbModel> productUrls;
    private SplashDbModel splashDbModel;

    private final String TAG = "InternalStorage";
    private Context context;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int[] notificationId = new int[]{
            272847, 448612, 641112, 843429, 912219
    };

    private int downloadComplete = 0;
    private Intent runningIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }


    /**
     * this is where the magic happens
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.runningIntent = intent;
        this.splashDbModel = (SplashDbModel) intent.getSerializableExtra("data");
        productUrls = splashDbModel.getSplashDbModels();

        Constant instance = Constant.getInstance();
        instance.setRunningDownload(productUrls.size());

        for (int i = 0; i < productUrls.size(); i++) {
            final String rawUrl = productUrls.get(i).getUrlRaw();
            final long dataId = productUrls.get(i).getUniqueId();
            final int currentIndex = i;

            new Thread() {
                @Override
                public void run() {
                    /**
                     * TODO: this is a heavy place for race condition.
                     * TODO: this is suppose to run on a different thread. the values are changing in each looping
                     * TODO: make sure the connecting function does not carry any global variable unless explicitly needed
                     * TODO: possible breakthrough if race condition exist: stop the main service thread as long as this does return anything. Will halt all the parallel work of the service
                     */
                    networkCall(rawUrl, currentIndex, dataId);
                    if (downloadComplete == productUrls.size()) stopSelf();
                }
            }.start();
        }
        return START_NOT_STICKY; //if the service is stopped, don't reopen it, unless it has been explicitly called.
    }


    /**
     * TODO - need to check if internet connection is available.
     * network call to download a certain image from the internet
     * @param downloadUrl | current url of the file. Url is Raw
     * @param currentIndex | current index of the array item
     * @param dataId | current data id from the SQLITE db
     * @return a unnecessary integer value.
     */
    private int networkCall(String downloadUrl, int currentIndex, long dataId) {
        Bitmap output = null;
        try {
            showFirstNotification(currentIndex);
            new SplashDb().setDownloadStatus(0, dataId);

            URL url = new URL(downloadUrl);
            Log.d(TAG, "_log: output URL is: " + url.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 2048);
            ArrayList<Byte> imageData = new ArrayList<>();
            int current;

            while ((current = bufferedInputStream.read()) != -1) {
                imageData.add((byte) current);
            }

            byte[] imageArray = new byte[imageData.size()];

            notificationBuilder
                    .setContentTitle("Downloading Image " + (currentIndex + 1) + " from the list")
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
                showFinalNotification(currentIndex);
            } else {
                new SplashDb().setDownloadStatus(-1, dataId);
                Log.d(TAG, "_log: output is null");
            }
        }

        downloadComplete++;
        return 1001; //this is a useless number.
    }


    /**
     * creating a notification Intent with pending intent to view the progress of download on the view
     * shows notification for current index
     * @param currentIndex | current index of array to show notification icon
     */
    private void showFirstNotification(int currentIndex) {
        Intent notificationIntent = new Intent(context, NotificationViewActivity.class);
        notificationIntent.setFlags
                (Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("splashDbModel", splashDbModel);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, notificationId[currentIndex], notificationIntent, 0);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Downloading Image "+(currentIndex + 1) + " from the list")
                .setContentText("Download in progress. It may take a while")
                .setSmallIcon(R.drawable.placeholder_image)
                .setProgress(0, 0, true)
                .setAutoCancel(false)
                .setContentIntent(notificationPendingIntent)
                .setPriority(2)
                .setOngoing(true);
        notificationManager.notify(notificationId[currentIndex], notificationBuilder.build());

        ContentResolver contentResolver = context.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = context.getPackageName();

        // check to see if the enabledNotificationListeners String contains our
        // package name
        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
            // in this situation we know that the user has not granted the app
            // the Notification access permission
            // Check if notification is enabled for this application
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Log.i("InternalStorage", "Have Notification access");
        }
    }


    /**
     * this function posts the final notification on the system tray
     * @param currentIndex | current array list position
     */
    private void showFinalNotification(int currentIndex) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder
                .setContentTitle("Download completed of Image "+ (currentIndex + 1))
                .setContentText("")
                .setProgress(0, 0, false)
                .setSmallIcon(R.drawable.placeholder_image)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(0)
                .setOngoing(false);

        notificationManager.notify(notificationId[currentIndex], notificationBuilder.build());

        Constant instance = Constant.getInstance();
        instance.setRunningDownload(instance.getRunningDownload() - 1);
    }
}
