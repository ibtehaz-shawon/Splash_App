package ninja.ibtehaz.splash.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.adapter.FeedAdapter;
import ninja.ibtehaz.splash.models.FeedModel;
import ninja.ibtehaz.splash.network.ApiWrapperToGet;
import ninja.ibtehaz.splash.network.ApiWrapperUtility;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibtehaz on 3/21/17.
 */

public class CollectionProfileFragments extends android.support.v4.app.Fragment implements
        ApiWrapperToGet.GetResponse {


    private final String TAG = CollectionProfileFragments.class.getSimpleName();
    private View view;
    private Context context;
    private RecyclerView recyclerView;
    private String collectionId, method;
    private boolean isCurated, isEmpty;
    private Util util;
    private ArrayList<FeedModel> dataModel;
    private FeedAdapter feedAdapter;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_colleciton_profile, container, false);

        init();
        getBundleData();
        callApi(1);

        return view;
    }


    /**
     *
     */
    private void  init() {
        context = getContext();
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        util = new Util();
        dataModel = new ArrayList<>();
    }


    /**
     *
     */
    private void getBundleData() {
        Bundle bundle = getArguments();
        this.collectionId = bundle.getString("collectionId");
        this.method = bundle.getString("method");
        this.isCurated = bundle.getBoolean("isCurated");
    }


    /**
     * calls the api to get the collection data
     * @param pageNumber
     */
    private void callApi(int pageNumber) {
        if (!isEmpty) {
            new ApiWrapperUtility().apicallToGetCollectionPhoto(this, pageNumber, collectionId, method, "");
        }
    }



    /**
     * parses JSON responses of Collection data
     * @param actionTag
     * @param response
     */
    @Override
    public void onGetResponse(String actionTag, JSONObject response) {
        if (response == null) {
            util.makeToast(context, "An Error Occurred! Please try again!");
            Log.d(TAG, "_log: null returned");
            return;
        } else {
            Log.d(TAG, "_log: "+response.toString());
        }

        if (response.has("collection_data")) {
            try {
                JSONArray collectionData = response.getJSONArray("collection_data");
                int totalCollectionPhoto = response.getInt("total_collection");
                int currentPayload = response.getInt("total");
                int page = response.getInt("page");

                if (totalCollectionPhoto == 0 && currentPayload == 0) {
                    JSONObject row = collectionData.getJSONObject(0);
                    if (row.has("error")) {
                        util.makeToast(context, "Data not uploaded yet into the server from Unsplash!");
                        util.makeToast(context, "Error: " + row.getString("message"));
                        return;
                    } else {
                        parseCollectionData(collectionData, page);
                    }
                } else {
                    parseCollectionData(collectionData, page);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     *
     * @param collectionData
     */
    private void parseCollectionData(JSONArray collectionData, int page) {
        if (collectionData.length() == 0 && page > 1) {
            isEmpty = true;
            return;
        }

        try {
            for (int i = 0; i < collectionData.length(); i++) {
                JSONObject row = collectionData.getJSONObject(i);
                FeedModel feedModel = new FeedModel();

                feedModel.setUserProfilePic(row.getString("user_profile_pic"));
                feedModel.setUrlRaw(row.getString("url_raw"));
                feedModel.setUrlDownload(row.getString("url_download"));
                feedModel.setCreatedAt("created_at");
                feedModel.setPhotoHeight(row.getString("height"));
                feedModel.setPhotoId(row.getString("photo_id"));
                feedModel.setUrlThumb(row.getString("url_thumb"));
                feedModel.setUserDisplayName(row.getString("user_display_name"));
                feedModel.setUrlShare(row.getString("url_share"));
                feedModel.setColor(row.getString("color"));
                feedModel.setUrlFull(row.getString("url_full"));
                feedModel.setPhotoWidth(row.getString("width"));
                feedModel.setUrlRegular(row.getString("url_regular"));
                feedModel.setUrlSmall(row.getString("url_small"));
                feedModel.setUrlCustom(row.getString("url_custom"));

                dataModel.add(feedModel);
            }
            listBind();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    /**
     *
     */
    private void listBind() {
        if (feedAdapter == null) {
            feedAdapter = new FeedAdapter(context, dataModel);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2,
                    LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(1500);
            itemAnimator.setChangeDuration(1000);
            recyclerView.setItemAnimator(itemAnimator);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(feedAdapter);
        } else {
            feedAdapter.notifyDataSetChanged();
        }
    }
}
