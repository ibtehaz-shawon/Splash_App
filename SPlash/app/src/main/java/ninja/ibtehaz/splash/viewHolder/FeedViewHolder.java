package ninja.ibtehaz.splash.viewHolder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.DialogTitle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.activity.DetailActivity;
import ninja.ibtehaz.splash.models.FeedModel;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibtehaz on 2/20/2017.
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
    private DailyWallpaper dailyWallpaper;

    /**
     * constructor to construct the view
     * @param itemView
     */
    public FeedViewHolder(View itemView, Context context, DailyWallpaper dailyWallpaper) {
        super(itemView);
        this.itemView = itemView;
        this.util = new Util();
        this.context = context;
        this.dailyWallpaper = dailyWallpaper;

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
            loadPicture();
            txtAuthorLeft.setText(util.capitalizeWords(model.getUserDisplayName()));
            itemView.findViewById(R.id.ll_daily_wallpaper).setVisibility(View.GONE);
        }
    }


    /**
     * checks which url would be able to load image
     */
    private void loadPicture() {
        util.loadImage(context, currentDataModel.getUrlRegular(), imgLeft, imgLayerLeft);
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
                else setUpDailyWallpaper();
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


    /**
     * sets up daily wallpaper from a modal
     * show some text what will happen and etc
     */
    private void setUpDailyWallpaper() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.item_daily_wallpaper_confirm);

        final CheckBox chkIsOffline = (CheckBox)dialog.findViewById(R.id.cBox_offline_mode);
        Button btnConfirm = (Button)dialog.findViewById(R.id.btn_confirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isOffline = chkIsOffline.isSelected();
                dailyWallpaper.onDailyPaperSet(isOffline);
                util.makeToast(context, "Wallpapers are being set! Please check Settings for changes!");
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public interface DailyWallpaper {
        void onDailyPaperSet(boolean isOffline);
    }
}
