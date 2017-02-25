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
}
