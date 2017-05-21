package ninja.ibtehaz.splash.network;

import com.android.volley.RequestQueue;

import ninja.ibtehaz.splash.application.Splash;

/**
 * Created by ibteh on 2/20/2017.
 */

public abstract class BaseApiWrapper {

    public RequestQueue requestQueue;

    public BaseApiWrapper() {
        requestQueue = Splash.getInstance().getRequestQueue();
    }

    public abstract void callApi(String url);
}
