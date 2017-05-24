package ninja.ibtehaz.splash.db_helper;

import android.content.Context;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ninja.ibtehaz.splash.models.FeedModel;
import ninja.ibtehaz.splash.models.SplashDbModel;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibteh on 4/1/2017.
 */

public class SplashDb extends SugarRecord {

    @Ignore
    private final String TAG = "SplashDb";

    @Unique
    private int id;
    private String localFileName; //current time function in milli second along with random generated text
    private String urlRaw;
    private String urlSmall;
    private boolean offline;
    private boolean isSet;
    @Unique
    private String imageId;
    private boolean isDailyWallpaper;
    //use this field to determine whether you have the image downloaded in local (if Offline mode is on)
    //check from settings and Feed.
    private int quality;
    private int wallpaperChangeTime;
    private int wallpaperAmount;
    private String collectionId;


    /**
     * default constructor
     */
    public SplashDb() {
    }


    /**
     * creates a database to store the database and offline/online preferences
     * @param feedModel
     * @param offline
     * @overload SplashDb()
     */
    public SplashDb(FeedModel feedModel, boolean offline, String localImageId,
                    boolean isDailyWallpaper) {
        this.urlRaw = feedModel.getUrlRaw();
        this.urlSmall = feedModel.getUrlSmall();
        this.imageId = feedModel.getPhotoId();
        this.isSet = false;
        this.offline = offline;
        this.localFileName = localImageId;
        this.isDailyWallpaper = isDailyWallpaper;
    }

    /**
     * -------------------------------------------------------------
     * -------------------------------------------------------------
     * -------------------------------------------------------------
     * -------------------------------------------------------------
     */
    /**
     * getter and setter
     */
    public String getUrlRaw() {
        return urlRaw;
    }

    public boolean isOffline() {
        return offline;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public void setSet(boolean set) {
        isSet = set;
    }

    public String getImageId() {
        return imageId;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public String getUrlSmall() {
        return urlSmall;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public int getWallpaperChangeTime() {
        return wallpaperChangeTime;
    }

    public void setWallpaperChangeTime(int wallpaperChangeTime) {
        this.wallpaperChangeTime = wallpaperChangeTime;
    }

    public int getWallpaperAmount() {
        return wallpaperAmount;
    }

    public void setWallpaperAmount(int wallpaperAmount) {
        this.wallpaperAmount = wallpaperAmount;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * -------------------------------------------------------------
     * -------------------------------------------------------------
     * -------------------------------------------------------------
     * -------------------------------------------------------------
     */

    /**
     * returns a single image, the first image from index 0, which has not been set to Wallpaper yet
     * @return SplashDBModel
     */
    private SplashDbModel returnLatestWallpaper() {
        List<SplashDb> allData = SplashDb.listAll(SplashDb.class);
        boolean isAllSet = true;
        SplashDb splashDb = null;

        for (int i = 0; i < allData.size(); i++) {
            if (!allData.get(i).isSet) {
                isAllSet = false;
                splashDb = allData.get(i);
                break;
            }
        }

        if (isAllSet) {
            reInitSplash(allData);
            splashDb = allData.get(0);
        }

        SplashDbModel dataModel = new SplashDbModel();
        dataModel.setSet(splashDb.isSet());
        dataModel.setUrlRaw(splashDb.getUrlRaw());
        dataModel.setUrlSmall(splashDb.getUrlSmall());
        dataModel.setImageId(splashDb.getImageId());
        dataModel.setLocalFileName(splashDb.getLocalFileName());
        splashDb.setOffline(splashDb.isOffline());

        return dataModel;
    }


    /**
     * this will re-initiate splash if it has been filled and running in offline mode
     * computation will be done in another part.
     * TODO: need to compute if the internet collection is available. Take decision based on that.
     * TODO: If the internet is on, get the new data from the internet,
     * TODO: if need to download then start downloading one by one
     * @param data
     */
    private void reInitSplash(List<SplashDb> data) {
        for (int i = 0; i < data.size(); i++) {
            Long id = data.get(i).getId();

            SplashDb reInit = SplashDb.findById(SplashDb.class, id);
            reInit.setSet(false);
            reInit.save();
        }
    }


    /**
     * insert data to splash DB
     * this will only insert feed data
     * check for duplicate image data by ImageId.
     * @param data
     * @return ArrayList<String> duplicate image id.
     */
    public ArrayList<String> insertFeedData(ArrayList<FeedModel> data) {
        //amount is 10;
        ArrayList<String> duplicate = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (!checkDuplicate(data.get(i).getPhotoId())) {
                SplashDb splash = new SplashDb(data.get(i), false, null, true);
                splash.setCollectionId(null);
                long id = splash.save();

                Log.d(TAG, "successfully inserted :"+id);
            } else {
                duplicate.add(data.get(i).getPhotoId());
            }
        }
        return duplicate;
    }


    /**
     * sweden dream
     * @param feedModel
     * @return
     */
    public boolean insertSingleImageData(FeedModel feedModel) {
        if (!checkDuplicate(feedModel.getPhotoId())) {
            SplashDb splashDb = new SplashDb(feedModel, false, null, false);
            splashDb.save();
            return true;
        } else {
            return false;
        }
    }


    /**
     * checks for duplicate data on the db based on imageId
     * @param imageId -> actual imageId from splash
     * @return boolean; true if DUPLICATE
     */
    private boolean checkDuplicate(String imageId) {
        List<SplashDb> data = SplashDb.find(SplashDb.class, "IMAGE_ID = ?", imageId);

        if (data == null) {
            return false;
        }
        if (data.size() == 0) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * remove all data from splash DB
     * @param context | necessary to remove internal storage file.
     */
    public void removeAll(Context context) {
        SplashDb splashDb = new SplashDb();
        //first delete if anything is present in the internal storage
        ArrayList<SplashDbModel> allImages = returnAllImage();
        for (int i = 0; i < allImages.size(); i++) {
            if (allImages.get(i).isOffline() && allImages.get(i).getLocalFileName() != null) {
                File file = new File(context.getFilesDir(), allImages.get(i).getLocalFileName());
                file.delete();
            }
        }
        splashDb.deleteAll(SplashDb.class);
    }


    /**
     * returns all image data from local db to settings page for viewing purpose.
     * @return ArrayList<> of SplashDbModel (ImageId, urlSmall and urlRaw is being sent)
     * @see SplashDbModel
     */
    public ArrayList<SplashDbModel> returnAllImage() {
        List<SplashDb> allData = SplashDb.listAll(SplashDb.class);
        ArrayList<SplashDbModel> returnModel = new ArrayList<>();

        for (int i = 0; i < allData.size(); i++) {
            SplashDbModel feedModel = new SplashDbModel();
            feedModel.setImageId(allData.get(i).getImageId());
            feedModel.setUrlSmall(allData.get(i).getUrlSmall());
            feedModel.setUrlRaw(allData.get(i).getUrlRaw());
            feedModel.setLocalFileName(allData.get(i).getLocalFileName());
            feedModel.setOffline(allData.get(i).isOffline());

            returnModel.add(feedModel);
        }
        return returnModel;
    }


    /**
     * updates the filename of a particular image for local storage use
     * @param fileName
     * @param dataId
     */
    public void updateFileName(String fileName, long dataId) {
        SplashDb data = SplashDb.findById(SplashDb.class, dataId);
        data.setLocalFileName(fileName);
        data.save();
    }


    /**
     * This function will be used only if the image has to be stored locally.
     * User will prefer this. Otherwise, Image will not be stored locally
     * User will get either randomly generated photo list from server or photos from the list (user preference)
     * @param data | all feedModel data containing image data
     * @param context | context is needed in order to access InternalStorage
     */
    public int insertLocalImage(ArrayList<FeedModel> data, Context context, int quality, int wallpaperChangeTime, int wallpaperAmount) {

        int duplicate = 0;
        ArrayList<SplashDbModel> dataModel = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (!checkDuplicate(data.get(i).getPhotoId())) {
                SplashDb splash = new SplashDb(data.get(i), true, null, true);
                splash.setQuality(quality);
                splash.setWallpaperAmount(wallpaperAmount);
                splash.setWallpaperChangeTime(wallpaperChangeTime);
                splash.setCollectionId(null);
                long id = splash.save();

                //call the util function to store data to Internal Storage
                SplashDbModel tempModel = new SplashDbModel();
                tempModel.setImageId(splash.getImageId());
                tempModel.setUrlRaw(splash.getUrlRaw());
                tempModel.setUrlSmall(splash.getUrlSmall());
                tempModel.setQuality(quality);
                tempModel.setUniqueId(id);

                dataModel.add(tempModel);
            } else {
                duplicate++;
            }
        }
        new Util().startInternalImageDownload(dataModel, context);
        return 0; //this value will return zero Always. means nothing.
    }


    /**
     * this function checks if all the download is completed for offline datas.
     * if not, will initiate a new download, if the current download is not running
     */
    public void isDownloadComplete(Context context) {
        return;
//        Constant instance = Constant.getInstance();
//        Log.d("InternalStorage", "Counter is --> "+instance.getRunningDownload());
//
//        if (instance.getRunningDownload() <= 0) {
//            //download i
//            List<SplashDb> allData = SplashDb.listAll(SplashDb.class);
//            ArrayList<SplashDbModel> downloadModel = new ArrayList<>();
//
//            for (int i = 0; i < allData.size(); i++) {
//                boolean isOffline = allData.get(i).isOffline();
//                String fileName = allData.get(i).getLocalFileName();
//
//                if (isOffline) {
//                   if (fileName == null) {
//                       //start downloading
//                       SplashDbModel tempModel = new SplashDbModel();
//                       tempModel.setImageId(allData.get(i).getImageId());
//                       tempModel.setUrlRaw(allData.get(i).getUrlRaw());
//                       tempModel.setUrlSmall(allData.get(i).getUrlSmall());
//                       tempModel.setUniqueId(allData.get(i).getId());
//
//                       downloadModel.add(tempModel);
//                   } else if (fileName.equals("")) {
//                       SplashDbModel tempModel = new SplashDbModel();
//                       tempModel.setImageId(allData.get(i).getImageId());
//                       tempModel.setUrlRaw(allData.get(i).getUrlRaw());
//                       tempModel.setUrlSmall(allData.get(i).getUrlSmall());
//                       tempModel.setUniqueId(allData.get(i).getId());
//
//                       downloadModel.add(tempModel);
//                   }
//                }
//            }
//            if (downloadModel.size() > 0) new Util().startInternalImageDownload(downloadModel, context);
//        }
    }


    /**
     * @deprecated
     * @since 24/05/2017
     * updates the database that download has started.
     * @param downloadStarted
     * @param dataId
     */
    public void setDownloadStatus(int downloadStarted, long dataId) {
//        SplashDb data = SplashDb.findById(SplashDb.class, dataId);
//        data.save();
        return;
    }


    /**
     * returns true, if it's the first time database is being initialized
     */
    public boolean isFirstTime() {
        List<SplashDb> data = SplashDb.listAll(SplashDb.class);

        if (data.size() == 0) return true;
        else return false;
    }


    /**
     * return the number of photo data to be stored
     * @return wallpapaer amount
     */
    public int returnDownloadAmount() {
        List<SplashDb> allData = SplashDb.listAll(SplashDb.class);

        if (allData.size() == 0) return 0;
        else return allData.get(0).getWallpaperAmount();
    }


    /**
     * returns the nutime when the photo will be changed
     * @return Wallpaper change time.
     */
    public int returnChangeTime() {
        List<SplashDb> allData = SplashDb.listAll(SplashDb.class);

        if (allData.size() == 0) return 0;
        else return allData.get(0).getWallpaperChangeTime();
    }
}
