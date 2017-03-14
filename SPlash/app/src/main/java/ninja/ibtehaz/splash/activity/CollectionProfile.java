package ninja.ibtehaz.splash.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
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

        imgCover = (ImageView)findViewById(R.id.img_cover);
        imgLayer = (ImageView)findViewById(R.id.img_layer);
        imgProfile = (ImageView)findViewById(R.id.img_profile);
        imgBack = (ImageView)findViewById(R.id.img_back);

        imgBack.setOnClickListener(this);
    }


    /**
     *
     */
    private void inflateUi() {
        txtTitle.setText(util.capitalizeWords(collectionModel.getCollectionTitle()));

        txtCollectionTitle.setText(util.capitalizeWords(collectionModel.getCollectionTitle()));
        txtName.setText("By "+util.capitalizeWords(collectionModel.getUsername()));

        if (!collectionModel.getCollectionDescription().equalsIgnoreCase("none")) {
            txtDescription.setText(collectionModel.getCollectionDescription());
            txtTotalPhotos.setTextColor(ContextCompat.getColor(context, R.color.secondaryColor));
        } else {
            txtDescription.setVisibility(View.GONE);
            txtTotalPhotos.setTextColor(ContextCompat.getColor(context, R.color.primaryTextColor));
        }
        txtTotalPhotos.setText(collectionModel.getTotalPhotos() + " Photos");

        imgLayer.setBackgroundColor(Color.parseColor(collectionModel.getCoverColor()));
        util.loadImage(context, collectionModel.getUrlRegular(), imgCover, imgLayer);
        util.loadProfilePic(context, collectionModel.getProfileImageLarge(), imgProfile);
        txtPublished.setText(collectionModel.getPublishedAt());
        txtUpdated.setText(collectionModel.getUpdatedAt());

        util.makeToast(context, collectionId + " collection id");
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
}
