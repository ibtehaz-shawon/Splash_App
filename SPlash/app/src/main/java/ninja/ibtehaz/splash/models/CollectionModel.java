package ninja.ibtehaz.splash.models;

import java.io.Serializable;

/**
 * Created by ibtehaz on 3/12/17.
 */

public class CollectionModel implements Serializable {

    private String username, coverColor, publishedAt, updatedAt, collectionUrlSelf, collectionDescription;
    private boolean isFeatured, isCurated;
    private String urlThumb, collectionId, coverWidth, urlFull, urlCustom, totalPhotos, collectionUrlHtml, coverHeight;
    private String collectionTitle, profileImageLarge, urlRegular, urlRaw, profileImageSmall, urlSmall;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCoverColor() {
        return coverColor;
    }

    public void setCoverColor(String coverColor) {
        this.coverColor = coverColor;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCollectionUrlSelf() {
        return collectionUrlSelf;
    }

    public void setCollectionUrlSelf(String collectionUrlSelf) {
        this.collectionUrlSelf = collectionUrlSelf;
    }

    public String getCollectionDescription() {
        return collectionDescription;
    }

    public void setCollectionDescription(String collectionDescription) {
        this.collectionDescription = collectionDescription;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public boolean isCurated() {
        return isCurated;
    }

    public void setCurated(boolean curated) {
        isCurated = curated;
    }

    public String getUrlThumb() {
        return urlThumb;
    }

    public void setUrlThumb(String urlThumb) {
        this.urlThumb = urlThumb;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getCoverWidth() {
        return coverWidth;
    }

    public void setCoverWidth(String coverWidth) {
        this.coverWidth = coverWidth;
    }

    public String getUrlFull() {
        return urlFull;
    }

    public void setUrlFull(String urlFull) {
        this.urlFull = urlFull;
    }

    public String getUrlCustom() {
        return urlCustom;
    }

    public void setUrlCustom(String urlCustom) {
        this.urlCustom = urlCustom;
    }

    public String getTotalPhotos() {
        return totalPhotos;
    }

    public void setTotalPhotos(String totalPhotos) {
        this.totalPhotos = totalPhotos;
    }

    public String getCollectionUrlHtml() {
        return collectionUrlHtml;
    }

    public void setCollectionUrlHtml(String collectionUrlHtml) {
        this.collectionUrlHtml = collectionUrlHtml;
    }

    public String getCoverHeight() {
        return coverHeight;
    }

    public void setCoverHeight(String coverHeight) {
        this.coverHeight = coverHeight;
    }

    public String getCollectionTitle() {
        return collectionTitle;
    }

    public void setCollectionTitle(String collectionTitle) {
        this.collectionTitle = collectionTitle;
    }

    public String getProfileImageLarge() {
        return profileImageLarge;
    }

    public void setProfileImageLarge(String profileImageLarge) {
        this.profileImageLarge = profileImageLarge;
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

    public String getProfileImageSmall() {
        return profileImageSmall;
    }

    public void setProfileImageSmall(String profileImageSmall) {
        this.profileImageSmall = profileImageSmall;
    }

    public String getUrlSmall() {
        return urlSmall;
    }

    public void setUrlSmall(String urlSmall) {
        this.urlSmall = urlSmall;
    }
}
