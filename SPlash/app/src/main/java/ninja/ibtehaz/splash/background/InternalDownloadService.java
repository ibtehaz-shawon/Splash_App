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
import ninja.ibtehaz.splash.utility.Constant;
import ninja.ibtehaz.splash.utility.RetrieveFeed;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibteh on 4/11/2017.
 */

public class InternalDownloadService extends Service {

    private final String TAG = "InternalStorage";
    private Context context;
    private ArrayList<SplashDbModel> productUrls;
    private SplashDbModel splashDbModel;

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
        this.splashDbModel = (SplashDbModel) intent.getSerializableExtra("data");
        productUrls = splashDbModel.getSplashDbModels();

        Log.d("InternalStorage", "Service start");
        Constant instance = Constant.getInstance();
        instance.setRunningDownload(productUrls.size());

        new Thread() {
            @Override
            public void run() {
                for (int index = 0; index < productUrls.size(); index++) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String rawUrl = productUrls.get(index).getUrlRaw();
                    long id = productUrls.get(index).getUniqueId();

                    Log.d("InternalStorage", "Inside the thread");
                    InternalAsyncDownload internalAsyncDownload = new InternalAsyncDownload(context, id,
                            productUrls.size(), index, splashDbModel);
                    internalAsyncDownload.execute(rawUrl);
                    Log.d("InternalStorage", " counter --> "+index + " arrayList "+productUrls.size());
                }
            }
        }.start();

        return START_STICKY;
    }
}
