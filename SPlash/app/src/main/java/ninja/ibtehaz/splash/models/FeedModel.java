package ninja.ibtehaz.splash.models;

import java.io.Serializable;

/**
 * Created by ibteh on 2/20/2017.
 */

public class FeedModel implements Serializable {

    private String urlShare, urlFull, urlRegular, urlRaw, urlDownload;
    private String cameraIso, cameraAparture, cameraMake, cameraExposure, cameraFocal, cameraModel;
    private String color, photoCategory;
    private String locationLat, locationLong, locationName;
    private String userDisplayName, userProfilePic;
    private String photoId;
    private String createdAt;
    private boolean isView;

    public boolean isView() {
        return isView;
    }

    public void setView(boolean view) {
        isView = view;
    }

    public String getUrlShare() {
        return urlShare;
    }

    public void setUrlShare(String urlShare) {
        this.urlShare = urlShare;
    }

    public String getUrlFull() {
        return urlFull;
    }

    public void setUrlFull(String urlFull) {
        this.urlFull = urlFull;
    }

    public String getUrlRegular() {
        return urlRegular;
    }

    public void setUrlRegular(String urlRegular) {
        this.urlRegular = urlRegular;
    }

    public String getUrlRaw() {
        return urlRaw;
    }

    public void setUrlRaw(String urlRaw) {
        this.urlRaw = urlRaw;
    }

    public String getUrlDownload() {
        return urlDownload;
    }

    public void setUrlDownload(String urlDownload) {
        this.urlDownload = urlDownload;
    }

    public String getCameraIso() {
        return cameraIso;
    }

    public void setCameraIso(String cameraIso) {
        this.cameraIso = cameraIso;
    }

    public String getCameraAparture() {
        return cameraAparture;
    }

    public void setCameraAparture(String cameraAparture) {
        this.cameraAparture = cameraAparture;
    }

    public String getCameraMake() {
        return cameraMake;
    }

    public void setCameraMake(String cameraMake) {
        this.cameraMake = cameraMake;
    }

    public String getCameraExposure() {
        return cameraExposure;
    }

    public void setCameraExposure(String cameraExposure) {
        this.cameraExposure = cameraExposure;
    }

    public String getCameraFocal() {
        return cameraFocal;
    }

    public void setCameraFocal(String cameraFocal) {
        this.cameraFocal = cameraFocal;
    }

    public String getCameraModel() {
        return cameraModel;
    }

    public void setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPhotoCategory() {
        return photoCategory;
    }

    public void setPhotoCategory(String photoCategory) {
        this.photoCategory = photoCategory;
    }

    public String getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(String locationLat) {
        this.locationLat = locationLat;
    }

    public String getLocationLong() {
        return locationLong;
    }

    public void setLocationLong(String locationLong) {
        this.locationLong = locationLong;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
