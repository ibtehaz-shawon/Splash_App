package ninja.ibtehaz.splash.utility;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.activity.NotificationViewActivity;
import ninja.ibtehaz.splash.background.InternalAsyncDownload;
import ninja.ibtehaz.splash.background.InternalDownloadService;
import ninja.ibtehaz.splash.background.NLService;
import ninja.ibtehaz.splash.db_helper.SplashDb;
import ninja.ibtehaz.splash.models.SplashDbModel;

/**
 * Created by ibteh on 2/20/2017.
 */

public class Util {

    private int [] notificationId = new int[] {
            102524, 102534, 101534, 902534, 332534
    };

    /**
     * singleton instance of Util to handle notification stuffs
     * @return
     */
    public Util getInstance() {
        return new Util();
    }

    /**
     *
     * @param context
     * @param parentView
     * @param message
     */
    public void showSnackbar(Context context, View parentView, String message) {
        Snackbar snackbar = Snackbar
                .make(parentView, message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextSize(context.getResources().getDimension(R.dimen.text_12sp));
        snackbar.show();
    }


    /**
     *
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            Log.e("hideSoftKeyboard", "Has focus to close" + view.getTag());
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (inputManager != null) {
                if (Build.VERSION.SDK_INT < 11) {
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                            0);
                } else {
                    if (activity.getCurrentFocus() != null) {
                        inputManager.hideSoftInputFromWindow(activity
                                        .getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    view.clearFocus();
                }
                view.clearFocus();
            }
        } else {
            Log.e("focus", "No focus to close");
        }
    }


    /**
     *
     * @param context
     * @return
     */
    public boolean isConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    /**
     * only load image
     * @param context
     * @param url
     * @param img
     */
    public void loadImage(Context context, String url, ImageView img) {
        Glide.with(context)
                .load(url)
                .fitCenter()
                .into(img);
    }

    /**
     *
     * @param context
     * @param url
     * @param img
     */
    public void loadImage(Context context, String url, final ImageView img, final ImageView imgLayer) {
        Glide.with(context)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (imgLayer != null)imgLayer.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (imgLayer != null)imgLayer.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(img);
    }


    /**
     *
     * @param context
     * @param url
     * @param img
     * @param progressBar
     */
    public void loadImage(Context context, String url, final ImageView img, final ProgressBar progressBar) {
        Glide.with(context)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(img);
    }


    /**
     * loads profile pic only
     * @param context
     * @param url
     * @param img
     */
    public void loadProfilePic(Context context, String url, ImageView img) {
        Glide.with(context)
                .load(url)
                .into(img);
    }


    /**
     *
     * @param message
     * @param context
     */
    public void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    /**
     * creates a scrollable website.
     * sets up wallpaper into an image
     * @param context
     * @param image
     */
    public void setupWallpaper(Context context, Bitmap image) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        width *= 2;
        try {
            if (image == null) {
                makeToast(context, "Image is null!");
            } else {
                float scale = width / (float) image.getWidth();
                height = (int) (scale * image.getHeight());
                Bitmap scaledImage = Bitmap.createScaledBitmap(image, width,height, true);
                Log.d("RetrieveFeed", "Scaled Image size "+scaledImage.getByteCount());
                wallpaperManager.setBitmap(scaledImage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    /**
     *
     * @param Url
     */
    public void setupWallpaperFromBackground(final Context context, String Url, final
                                             CircularFillableLoaders loader, Activity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        RetrieveFeed retrieveFeed = new RetrieveFeed(context, loader, false, -1);
        retrieveFeed.execute(Url);
    }


    /**
     * returns the capitalize version of a string/each string
     * @param word
     * @return
     */
    public String capitalizeWords(String word) {
        String []words = word.split(" ");
        String returnVal = "";
        for (int i =0; i < words.length; i++) {
            returnVal += words[i].substring(0,1).toUpperCase()
                    + words[i].substring(1).toLowerCase() +" ";
        }
        return returnVal;
    }


    /**
     * downloads the image from internet to Internal storage
     * Filename has to be sent to database based on ID
     * This function will be called from splashDB to download the image
     * @see SplashDb
     * @param rawUrl | raw url of the current photo
     * @param databaseId | sqlite unique id
     * @param context
     */
    public void downloadImageToStore(String rawUrl, long databaseId, Context context) {
        RetrieveFeed retrieveFeed = new RetrieveFeed(context, null, true, databaseId);
        retrieveFeed.execute(rawUrl);
    }


    /**
     * store images in android's internal storage
     * this function will be called from RetrieveFeed Async class to load image on background rather than
     * foreground
     * @see RetrieveFeed
     * @param image | just downloaded image from the server
     * @param context
     * @param dataId | sqlite primary key
     */
    public void storeImageInternalStorage(Bitmap image, Context context, long dataId) {
        String TAG = "InternalStorage";
        try {
            String fileName = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + ""
                    + Calendar.getInstance().get(Calendar.MINUTE) + ""
                    + Calendar.getInstance().get(Calendar.SECOND) + ""
                    + Calendar.getInstance().get(Calendar.MILLISECOND)+".jpeg";

            // path to /data/data/yourapp/app_data/splashDir
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.close();

            //load based on id
            new SplashDb().updateFileName(fileName, dataId);
            Log.d(TAG, "FileName: "+fileName);
        } catch (IOException e) {
            Log.d(TAG, "Exception "+e.toString());
            e.printStackTrace();
        } catch (Exception exc) {
            Log.d(TAG, "Exception "+exc.toString());
            exc.printStackTrace();
        }
    }


    /**
     * pulls the Internally stored photo from local storage and show it in an ImageView
     * @param fileName | name of the JPEG file.
     * @param context
     * @param imgPreview | to preview
     */
    public void getInternalStorageImage(String fileName, Context context, ImageView imgPreview) {
        try {
            File f = new File(context.getFilesDir(), fileName);
            Bitmap output = BitmapFactory.decodeStream(new FileInputStream(f));
            imgPreview.setImageBitmap(output);
        } catch (IOException iox) {
            iox.printStackTrace();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    /**
     * this function will start downloading photos for internal/in app storage.
     * this function will use notification to keep track of downloading.
     * TODO: need forground service to handle everything.
     * @param data contains SplashDB data
     * @param context handling context to put/do stuffs
     */
    public void startInternalImageDownload(ArrayList<SplashDbModel> data, Context context) {
        NotificationCompat.Builder notificationBuilder;
        NotificationManager notificationManager;
        /*
        *
        * creating a notification Intent with pending intent to view the progress of download on the view
        *
        **/
        Intent notificationIntent = new Intent(context, NotificationViewActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        notificationIntent.putExtra("splashDbModel", data);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        for (int index = 0; index < data. size(); index++) {
            String rawUrl = data.get(index).getUrlRaw();
            long id = data.get(index).getUniqueId();

            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle("Downloading Image "+(index + 1) + " from the list")
                    .setContentText("Download in progress. It may take a while")
                    .setSmallIcon(R.drawable.placeholder_image)
                    .setProgress(0, 0, true)
                    .setAutoCancel(false)
                    .setContentIntent(notificationPendingIntent);
            // Start a lengthy operation in a background thread
            notificationManager.notify(notificationId[index], notificationBuilder.build());

            InternalAsyncDownload internalAsyncDownload = new InternalAsyncDownload(context, id,
                    data.size(), index, notificationBuilder, notificationManager);
            internalAsyncDownload.execute(rawUrl);
            Log.d("InternalStorage", " counter --> "+index + " arrayList "+data.size());
        }

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
    }
}
