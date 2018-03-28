package ninja.ibtehaz.splash.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.adapter.CollectionAdapter;
import ninja.ibtehaz.splash.adapter.FeedAdapter;
import ninja.ibtehaz.splash.models.CollectionModel;
import ninja.ibtehaz.splash.network.ApiWrapperToGet;
import ninja.ibtehaz.splash.network.ApiWrapperUtility;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibtehaz on 3/12/17.
 */

public class CollectionDetails extends BaseActivity
        implements
        View.OnClickListener,
        ApiWrapperToGet.GetResponse {

    private final String TAG_CURATED = "curated", TAG_FEATURED = "featured",
            TAG = CollectionDetails.class.getSimpleName();
    private Context context;
    private boolean collectionState;
    private boolean isCurated;
    private ImageView imgBack;
    private TextView txtTitle;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private LinearLayout llErrorLayout;
    private int currentPage;
    private Util util;
    private boolean isEmpty;
    private int method;
    private ApiWrapperUtility apiWrapperUtility;
    private boolean isLoading;
    private Object paginationLock = new Object();
    private ArrayList<CollectionModel> dataModel;
    private CollectionAdapter collectionAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/
        init();
        /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/
        getIntentExtra();
        /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/
        callApi();
        /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    while (isLoading) {
                        synchronized (paginationLock) {
                            isLoading = false;
                            callApi();
                        }
                    }
                }
            }
        });

    }


    /**
     *
     */
    private void init() {
        this.context = this;
        imgBack = (ImageView)findViewById(R.id.img_back);
        txtTitle = (TextView)findViewById(R.id.txt_title);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        llErrorLayout = (LinearLayout)findViewById(R.id.ll_error_layout);
        currentPage = 1;
        util = new Util();
        isEmpty = false;
        dataModel = new ArrayList<>();
        apiWrapperUtility = new ApiWrapperUtility();

        imgBack.setOnClickListener(this);
    }


    /**
     *
     */
    private void getIntentExtra() {
        collectionState = getIntent().getBooleanExtra("collection_state", false);
        if (collectionState) isCurated = true;
        else isCurated = false;
        if (isCurated) {
            txtTitle.setText(context.getString(R.string.curated));
            method = 1;
        } else {
            method = 2;
            txtTitle.setText(context.getString(R.string.featured));
        }

    }


    /**
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    /**
     *
     */
    private void callApi() {
        if (currentPage == 1)
            showDialog("Loading Collections... Please wait!");

        if (util.isConnectionAvailable(context)) {
            String actionTag;
            if (method == 1) {
                actionTag = TAG_CURATED;
            } else {
                actionTag = TAG_FEATURED;
            }
            if (!isEmpty) {
                apiWrapperUtility.apiCallToGetCollection(this, currentPage, method, actionTag);
                currentPage++;
            }
        } else {
            //ERROR Layout
            cancelDialog();
            initErrorLayout("No Internet Connectivity", true);
        }
    }


    /**
     *
     * @param message
     * @param state
     */
    private void initErrorLayout(String message, boolean state) {
        if (recyclerView.getVisibility() != View.GONE) recyclerView.setVisibility(View.GONE);
        llErrorLayout.setVisibility(View.VISIBLE);

        if (state) {
            util.showSnackbar(context, findViewById(R.id.ll_feed_layout), message);
        }
    }


    /**
     * handles the get response and then parse it to separate function to parse
     * particular data file
     * @param actionTag
     * @param response
     */
    @Override
    public void onGetResponse(String actionTag, JSONObject response) {
        cancelDialog();
        if (response == null) {
            initErrorLayout("An Error Occurred!", true);
            return;
        } else {
            Log.d(TAG, "_log: "+actionTag + " response: "+response.toString());
        }

        if (actionTag.equals(TAG_CURATED)) {
            parseCurated(response);
        } else if (actionTag.equals(TAG_FEATURED)) {
            parseFeatured(response);
        }
    }


    /**
     * parses curated list of data
     */
    private void parseCurated(JSONObject response) {
        if (response.has("collections")) {
            try {
                JSONArray collections = response.getJSONArray("collections");

                if (collections.length() == 0) {
                    isEmpty = true;
                }

                for (int i = 0; i < collections.length(); i++) {
                    JSONObject row = collections.getJSONObject(i);
                    CollectionModel collectionModel = new CollectionModel();

                    collectionModel.setUrlSmall(row.getString("url_small"));
                    collectionModel.setProfileImageSmall(row.getString("profile_image_small"));
                    collectionModel.setUrlRaw(row.getString("url_raw"));
                    collectionModel.setUpdatedAt(row.getString("updated"));
                    collectionModel.setUrlRegular(row.getString("url_regular"));
                    collectionModel.setProfileImageLarge(row.getString("profile_image_large"));
                    collectionModel.setCollectionTitle(row.getString("collection_title"));
                    collectionModel.setCoverHeight(row.getString("cover_height"));
                    collectionModel.setCollectionUrlHtml(row.getString("collection_url_html"));
                    collectionModel.setTotalPhotos(row.getString("total_photos"));
                    collectionModel.setUrlCustom(row.getString("url_custom"));
                    collectionModel.setUrlFull(row.getString("url_full"));
                    collectionModel.setCoverWidth(row.getString("cover_width"));
                    collectionModel.setCollectionId(row.getString("collection_id"));
                    collectionModel.setCurated(row.getBoolean("is_curated"));
                    collectionModel.setUrlThumb(row.getString("url_thumb"));
                    collectionModel.setFeatured(row.getBoolean("is_featured"));
                    collectionModel.setCollectionDescription(row.getString("collection_description"));
                    collectionModel.setCollectionUrlSelf(row.getString("collection_url_self"));
                    collectionModel.setPublishedAt(row.getString("published"));
                    collectionModel.setCoverColor(row.getString("cover_color"));
                    collectionModel.setUsername(row.getString("user_name"));

                    dataModel.add(collectionModel);
                }
                isLoading = true;
                listBind();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * parses featured lists of data
     */
    private void parseFeatured(JSONObject response) {
        if (response.has("collections")) {
            try {
                JSONArray collections = response.getJSONArray("collections");

                if (collections.length() == 0) {
                    isEmpty = true;
                }

                for (int i = 0; i < collections.length(); i++) {
                    JSONObject row = collections.getJSONObject(i);
                    CollectionModel collectionModel = new CollectionModel();

                    collectionModel.setUrlSmall(row.getString("url_small"));
                    collectionModel.setProfileImageSmall(row.getString("profile_image_small"));
                    collectionModel.setUrlRaw(row.getString("url_raw"));
                    collectionModel.setUpdatedAt(row.getString("updated"));
                    collectionModel.setUrlRegular(row.getString("url_regular"));
                    collectionModel.setProfileImageLarge(row.getString("profile_image_large"));
                    collectionModel.setCollectionTitle(row.getString("collection_title"));
                    collectionModel.setCoverHeight(row.getString("cover_height"));
                    collectionModel.setCollectionUrlHtml(row.getString("collection_url_html"));
                    collectionModel.setTotalPhotos(row.getString("total_photos"));
                    collectionModel.setUrlCustom(row.getString("url_custom"));
                    collectionModel.setUrlFull(row.getString("url_full"));
                    collectionModel.setCoverWidth(row.getString("cover_width"));
                    collectionModel.setCollectionId(row.getString("collection_id"));
                    collectionModel.setCurated(row.getBoolean("is_curated"));
                    collectionModel.setUrlThumb(row.getString("url_thumb"));
                    collectionModel.setFeatured(row.getBoolean("is_featured"));
                    collectionModel.setCollectionDescription(row.getString("collection_description"));
                    collectionModel.setCollectionUrlSelf(row.getString("collection_url_self"));
                    collectionModel.setPublishedAt(row.getString("published"));
                    collectionModel.setCoverColor(row.getString("cover_color"));
                    collectionModel.setUsername(row.getString("user_name"));

                    dataModel.add(collectionModel);
                }
                isLoading = true;
                listBind();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     *
     */
    private void listBind() {
        if (collectionAdapter == null) {
            collectionAdapter = new CollectionAdapter(dataModel, context);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, 1);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(false);
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(1500);
            itemAnimator.setChangeDuration(1000);
            recyclerView.setItemAnimator(itemAnimator);
            recyclerView.setAdapter(collectionAdapter);
        } else {
            collectionAdapter.notifyDataSetChanged();
        }
    }
}
