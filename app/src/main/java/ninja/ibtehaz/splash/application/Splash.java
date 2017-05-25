package ninja.ibtehaz.splash.application;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.orm.SugarContext;

import ninja.ibtehaz.splash.db_helper.SplashDb;
import ninja.ibtehaz.splash.models.FeedModel;

/**
 * Created by ibteh on 2/20/2017.
 */

public class Splash extends Application {

    public static final String TAG = Splash.class.getSimpleName();
    private static Splash application;
    private RequestQueue mRequestQueue;


    /**
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
        /*-------------init the db with a dummy data-------------------*/
//        new SplashDb().removeAll();
//        initDB();
        /*-------------------------------------------------------------*/
        application = this;
        mRequestQueue=null;
    }


    /**
     * @deprecated
     * this function was used just for testing purpose
     */
    private void initDB() {
        FeedModel feedModel = new FeedModel();
        feedModel.setPhotoId("imiXX93U8Po");
        feedModel.setUrlRaw("https://images.unsplash.com/photo-1488977321720-6672ab3ed60f");
        feedModel.setUrlSmall("https://images.unsplash.com/photo-1488977321720-6672ab3ed60f?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=f0b2a4bd6f9abcc69c9c6fc65ba4cd0f");

        new SplashDb().insertSingleImageData(feedModel);
    }

    public static synchronized Splash getInstance()
    {
        return application;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

    /**
     * Adds the specified request to the global queue, if tag is specified then
     * it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important to
     * specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        getRequestQueue().cancelAll(tag);
    }
}
