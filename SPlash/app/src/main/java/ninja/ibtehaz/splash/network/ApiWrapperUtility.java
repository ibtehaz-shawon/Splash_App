package ninja.ibtehaz.splash.network;

import android.util.Log;

import ninja.ibtehaz.splash.application.ApiConfig;

/**
 * Created by ibteh on 2/20/2017.
 */

public class ApiWrapperUtility {

    private final String TAG = ApiWrapperUtility.class.getSimpleName();

    /**
     *
     * @param getResponse
     * @param pageNumber
     * @param actionTag
     */
    public void apiCallToGetFeed(ApiWrapperToGet.GetResponse getResponse, int pageNumber,
                                 String actionTag) {
        String feedUrl = new ApiConfig().GET_FEED_URL(pageNumber);
        Log.d(TAG, "_log: feed url: "+feedUrl);
        ApiWrapperToGet apiWrapperToGet = new ApiWrapperToGet(actionTag, getResponse);
        apiWrapperToGet.callApi(feedUrl);
    }


    /**
     *
     * @param getResponse
     * @param pageNumber
     * @param method
     * @param actionTag
     */
    public void apiCallToGetCollection(ApiWrapperToGet.GetResponse getResponse, int pageNumber,
                                       int method, String actionTag) {
        String collectionUrl = new ApiConfig().GET_COLLECTION_URL(pageNumber, method);
        Log.d(TAG, "_log: collection url: "+collectionUrl);
        ApiWrapperToGet apiWrapperToGet = new ApiWrapperToGet(actionTag, getResponse);
        apiWrapperToGet.callApi(collectionUrl);
    }


    /**
     *
     * @param getResponse
     * @param pageNumber
     * @param collectionId
     * @param method
     * @param actionTag
     */
    public void apicallToGetCollectionPhoto(ApiWrapperToGet.GetResponse getResponse, int pageNumber,
                                            String collectionId, String method, String actionTag) {

        String collectionPhoto = new ApiConfig().GET_COLLECTION_PHOTO(collectionId, method, pageNumber);
        Log.d(TAG, "_log: collection photo url "+collectionPhoto);
        ApiWrapperToGet apiWrapperToGet = new ApiWrapperToGet(actionTag, getResponse);
        apiWrapperToGet.callApi(collectionPhoto);
    }
}
