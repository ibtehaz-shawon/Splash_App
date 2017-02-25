package ninja.ibtehaz.splash.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.activity.DetailActivity;
import ninja.ibtehaz.splash.models.FeedModel;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibteh on 2/20/2017.
 */

public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView imgLeft, imgLayerLeft;
    private FrameLayout frameLeft;
    private TextView txtAuthorLeft;

    private FeedModel currentDataModel;
    private int currentPosition;
    private View itemView;
    private Util util;
    private Context context;


    /**
     * constructor to construct the view
     * @param itemView
     */
    public FeedViewHolder(View itemView, Context context) {
        super(itemView);
        this.itemView = itemView;
        this.util = new Util();
        this.context = context;

        imgLeft = (ImageView)itemView.findViewById(R.id.img_left_original);

        imgLayerLeft = (ImageView)itemView.findViewById(R.id.img_layer_left);

        frameLeft = (FrameLayout)itemView.findViewById(R.id.frame_left);

        txtAuthorLeft = (TextView)itemView.findViewById(R.id.txt_author_left);

        frameLeft.setOnClickListener(this);
    }


    /**
     *
     * @param model
     * @param position
     */
    public void bindDataToView(FeedModel model, int position) {
        this.currentDataModel = model;
        this.currentPosition = position;

        Log.d("feedUrl", "position:_ "+position+" : >> "
                +model.getUrlRegular());

        if (model.isView()) {
            imgLeft.setVisibility(View.GONE);
            imgLayerLeft.setVisibility(View.GONE);
            itemView.findViewById(R.id.ll_daily_wallpaper).setVisibility(View.VISIBLE);
            txtAuthorLeft.setVisibility(View.GONE);
        } else {
            imgLayerLeft.setVisibility(View.VISIBLE);
            imgLayerLeft.setBackgroundColor(Color.parseColor(model.getColor()));
            util.loadImage(context, model.getUrlRegular(), imgLeft, imgLayerLeft);
            txtAuthorLeft.setText(model.getUserDisplayName());
            itemView.findViewById(R.id.ll_daily_wallpaper).setVisibility(View.GONE);
        }
    }

    /**
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frame_left:
                if (!currentDataModel.isView()) openDetails(currentDataModel);
                break;
        }
    }


    /**
     *
     */
    private void openDetails(FeedModel dataModel) {
        Intent i = new Intent(context, DetailActivity.class);
        i.putExtra("feed_details", dataModel);
        context.startActivity(i);
    }
}
