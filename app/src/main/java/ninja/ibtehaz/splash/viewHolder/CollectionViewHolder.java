package ninja.ibtehaz.splash.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.activity.CollectionProfile;
import ninja.ibtehaz.splash.models.CollectionModel;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibtehaz on 3/13/17.
 */

public class CollectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;
    private ImageView imgOriginal, imgLayer;
    private TextView txtCollectionTitle, txtUserName;
    private Util util;
    private CollectionModel currentModel;
    private LinearLayout llCollection;


    /**
     *
     * @param itemView
     * @param context
     */
    public CollectionViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        this.util = new Util();

        imgOriginal = (ImageView)itemView.findViewById(R.id.img_cover);
        imgLayer = (ImageView)itemView.findViewById(R.id.img_layer);
        txtCollectionTitle = (TextView)itemView.findViewById(R.id.txt_title);
        txtUserName = (TextView)itemView.findViewById(R.id.txt_author);
        llCollection = (LinearLayout)itemView.findViewById(R.id.ll_collection);

        llCollection.setOnClickListener(this);
    }


    /**
     *
     * @param model
     */
    public void onBindData(CollectionModel model) {
        this.currentModel = model;
        imgLayer.setBackgroundColor(Color.parseColor(model.getCoverColor()));
        util.loadImage(context, model.getUrlRegular(), imgOriginal, imgLayer);
        txtCollectionTitle.setText(util.capitalizeWords(model.getCollectionTitle()));
        txtUserName.setText(util.capitalizeWords(model.getUsername()));
    }

    /**
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_collection:
                openCollectionProfile();
                break;
        }
    }


    /**
     *
     */
    private void openCollectionProfile() {
        Intent i = new Intent(context, CollectionProfile.class);
        i.putExtra("collection_data", currentModel);
        context.startActivity(i);
    }
}
