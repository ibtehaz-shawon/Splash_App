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
                for (int index = 0; index < productUrls. size(); index++) {
                    String rawUrl = productUrls.get(index).getUrlRaw();
                    long id = productUrls.get(index).getUniqueId();

                    shama_pe_giya(rawUrl, id);
                    Log.d("InternalStorage", " counter --> "+index + " arrayList "+productUrls.size());

                    if (index == productUrls.size() - 1) {
                        Log.d("InternalStorage", " Tata service when "+index
                                + " and product url size "+ productUrls.size());
                        stopSelf();
                    }
                }
            }
        }.start();
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    private void shama_pe_giya(String rawUrl, long dataId) {
        Bitmap output = null;
        try {
            URL url = new URL(rawUrl);
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
    }
}
