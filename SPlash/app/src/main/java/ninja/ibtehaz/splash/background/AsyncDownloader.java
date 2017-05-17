package ninja.ibtehaz.splash.background;

import android.app.IntentService;
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
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibtehaz on 5/17/17.
 */

public class AsyncDownloader extends Service {

    private final String TAG = "InternalStorage";
    private Context context;
    private long dataId;
    private int currentIndex;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int[] notificationId = new int[]{
            272847, 448612, 641112, 843429, 912219
    };
    private SplashDbModel dataModel;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bitmap output = null;
        dataId = intent.getLongExtra(Util.EXTRA_SERVICE_DATA_ID, -1);
        currentIndex = intent.getIntExtra(Util.EXTRA_SERVICE_CURRENT_INDEX, 0);
        String downloadUrl = intent.getStringExtra(Util.EXTRA_SERVICE_CURRENT_DOWNLOAD_URL);
        dataModel = (SplashDbModel) intent.getSerializableExtra(Util.EXTRA_SERVICE_DATA_MODEL);

        Log.d(TAG, "_log: inside SERVICE $$$$$ ||-> " + AsyncDownloader.class.getCanonicalName());
        try {
            showFirstNotification();
            /**
             * updates the database that a download has commenced
             */
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
                showFinalNotification();

                Log.d(TAG, "_log: Service 1234 is stopping for "+currentIndex);
                stopSelf();
            } else {
                new SplashDb().setDownloadStatus(-1, dataId);
                Log.d(TAG, "_log: output is null");
            }
        }

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }


    /**
     * creating a notification Intent with pending intent to view the progress of download on the view
     * shows notification for current index
     */
    private void showFirstNotification() {
        Intent notificationIntent = new Intent(context, NotificationViewActivity.class);
        notificationIntent.setFlags
                (Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("splashDbModel", dataModel);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, notificationId[currentIndex], notificationIntent, 0);

        Log.d("InternalStorage", "Notification for index "+currentIndex);

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
            Log.i("InternalStorage", "Dont Have Notification access");
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Log.i("InternalStorage", "Have Notification access");
        }
    }


    /**
     *
     */
    private void showFinalNotification() {
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
