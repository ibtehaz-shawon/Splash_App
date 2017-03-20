package ninja.ibtehaz.splash.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
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
import ninja.ibtehaz.splash.adapter.FeedAdapter;
import ninja.ibtehaz.splash.models.CollectionModel;
import ninja.ibtehaz.splash.models.FeedModel;
import ninja.ibtehaz.splash.network.ApiWrapperToGet;
import ninja.ibtehaz.splash.network.ApiWrapperUtility;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibtehaz on 3/14/17.
 */

public class CollectionProfile extends BaseActivity
        implements View.OnClickListener,
        ApiWrapperToGet.GetResponse {

    private final String TAG = "collection_photo";
    private Context context;
    private CollectionModel collectionModel;
    private String collectionId;
    private Toolbar toolbar;
    private boolean isCurated, isEmpty;
    private ArrayList<FeedModel> dataModel;
    private FeedAdapter feedAdapter;

    private TextView txtTitle, txtName, txtDescription, txtTotalPhotos, txtUpdated, txtPublished,
            txtCollectionTitle;
    private ImageView imgCover, imgProfile, imgLayer;
    private Util util;
    private ImageView imgBack;
    private RecyclerView recyclerView;

    private LinearLayout llTotalPhotos, llAboutCollection, llLastUpdate, llAboutCollectionTab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*------------------------------------------------------*/
        init();
        /*------------------------------------------------------*/
        getExtra();
        /*------------------------------------------------------*/
        inflateUi();
        /*------------------------------------------------------*/
        callApi(1);
    }


    /**
     *
     */
    private void getExtra() {
        this.collectionModel = (CollectionModel)getIntent().getSerializableExtra("collection_data");
        this.collectionId = collectionModel.getCollectionId();
        if (collectionModel.isCurated()) this.isCurated = true;
        else this.isCurated = false;
    }


    /**
     *
     */
    private void init() {
        this.context = this;
        this.util = new Util();
        this.isEmpty = false;
        dataModel = new ArrayList<>();

        txtTitle = (TextView)findViewById(R.id.txt_title);
        txtCollectionTitle = (TextView)findViewById(R.id.txt_collection_title);
        txtName = (TextView)findViewById(R.id.txt_user_name);
        txtDescription = (TextView)findViewById(R.id.txt_description);

        txtTotalPhotos = (TextView)findViewById(R.id.txt_total_photos);
        txtUpdated = (TextView)findViewById(R.id.txt_last_updated);
        txtPublished = (TextView)findViewById(R.id.txt_published);

        llTotalPhotos = (LinearLayout)findViewById(R.id.ll_total_photos);
        llAboutCollection = (LinearLayout)findViewById(R.id.ll_about_collection);
        llAboutCollectionTab = (LinearLayout)findViewById(R.id.ll_about_collection_tab);
        llLastUpdate = (LinearLayout)findViewById(R.id.ll_last_update);

        imgCover = (ImageView)findViewById(R.id.img_cover);
        imgLayer = (ImageView)findViewById(R.id.img_layer);
        imgProfile = (ImageView)findViewById(R.id.img_profile);
        imgBack = (ImageView)findViewById(R.id.img_back);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        imgBack.setOnClickListener(this);
        llAboutCollection.setOnClickListener(this);
    }


    /**
     *
     */
    private void inflateUi() {
        txtTitle.setText(util.capitalizeWords(collectionModel.getCollectionTitle()));

        txtCollectionTitle.setText(util.capitalizeWords(collectionModel.getCollectionTitle()));
        txtName.setText("By "+util.capitalizeWords(collectionModel.getUsername()));

        if (!collectionModel.getCollectionDescription().equalsIgnoreCase("none")) {
            Spannable spannable = new SpannableString(collectionModel.getCollectionDescription().trim());
            spannable.setSpan(new RelativeSizeSpan(1.5f), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtDescription.setText(spannable);
        } else {
            llAboutCollection.setVisibility(View.GONE);
            findViewById(R.id.view_about_collection).setVisibility(View.GONE);
        }
        txtTotalPhotos.setText(collectionModel.getTotalPhotos());

        imgLayer.setBackgroundColor(Color.parseColor(collectionModel.getCoverColor()));
        util.loadImage(context, collectionModel.getUrlRegular(), imgCover, imgLayer);
        util.loadProfilePic(context, collectionModel.getProfileImageLarge(), imgProfile);
        txtPublished.setText(collectionModel.getPublishedAt());
        txtUpdated.setText(collectionModel.getUpdatedAt());
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
            case R.id.ll_about_collection:
                if (llAboutCollectionTab.getVisibility() != View.VISIBLE) llAboutCollectionTab.setVisibility(View.VISIBLE);
                else llAboutCollectionTab.setVisibility(View.GONE);
                break;
        }
    }


    /**
     * calls the api to get the collection data
     * @param pageNumber
     */
    private void callApi(int pageNumber) {
        if (pageNumber == 1) showDialog("Loading collection.....");

        if (!isEmpty) {
            String method;
            if (isCurated) method = "1";
            else method = "2";
            new ApiWrapperUtility().apicallToGetCollectionPhoto(this, pageNumber, collectionId, method, "");
        }
    }

    /**
     * parses JSOB responses of Collection data
     * @param actionTag
     * @param response
     */
    @Override
    public void onGetResponse(String actionTag, JSONObject response) {
        cancelDialog();

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
