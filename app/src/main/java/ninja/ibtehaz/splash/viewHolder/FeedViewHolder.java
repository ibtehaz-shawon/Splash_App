package ninja.ibtehaz.splash.viewHolder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.widget.DialogTitle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.activity.DetailActivity;
import ninja.ibtehaz.splash.db_helper.SplashDb;
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
     * opens up the feed details in the main feed
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

        final RadioGroup rGroupChangeTime = (RadioGroup)dialog.findViewById(R.id.rGroup_wallpaper_change);
        final RadioGroup rGroupWallpaperNumber = (RadioGroup)dialog.findViewById(R.id.rGroup_wallpaper_number);
        final CheckBox chkIsOffline = (CheckBox)dialog.findViewById(R.id.cBox_offline_mode);
        Button btnConfirm = (Button)dialog.findViewById(R.id.btn_confirm);
        final SeekBar seekBarDownload = (SeekBar)dialog.findViewById(R.id.seekbar_download_size);

        final boolean isFirstTime = new SplashDb().isFirstTime();
        if (isFirstTime) {
            rGroupChangeTime.setVisibility(View.VISIBLE);
            rGroupWallpaperNumber.setVisibility(View.VISIBLE);
        } else {
            rGroupChangeTime.setVisibility(View.GONE);
            rGroupWallpaperNumber.setVisibility(View.GONE);
        }

        /**
         * on click listener to handle and detect time listener (When to change wallpaper)
         */
        rGroupChangeTime.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.rButton_six_am:
                        break;
                    case R.id.rButton_twelve_am:
                        //12 am
                        break;
                }
            }
        });


        /**
         * on click listener to check and hold number of wallpapers to be downloaded at a time.
         */
        rGroupWallpaperNumber.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.rBtn_five:
                        break;

                    case R.id.rBtn_ten:
                        break;

                    case R.id.rBtn_fifteen:
                        break;
                }
            }
        });


        /**
         * checked on this on will enable some other UI components as well.
         */
        chkIsOffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dialog.findViewById(R.id.txt_offline_alert).setVisibility(View.VISIBLE);
                    dialog.findViewById(R.id.txt_download_size).setVisibility(View.VISIBLE);
                    dialog.findViewById(R.id.seekbar_download_size).setVisibility(View.VISIBLE);
                } else {
                    dialog.findViewById(R.id.txt_offline_alert).setVisibility(View.GONE);
                    dialog.findViewById(R.id.txt_download_size).setVisibility(View.GONE);
                    dialog.findViewById(R.id.seekbar_download_size).setVisibility(View.GONE);
                }
            }
        });


        /**
         * seekbar changed listener
         */
        seekBarDownload.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 50) {
                    seekBarDownload.setProgress(50);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isOffline = chkIsOffline.isChecked();
                int changeTime = 0, wallpaperAmount = 0, quality = 70;
                //quality is by default 70%

                if (isFirstTime) {
                    switch (rGroupChangeTime.getCheckedRadioButtonId()) {
                        case R.id.rButton_six_am:
                            changeTime = 1;
                            break;
                        case R.id.rButton_twelve_am:
                            changeTime = 2;
                            break;
                    }


                    switch (rGroupWallpaperNumber.getCheckedRadioButtonId()) {
                        case R.id.rBtn_five:
                            wallpaperAmount = 5;
                            break;

                        case R.id.rBtn_ten:
                            wallpaperAmount = 10;
                            break;

                        case R.id.rBtn_fifteen:
                            wallpaperAmount = 15;
                            break;
                    }
                }

                if (isOffline) {
                    quality = seekBarDownload.getProgress();
                }
                dailyWallpaper.onDailyPaperSet(isOffline, quality, wallpaperAmount, changeTime);
                if (isOffline) {
                    util.makeToast(context, context.getString(R.string.download_wallpaper));

                } else {
                    util.makeToast(context, context.getString(R.string.daily_wallpaper_set));
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    /**
     * interace implementation for on click listener for download image
     */
    public interface DailyWallpaper {
        /**
         *
         * @param isOffline | if the images will be stored in offline mode
         * @param quality | quality of the downloading image
         * @param wallpaperAmount | amount of wallpaper to be stored at a time, both online/offline
         * @param changeTime | when will the change occur
         */
        void onDailyPaperSet(boolean isOffline, int quality, int wallpaperAmount, int changeTime);
    }
}
