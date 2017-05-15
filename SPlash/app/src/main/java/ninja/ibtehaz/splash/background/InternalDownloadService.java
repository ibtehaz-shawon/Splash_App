package ninja.ibtehaz.splash.background;

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
import ninja.ibtehaz.splash.models.SplashDbModel;
import ninja.ibtehaz.splash.utility.RetrieveFeed;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibteh on 4/11/2017.
 */

public class InternalDownloadService extends Service {

    private final String TAG = "InternalStorage";
    private Context context;
    private ArrayList<SplashDbModel> productUrls;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int [] notificationId = new int[] {
            102524, 102534, 101534, 902534, 332534
    };

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
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SplashDbModel splashDbModel = (SplashDbModel) intent.getSerializableExtra("data");
        productUrls = splashDbModel.getSplashDbModels();

        Log.d("InternalStorage", "Service start");
        new Thread(){
            @Override
            public void run() {
                super.run();
                for (int index = 0; index < productUrls.size(); index++) {
                    String rawUrl = productUrls.get(index).getUrlRaw();
                    long id = productUrls.get(index).getUniqueId();

                    showNotification(index);

                    InternalAsyncDownload internalAsyncDownload = new InternalAsyncDownload(context, id,
                            productUrls.size(), index, notificationBuilder, notificationManager);
                    internalAsyncDownload.execute(rawUrl);

                    Log.d("InternalStorage", " counter --> "+index + " arrayList "+productUrls.size());
                }
            }
        }.start();
        return START_STICKY;
    }



    /**
     * creating a notification Intent with pending intent to view the progress of download on the view
     * shows notification for current index
     * @param currentIndex
     */
    private void showNotification(int currentIndex) {
        Intent notificationIntent = new Intent(context, NotificationViewActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        //        notificationIntent.putExtra("splashDbModel", data);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Downloading Image "+(currentIndex + 1) + " from the list")
                .setContentText("Download in progress. It may take a while")
                .setSmallIcon(R.drawable.placeholder_image)
                .setProgress(0, 0, true)
                .setAutoCancel(false)
                .setContentIntent(notificationPendingIntent);
        // Start a lengthy operation in a background thread
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
            context.startActivity(intent);
        } else {
            Log.i("InternalStorage", "Have Notification access");
        }

        startForeground(notificationId[currentIndex], notificationBuilder.build());
    }
}
