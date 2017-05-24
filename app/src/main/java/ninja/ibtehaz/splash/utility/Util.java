package ninja.ibtehaz.splash.utility;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.background.DownloadManager;
import ninja.ibtehaz.splash.background.InternalDownloadService;
import ninja.ibtehaz.splash.db_helper.SplashDb;
import ninja.ibtehaz.splash.models.SplashDbModel;

/**
 * Created by ibtehaz on 2/20/2017.
 */

public class Util {

    public static String NOTIFICATION_BROADCAST_CONSTANT = "14442";
    public static String NOTIFICATION_BROADCAST_ID_EXTRA = "uniqueId";

    public static String EXTRA_SERVICE_DATA_ID = "data_id";
    public static String EXTRA_SERVICE_CURRENT_INDEX = "current_index";
    public static String EXTRA_SERVICE_DATA_MODEL = "data_model";
    public static String EXTRA_SERVICE_CURRENT_DOWNLOAD_URL = "download_url";

    public static String [] MONTH_NAMES = new String[] {
            "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
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
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .crossFade()
                .centerCrop()
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
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .crossFade()
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
    public boolean setupWallpaper(Context context, Bitmap image) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        width *= 2;
        boolean isSuccess = false;
        try {
            if (image == null) {
                makeToast(context, "Image is null!");
            } else {
                float scale = width / (float) image.getWidth();
                height = (int) (scale * image.getHeight());
                Bitmap scaledImage = Bitmap.createScaledBitmap(image, width,height, true);
                Log.d("DownloadService", "Scaled Image size "+scaledImage.getByteCount());
                wallpaperManager.setBitmap(scaledImage);
                isSuccess = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return isSuccess;
    }


    /**
     * uses a service to download and later sets it up as wallpaper.
     * @param downloadUrl
     * @param context
     */
    public void setupWallpaperFromBackground(Context context, String downloadUrl) {
        Intent i = new Intent(context, DownloadManager.class);
        i.putExtra(Util.EXTRA_SERVICE_CURRENT_DOWNLOAD_URL, downloadUrl);
        context.startService(i);
    }


    /**
     * returns the capitalize version of a string/each string
     * string size for any splitted index might become (min 1, maxif (words.length == 1)  = n)
     * @param word
     * @return Capitalized name for each words
     */
    public String capitalizeWords(String word) {
        String []words = word.split(" ");
        String returnVal = "";
        for (int i =0; i < words.length; i++) {
            if (words[i].length() > 1) {
                returnVal += words[i].substring(0,1).toUpperCase()
                        + words[i].substring(1).toLowerCase() +" ";
            } else {
                returnVal += words[i].substring(0).toUpperCase() + " ";
            }
        }
        return returnVal;
    }


    /**
     * store images in android's internal storage
     * this function will be called from Internal Download Service class to load image on background rather than foreground. Service runs in another process and as well as Async
     * @see InternalDownloadService
     * @param image | just downloaded image from the server
     * @param context
     * @param dataId | sqlite primary key
     */
    public void storeImageInternalStorage(Bitmap image, Context context, long dataId, int quality) {
        String TAG = "InternalStorage";
        try {
            String fileName = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + ""
                    + Calendar.getInstance().get(Calendar.MINUTE) + ""
                    + Calendar.getInstance().get(Calendar.SECOND) + ""
                    + Calendar.getInstance().get(Calendar.MILLISECOND)+".jpeg";

            // path to /data/data/yourapp/app_data/splashDir
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.close();

            //load based on id
            new SplashDb().updateFileName(fileName, dataId);
            Log.d(TAG, "FileName: "+fileName + " for "+dataId + " and bitmap size is "+image.getByteCount());

            //send a broadcast from here that the download is complete
            Intent intent = new Intent(Util.NOTIFICATION_BROADCAST_CONSTANT);
            intent.putExtra(Util.NOTIFICATION_BROADCAST_ID_EXTRA, dataId);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            Log.d(TAG, "------------- Broadcast fired -------------");
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
     * this function works on background thread of another process which is separated from main process.
     * @param data contains SplashDB data
     * @param context handling context to put/do stuffs
     */
    public void startInternalImageDownload(ArrayList<SplashDbModel> data, Context context) {
        Intent i = new Intent(context, InternalDownloadService.class);
        SplashDbModel local = new SplashDbModel();
        local.setSplashDbModels(data);
        i.putExtra("data", local);
        context.startService(i);
    }


    /**
     * fuck society
     * returns human readable data from 2017-02-03 12:28:06+00:00
     * @param date
     * @return DD MMM, YYYY format
     */
    public String dateParser(String date) {
        String []yearParsed = date.split(" ")[0].split("-");
        int monthPart = Integer.parseInt(yearParsed[1]);

        return yearParsed[2] + " "+ MONTH_NAMES[monthPart -1]+", "+yearParsed[0];
    }


    /**
     * receives a date (2017-02-03 12:28:06+00:00) and returns AGO text
     * @param date
     * @return 3 MMM/YYYY/DD/HH/MM/Just now ago
     */
    public String dateParserAgo(String date, Context context) {
        String []yearParsed = date.split(" ")[0].split("-");
        String []hourParsed = date.split(" ")[1].split(":");

        String updatedDate = yearParsed[0]+"-"+yearParsed[1]+"-"+yearParsed[2]+" "+hourParsed[0]+":"+hourParsed[1];
        return calculateDate(updatedDate, context);
    }


    /**
     * returns a fucking human readable text
     * @param date | yyyy-MM-dd HH:mm:ss date
     * @param context | current context
     * @return returns a bullshit date
     */
    public static String calculateDate(String date, Context context) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] dateTime = date.split(" "); //separate the space
        SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
        Date postCreationYear, postCreationHour;
        String whenCreated = "";

        try {
            postCreationYear = yearFormatter.parse(dateTime[0]);
            postCreationHour = timeFormatter.parse(dateTime[1]);
            Date today = yearFormatter.parse(yearFormatter.format(new Date())); //Today's date
            Calendar currentCalendar = GregorianCalendar.getInstance();  //todays calender
            Calendar postCalendar = GregorianCalendar.getInstance(); //polls calender

            //get how long ago the poll was created
            if (today.after(postCreationYear)) {
                currentCalendar.setTime(today);
                postCalendar.setTime(postCreationYear);
                int postYear = postCalendar.get(Calendar.YEAR);
                int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
                int postDate = postCalendar.get(Calendar.DAY_OF_MONTH);

                if (postDate < currentDay) {
                    if ((currentDay - postDate) <= 1) {
                        whenCreated = "" + (currentDay - postDate) + " " + context.getString(R.string.day_single);
                    } else if ((currentDay - postDate) > 1 && (currentDay - postDate) <= 30)
                        whenCreated = "" + (currentDay - postDate) + " " + context.getString(R.string.day_plural);
                    else {
                        //month
                        int currentMonth = currentCalendar.get(Calendar.MONTH);
                        int postMonth = postCalendar.get(Calendar.MONTH);

                        if (currentMonth - postMonth == 0) {
                            //check date here
                            whenCreated = "" + (currentMonth - postMonth) + " " + context.getString(R.string.month_singular);
                        } else if (currentMonth - postMonth == 1) {
                            // 1 or less month
                            whenCreated = "" + (currentMonth - postMonth) + " " + context.getString(R.string.month_singular);
                        } else if (currentMonth - postMonth > 1 && currentMonth - postMonth <= 12) {
                            whenCreated = "" + (currentMonth - postMonth) + " " + context.getString(R.string.month_plural);
                        } else {
                            //year
                            int currentYear = currentCalendar.get(Calendar.YEAR);
                            if (currentYear - postYear <= 1) {
                                whenCreated = "" + (currentYear - postYear) + " " + context.getString(R.string.year_singular);
                            } else {
                                whenCreated = "" + (currentYear - postYear) + " " + context.getString(R.string.year_plural);
                            }
                        }
                    }
                }

            } else if (today.equals(postCreationYear)) {
                Date now = new Date();
                currentCalendar.setTime(now);
                postCalendar.setTime(postCreationHour);
                int nowHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
                int nowMin = currentCalendar.get(Calendar.MINUTE);
                int postHour = postCalendar.get(Calendar.HOUR_OF_DAY);
                int postMin = postCalendar.get(Calendar.MINUTE);

                if (postHour < nowHour) {
                    if ((nowHour - postHour) == 1) {
                        whenCreated = "" + (nowHour - postHour) + " " + context.getString(R.string.hour_single);
                    } else
                        whenCreated = "" + (nowHour - postHour) + " " + context.getString(R.string.hour_plural);
                } else {
                    if ((nowMin - postMin) == 0) {
                        whenCreated = context.getString(R.string.just_now);
                    } else {
                        if ((nowMin - postMin) == 1) {
                            whenCreated = "" + (nowMin - postMin) + " " + context.getString(R.string.min_single);
                        } else
                            whenCreated = "" + (nowMin - postMin) + " " + context.getString(R.string.min_plural);
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (whenCreated.equalsIgnoreCase("")) {
                return date; //fail safe the orignal date if crashes
            } else {
                return whenCreated + " ago"; //return actual data
            }
        }
    }
}
