package ninja.ibtehaz.splash.network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ibteh on 2/20/2017.
 */

public class ApiWrapperToPost extends BaseApiWrapper {

    private String TAG = ApiWrapperToPost.class.getSimpleName();
    private String mActionTag;
    private Map<String, String> mPostParams;
    private PostResponse mPostResponse;


    /**
     * @param actionTag    | tag to signify caller
     * @param postParams   | parameter to post
     * @param postResponse | response interface implementation interface
     */
    public ApiWrapperToPost(String actionTag, Map<String, String> postParams,
                            PostResponse postResponse) {
        mActionTag = actionTag;
        mPostParams = new HashMap<>();
        mPostParams = postParams;
        mPostResponse = postResponse;

        for (Map.Entry<String, String> entry : mPostParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Log.e(TAG, "_log : APIWrapperToPost : key : "
                    + key + " : value : " + value);
        }
    }


    /**
     * @param url
     */
    @Override
    public void callApi(String url) {
        StringRequest jsonObjRequest =
                new StringRequest(Request.Method.POST, url,
                mOkResponseListener, mErrorListener) {


            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded;";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return mPostParams;
            }
        };

        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjRequest);

    }

    /**
     * TODO: need to handle different error type instead of sending NULL as generic
     */
    Response.ErrorListener mErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if(error.networkResponse != null
                    && error.networkResponse.data != null){
                VolleyError error1 = new VolleyError(new String(error.networkResponse.data));
                Log.e("PD_DEBUG", String.valueOf(error.networkResponse.data.toString()));
            }
            try {
                Log.e(TAG, "_log : error : " + error.getMessage() + " " + error.networkResponse.statusCode);
            } catch (Exception err) {
                Log.e(TAG, "_log : error : response is NULL");
            } finally {
                mPostResponse.onPostResponse(mActionTag, null);
            }
        }
    };

    /**
     *
     */
    Response.Listener<String> mOkResponseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.i(TAG, "_log : success: " + response.toString());
            try {
                mPostResponse.onPostResponse(mActionTag, new JSONObject(response));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * response interface
     */
    public interface PostResponse {
        void onPostResponse(String actionTag, JSONObject response);
    }
}
