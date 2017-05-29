package ninja.ibtehaz.splash.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.adapter.FeedAdapter;
import ninja.ibtehaz.splash.db_helper.SplashDb;
import ninja.ibtehaz.splash.models.FeedModel;
import ninja.ibtehaz.splash.utility.Util;
import ninja.ibtehaz.splash.viewHolder.FeedViewHolder;

/**
 * this activity shows the detailed information of a photo
 * Created by ibtehaz on 2/21/2017.
 */

public class DetailActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = DetailActivity.class.getSimpleName();
    private Context context;
    private RecyclerView recyclerView;
    private ImageView imgBack;
    private Util util;
    private FeedModel dataModel;
    private Button btnSetWallpaper;
    private DetailsAdapter detailsAdapter;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        /*---------------------------------------*/
        init();
        /*---------------------------------------*/
        listBindData();
        /*---------------------------------------*/
        btnSetWallpaper.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }


    /**
     * initialize the view
     */
    private void init() {
        this.context = this;
        util = new Util();
        this.dataModel = (FeedModel) getIntent().getSerializableExtra("feed_details");
        imgBack = (ImageView)findViewById(R.id.img_back);
        btnSetWallpaper = (Button)findViewById(R.id.btn_set_wallpaper);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
    }


    private void listBindData() {
        if (detailsAdapter == null) {
            detailsAdapter = new DetailsAdapter(dataModel);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(1500);
            itemAnimator.setChangeDuration(1000);
            recyclerView.setItemAnimator(itemAnimator);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(detailsAdapter);
        }

        detailsAdapter.notifyDataSetChanged();
    }


    /**
     * on click listener interface
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_wallpaper:
                if (new SplashDb().isDailyWallpaper()) {
                    showAlertDialog();
                } else {
                    util.showSuccessToast(context.getString(R.string.wallpaper_setup_process_start), this);
                    util.setupWallpaperFromBackground(context,
                            dataModel.getUrlRaw());
                }
                break;

            case R.id.img_back:
                finish();
                break;
        }
    }


    /**
     * finishes the activity without keeping anything
     * END OF STORY!
     */
    @Override
    public void onBackPressed() {
        finish();
    }


    /**
     * ################################################################
     * ################################################################
     * ################################################################
     * ################################################################
     * ################################################################
     */
    /**
     * in class adapter view to handle the recycler view
     */
    private class DetailsAdapter extends RecyclerView.Adapter {

        private FeedModel dataModel;
        /**
         * default constructor holding the item
         * @param dataModel
         */
        public DetailsAdapter(FeedModel dataModel) {
            this.dataModel = dataModel;
        }

        /**
         * creates the view holder instance
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_detail, parent, false);
            return new DetailsViewHolder(itemView);
        }

        /**
         * binds data to view
         * @param holder | current view holder
         * @param position | current position of the item. which is actually always 0
         */
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((DetailsViewHolder)holder).onBindData(dataModel, position);
        }

        /**
         * the item size ONLY 1
         * @return
         */
        @Override
        public int getItemCount() {
            return 1;
        }
    }

    /**
     * ################################################################
     * ################################################################
     * ################################################################
     * ################################################################
     * ################################################################
     */
    /**
     * in class view holder to show the detailed item on the view
     */
    private class DetailsViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgOriginal, imgProfilePic;
        private ProgressBar progressBar;
        private TextView txtUserDisplayName;
        private View viewPositionZero, viewPositionOne;
        private FrameLayout frameOriginalImage;
        private LinearLayout llFooter;


        /**
         * the constructor to initialize the view components for each items
         * @param itemView
         */
        public DetailsViewHolder(View itemView) {
            super(itemView);

            imgOriginal = (ImageView)itemView.findViewById(R.id.img_original);
            imgProfilePic = (ImageView)itemView.findViewById(R.id.img_profile_ic);
            txtUserDisplayName = (TextView)itemView.findViewById(R.id.txt_user_name);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progress_bar);
            viewPositionZero = itemView.findViewById(R.id.view_position_zero);
            viewPositionOne = itemView.findViewById(R.id.view_position_one);
            frameOriginalImage = (FrameLayout) itemView.findViewById(R.id.frame_original_image);
            llFooter = (LinearLayout) itemView.findViewById(R.id.ll_footer);

        }


        /**
         * shows the only data model to the view
         * @param dataModel
         */
        public void onBindData(FeedModel dataModel, int position) {
            progressBar.setIndeterminate(true);
            util.loadImage(context, dataModel.getUrlRegular(), imgOriginal, progressBar);
            Log.d(TAG, dataModel.getPhotoId() + " %%% ");

            util.loadProfilePic(context, dataModel.getUserProfilePic(), imgProfilePic);
            txtUserDisplayName.setText(context.getString(R.string.photo_by_text) + " " + dataModel.getUserDisplayName());
        }
    }


    /**
     * shows an alert dialog before confirming the daily wallpaper cancel setup
     */
    private void showAlertDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.item_daily_wallpaper_set);

        final Button btnConfirm = (Button)dialog.findViewById(R.id.btn_confirm);
        final Button btnCancel = (Button)dialog.findViewById(R.id.btn_cancel);

        /**
         * confirm btn on click listener handler
         */
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                util.showSuccessToast(context.getString(R.string.wallpaper_setup_process_start), DetailActivity.this);
                util.setupWallpaperFromBackground(context,
                        dataModel.getUrlRaw());
                //cancel the daily wallpaper setup
                new SplashDb().statusDailyWallpaper(false);
                dialog.dismiss();
            }
        });


        /**
         * cancels the fucking dialog
         */
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}

