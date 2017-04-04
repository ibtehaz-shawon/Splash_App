package ninja.ibtehaz.splash.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * Created by ibteh on 2/20/2017.
 */

public class FeedActivity extends BaseActivity
        implements
        ApiWrapperToGet.GetResponse,
        View.OnClickListener {

    private final String TAG = FeedActivity.class.getSimpleName();
    private Context context;
    private ImageView imgBack;
    private TextView txtTitle;
    private RecyclerView recyclerView;
    private LinearLayout llErrorLayout;
    private Util util;
    private ApiWrapperUtility apiWrapperUtility;
    private ArrayList<FeedModel> dataModel;
    private FeedAdapter feedAdapter;
    private int pageNumber;
    private boolean isLoading, isEmpty;
    private final Object paginationLock = new Object();
    private View header;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar_landing);
        setSupportActionBar(toolbar);

        /*-----------------------------------------*/
        init();
        /*-----------------------------------------*/
        callApi();
        /*-----------------------------------------*/
        initNavigationDrawer();
        /*-----------------------------------------*/
        /*-----------------------------------------*/
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
        /*-----------------------------------------*/
        /*-----------------------------------------*/
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNumber = 1;
                callApi();
            }
        });
    }


    /**
     * initializes the view
     */
    private void init() {
        context = this;
        util = new Util();
        pageNumber = 1;
        isLoading = true;
        isLoading = false;
        dataModel = new ArrayList<>();
        apiWrapperUtility = new ApiWrapperUtility();
        imgBack = (ImageView)findViewById(R.id.img_back);
        txtTitle = (TextView)findViewById(R.id.txt_title);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        llErrorLayout = (LinearLayout)findViewById(R.id.ll_error_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);

        findViewById(R.id.img_back).setVisibility(View.GONE);
    }


    /**
     * initiates the navigation drawer
     */
    private void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        /*--------------------------------------------------------------*/
        /*---------------------Header TextView and ImageView------------*/
        /*--------------------------------------------------------------*/
        header = navigationView.getHeaderView(0);

        /*--------------------------------------------------------------*/
        /*--------------------------------------------------------------*/
        /*--------------On Click Listener NavBar------------------------*/
        /*--------------------------------------------------------------*/
        header.findViewById(R.id.btn_curated).setOnClickListener(this);
        header.findViewById(R.id.btn_featured).setOnClickListener(this);
        header.findViewById(R.id.btn_settings).setOnClickListener(this);
        header.findViewById(R.id.btn_test).setOnClickListener(this);
        /*--------------------------------------------------------------*/
        /*--------------------------------------------------------------*/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }


    /**
     *
     */
    private void callApi() {
        if (pageNumber == 1)
            showDialog("Loading Data... Please wait!");

        if (util.isConnectionAvailable(context)) {
            if (!isEmpty) {
                apiWrapperUtility.apiCallToGetFeed(this, pageNumber, "");
                pageNumber++;
            }
        } else {
            //ERROR Layout
            cancelDialog();
            initErrorLayout("No Internet Connectivity", true);
            if (swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(false);
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
        if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (response == null) {
            initErrorLayout("", false);
            return;
        } else {
            Log.d(TAG, "_log: "+response.toString());
        }

        if (response.has("photo")) {
            try {
                int total = Integer.parseInt(response.getString("total"));
                if (total == 0) {
                    findViewById(R.id.ll_empty_view).setVisibility(View.VISIBLE);
                    isEmpty = true;
                } else {
                    JSONArray photo = response.getJSONArray("photo");

                    if (pageNumber == 2){
                        FeedModel model = new FeedModel();
                        model.setView(true);
                        dataModel.add(model);
                    }

                    for (int i = 0; i < photo.length(); i++) {
                        JSONObject row = photo.getJSONObject(i);
                        FeedModel model = new FeedModel();

                        model.setUrlShare(row.getString("url_share"));
                        model.setUrlDownload(row.getString("url_download"));
                        model.setUrlFull(row.getString("url_full"));
                        model.setUrlRaw(row.getString("url_raw"));
                        model.setUrlRegular(row.getString("url_regular"));
                        model.setUrlCustom(row.getString("url_custom"));
                        model.setUrlThumb(row.getString("url_thumb"));
                        model.setUrlSmall(row.getString("url_small"));

                        model.setCameraMake(row.getString("user_camera_make"));
                        model.setCameraModel(row.getString("user_camera_model"));
                        model.setCameraExposure(row.getString("user_camera_exposure"));
                        model.setCameraAparture(row.getString("user_camera_apature"));
                        model.setCameraFocal(row.getString("user_camera_focal"));
                        model.setCameraIso(row.getString("user_camera_iso"));

                        model.setColor(row.getString("color"));
                        model.setPhotoCategory(row.getString("photo_category"));

                        model.setLocationName(row.getString("photo_location_name"));
                        model.setLocationLat(row.getString("photo_location_lat"));
                        model.setLocationLong(row.getString("photo_location_long"));

                        model.setUserDisplayName(row.getString("user_display_name"));
                        model.setUserProfilePic(row.getString("user_profile_pic"));

                        model.setPhotoId(row.getString("photo_id"));
                        model.setCreatedAt(row.getString("created_at"));
                        model.setView(false);

                        dataModel.add(model);
                    }
                    listBind();
                    isLoading = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            initErrorLayout("", false);
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

        if (llErrorLayout.getVisibility() == View.VISIBLE) {
            llErrorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
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
     * on click listeners
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent i = new Intent(context, CollectionDetails.class);
        switch (v.getId()) {
            case R.id.btn_curated:
                i.putExtra("collection_state", true);
                startActivity(i);
                break;
            case R.id.btn_featured:
                i.putExtra("collection_state", false);
                startActivity(i);
                break;
            case R.id.btn_settings:
                Intent settings = new Intent(context, SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.btn_test:
                Intent testLocalPhoto = new Intent(context, TestLocalPhoto.class);
                startActivity(testLocalPhoto);
                break;
        }
    }

}
