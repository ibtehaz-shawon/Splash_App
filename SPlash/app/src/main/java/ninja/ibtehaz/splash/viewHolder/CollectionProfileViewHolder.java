package ninja.ibtehaz.splash.viewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
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
 * Created by ibtehaz on 5/18/17.
 */

public class CollectionProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;
    private LinearLayout llHeader;
    private TextView txtName, txtDescription, txtTotalPhotos, txtUpdated, txtPublished,
            txtCollectionTitle;
    private ImageView imgCover, imgProfile, imgLayer;
    private LinearLayout llTotalPhotos, llAboutCollection, llLastUpdate, llAboutCollectionTab;
    private Util util;
    private View itemView;


    /**
     *
     * @param itemView
     */
    public CollectionProfileViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        this.util = new Util();
        this.itemView = itemView;

        txtCollectionTitle = (TextView)itemView.findViewById(R.id.txt_collection_title);
        txtName = (TextView)itemView.findViewById(R.id.txt_user_name);
        txtDescription = (TextView)itemView.findViewById(R.id.txt_description);

        txtTotalPhotos = (TextView)itemView.findViewById(R.id.txt_total_photos);
        txtUpdated = (TextView)itemView.findViewById(R.id.txt_last_updated);
        txtPublished = (TextView)itemView.findViewById(R.id.txt_published);

        llTotalPhotos = (LinearLayout)itemView.findViewById(R.id.ll_total_photos);
        llAboutCollection = (LinearLayout)itemView.findViewById(R.id.ll_about_collection);
        llAboutCollectionTab = (LinearLayout)itemView.findViewById(R.id.ll_about_collection_tab);
        llLastUpdate = (LinearLayout)itemView.findViewById(R.id.ll_last_update);

        imgCover = (ImageView)itemView.findViewById(R.id.img_cover);
        imgLayer = (ImageView)itemView.findViewById(R.id.img_layer);
        imgProfile = (ImageView)itemView.findViewById(R.id.img_profile);

        llHeader = (LinearLayout)itemView.findViewById(R.id.ll_header);

        llAboutCollection.setOnClickListener(this);
    }


    /**
     * binds collection model's profile data to the zeroth view
     */
    public void onBindData(CollectionModel collectionModel) {
        llHeader.setBackgroundResource(R.drawable.gradient_feed_bg);

        txtCollectionTitle.setText(util.capitalizeWords(collectionModel.getCollectionTitle()));
        txtName.setText("By "+util.capitalizeWords(collectionModel.getUsername()));

        if (!collectionModel.getCollectionDescription().equalsIgnoreCase("none")) {
            Spannable spannable = new SpannableString(collectionModel.getCollectionDescription().trim());
            spannable.setSpan(new RelativeSizeSpan(1.5f), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtDescription.setText(spannable);
        } else {
            llAboutCollection.setVisibility(View.GONE);
            itemView.findViewById(R.id.view_about_collection).setVisibility(View.GONE);
        }

        txtTotalPhotos.setText(collectionModel.getTotalPhotos());

        imgLayer.setBackgroundColor(Color.parseColor(collectionModel.getCoverColor()));
        util.loadImage(context, collectionModel.getUrlRegular(), imgCover, imgLayer);
        util.loadProfilePic(context, collectionModel.getProfileImageLarge(), imgProfile);
        txtPublished.setText(new Util().dateParser(collectionModel.getPublishedAt()));

        txtUpdated.setText(collectionModel.getUpdatedAt());
    }

    /**
     * on click listener handler for item's
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_about_collection:
                if (llAboutCollectionTab.getVisibility() != View.VISIBLE) llAboutCollectionTab.setVisibility(View.VISIBLE);
                else llAboutCollectionTab.setVisibility(View.GONE);
                break;
        }
    }
}
