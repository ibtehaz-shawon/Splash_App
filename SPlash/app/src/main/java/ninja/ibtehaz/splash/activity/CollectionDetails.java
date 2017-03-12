package ninja.ibtehaz.splash.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import ninja.ibtehaz.splash.R;
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        getIntentExtra();
        callApi();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

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
        apiWrapperUtility = new ApiWrapperUtility();
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

    @Override
    public void onClick(View v) {

    }

    /**
     *
     */
    private void callApi() {
        if (currentPage == 1)
            showDialog("Loading Data... Please wait!");

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
     *
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
            parseCurated();
        } else if (actionTag.equals(TAG_FEATURED)) {
            parseFeatured();
        }
    }


    /**
     * parses curated list of data
     */
    private void parseCurated() {

    }


    /**
     * parses featured lists of data
     */
    private void parseFeatured() {

    }
}
