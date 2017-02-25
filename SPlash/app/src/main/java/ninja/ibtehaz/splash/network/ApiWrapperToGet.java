package ninja.ibtehaz.splash.network;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by ibteh on 2/20/2017.
 */

public class ApiWrapperToGet extends BaseApiWrapper {

    private String TAG = ApiWrapperToGet.class.getSimpleName();
    private String mActionTag;
    private GetResponse mGetResponse;
    /**
     *
     * @param actionTag | signifies the current API caller
     * @param getResponse | response interface instance
     */
    public ApiWrapperToGet(String actionTag, GetResponse getResponse) {
        mActionTag = actionTag;
        mGetResponse = getResponse;
    }



    Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                Log.e(TAG, "_log : error : "
                        + error.toString() + " "
                        + error.networkResponse.statusCode);
            } catch (Exception err) {
                Log.e(TAG, "_log : error : null returned");
            } finally {
                mGetResponse.onGetResponse(mActionTag, null);
            }
        }
    };

    Response.Listener<JSONObject> mOkResponseListener =
            new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.e(TAG, "_log : success: " + response.toString());
            mGetResponse.onGetResponse(mActionTag, response);
        }
    };

    @Override
    public void callApi(String url) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url,
                        mOkResponseListener, mErrorListener);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
    }


    /**
     * response interface
     */
    public interface GetResponse {
        void onGetResponse(String actionTag, JSONObject response);
    }
}
