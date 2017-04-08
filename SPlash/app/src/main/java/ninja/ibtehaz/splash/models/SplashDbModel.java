package ninja.ibtehaz.splash.models;

import java.io.Serializable;

/**
 * Created by ibteh on 4/9/2017.
 */

public class SplashDbModel implements Serializable {

    private int uniqueId;
    private String localFileName;
    private String urlRaw;
    private String urlSmall;
    private boolean isOffline;
    private boolean isSet;

    private String imageId;
    private boolean isDailyWallpaper;

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
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
