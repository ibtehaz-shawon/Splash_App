package ninja.ibtehaz.splash.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.models.CollectionModel;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibtehaz on 3/14/17.
 */

public class CollectionProfile extends BaseActivity implements View.OnClickListener {

    private Context context;
    private CollectionModel collectionModel;
    private String collectionId;
    private Toolbar toolbar;

    private TextView txtTitle, txtName, txtDescription, txtTotalPhotos, txtUpdated, txtPublished,
            txtCollectionTitle;
    private ImageView imgCover, imgProfile, imgLayer;
    private Util util;
    private ImageView imgBack;

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

    }


    /**
     *
     */
    private void getExtra() {
        this.collectionModel = (CollectionModel)getIntent().getSerializableExtra("collection_data");
        this.collectionId = collectionModel.getCollectionId();
    }


    /**
     *
     */
    private void init() {
        this.context = this;
        this.util = new Util();

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
}
