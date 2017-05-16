package ninja.ibtehaz.splash.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ibteh on 4/9/2017.
 */

public class SplashDbModel implements Serializable {

    private long uniqueId;
    private String localFileName;
    private String urlRaw;
    private String urlSmall;
    private boolean isOffline;
    private boolean isSet;
    private boolean isDownloaded;

    private String imageId;
    private boolean isDailyWallpaper;

    //in dare cases
    private ArrayList<SplashDbModel> splashDbModels;

    /*-------------------------------------------------------------------*/
    /*-------------------------------------------------------------------*/
    /*-------------------------------------------------------------------*/
    /*-------------------------------------------------------------------*/
    /*if it is needed to transport SplashDbModels all at once, from one class to another*/
    public ArrayList<SplashDbModel> getSplashDbModels() {
        return splashDbModels;
    }

    public void setSplashDbModels(ArrayList<SplashDbModel> splashDbModels) {
        this.splashDbModels = splashDbModels;
    }

    /*-------------------------------------------------------------------*/
    /*-------------------------------------------------------------------*/
    /*-------------------------------------------------------------------*/
    /*-------------------------------------------------------------------*/

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public String getUrlRaw() {
        return urlRaw;
    }

    public void setUrlRaw(String urlRaw) {
        this.urlRaw = urlRaw;
    }

    public String getUrlSmall() {
        return urlSmall;
    }

    public void setUrlSmall(String urlSmall) {
        this.urlSmall = urlSmall;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public boolean isDailyWallpaper() {
        return isDailyWallpaper;
    }

    public void setDailyWallpaper(boolean dailyWallpaper) {
        isDailyWallpaper = dailyWallpaper;
    }
}
