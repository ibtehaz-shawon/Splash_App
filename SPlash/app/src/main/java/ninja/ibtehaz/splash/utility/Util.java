package ninja.ibtehaz.splash.utility;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.db_helper.SplashDb;

/**
 * Created by ibteh on 2/20/2017.
 */

public class Util {


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
        String fileName = Calendar.getInstance().get(Calendar.MILLISECOND) + "";
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();

            //load based on id
            new SplashDb().updateFileName(fileName, dataId);
            Log.d(TAG, "FileName: "+ fileName);
        } catch (IOException e) {
            Log.d(TAG, "Exception "+e.toString());
            e.printStackTrace();
        } catch (Exception exc) {
            Log.d(TAG, "Exception "+exc.toString());
            exc.printStackTrace();
        }
    }
}
