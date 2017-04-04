package ninja.ibtehaz.splash.db_helper;

import android.content.Context;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;

import java.util.ArrayList;
import java.util.List;

import ninja.ibtehaz.splash.models.FeedModel;
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
    private boolean isOffline;
    private boolean isSet;
    @Unique
    private String imageId;
    private boolean isDailyWallpaper;


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
        this.isOffline = offline;
        this.localFileName = localImageId;
        this.isDailyWallpaper = isDailyWallpaper;
    }

    /**
     * getter and setter
     */
    public String getUrlRaw() {
        return urlRaw;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
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

    /**
     *
     * @return
     */
    private SplashDb returnImageData() {
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
        return splashDb;
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
     */
    public void removeAll() {
        SplashDb splashDb = new SplashDb();
        splashDb.deleteAll(SplashDb.class);
    }


    /**
     * returns all image data from local db to settings page for viewing purpose.
     * @return ArrayList<> of FeedModel (ImageId, urlSmall and urlRaw is being sent)
     * @see FeedModel
     */
    public ArrayList<FeedModel> returnAllImage() {
        List<SplashDb> allData = SplashDb.listAll(SplashDb.class);
        ArrayList<FeedModel> returnModel = new ArrayList<>();

        for (int i = 0; i < allData.size(); i++) {
            FeedModel feedModel = new FeedModel();
            feedModel.setPhotoId(allData.get(i).getImageId());
            feedModel.setUrlSmall(allData.get(i).getUrlSmall());
            feedModel.setUrlRaw(allData.get(i).getUrlRaw());
            feedModel.setLocationName(allData.get(i).getLocalFileName());

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
     * @param data | all feedModel data containing image data
     * @param context | context is needed in order to access InternalStorage
     */
    public void insertLocalImage(ArrayList<FeedModel> data, Context context) {
        for (int i = 0; i < data.size(); i++) {
            if (!checkDuplicate(data.get(i).getPhotoId())) {
                SplashDb splash = new SplashDb(data.get(i), true, null, true);
                long id = splash.save();

                //call the util function to store data to Internal Storage
                Log.d(TAG, "successfully inserted :"+id);
                new Util().downloadImageToStore(splash.getUrlRaw(), id, context);
            }
        }
    }
}
