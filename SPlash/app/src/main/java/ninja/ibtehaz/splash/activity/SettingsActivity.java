package ninja.ibtehaz.splash.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.adapter.FeedAdapter;
import ninja.ibtehaz.splash.db_helper.SplashDb;
import ninja.ibtehaz.splash.models.FeedModel;
import ninja.ibtehaz.splash.utility.Util;
import ninja.ibtehaz.splash.viewHolder.FeedViewHolder;

/**
 * Created by ibteh on 4/1/2017.
 */

public class SettingsActivity extends BaseActivity implements View.OnClickListener {


    private Context context;
    private Button btnShowPhotos;
    private RecyclerView recyclerView;
    private ArrayList<?> dataModel;
    private AdapterSettings adapterSettings;


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();
        getAllLocalData();
        listBind();

        btnShowPhotos.setOnClickListener(this);
    }


    /**
     *
     */
    private void init() {
        context = this;
        btnShowPhotos = (Button)findViewById(R.id.btn_show_photos);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        dataModel = new ArrayList<>();
    }

    /**
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_photos:
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                    btnShowPhotos.setText("Show Saved Photos");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    btnShowPhotos.setText("Hide Saved Photos");
                }
                ((LinearLayout)findViewById(R.id.ll_settings_part)).setBackgroundResource(R.drawable.white_all_radius);
                adapterSettings.notifyDataSetChanged();
                break;

        }
    }

    /**
     *
     */
    private void getAllLocalData() {
        dataModel = new SplashDb().returnAllImage();

        //filtering NULL Vals
        for (int i = 0; i < dataModel.size(); i++) {
            FeedModel model = ((FeedModel)dataModel.get(i));
            if (model.getUrlSmall() == null || model.getUrlRaw() == null) {
                dataModel.remove(i);
            }
        }

        if (dataModel.size() == 0) {
            btnShowPhotos.setEnabled(false);
            recyclerView.setVisibility(View.GONE);
            btnShowPhotos.setAlpha(0.3f);
            btnShowPhotos.setText("No Saved Photos!");
        }
    }


    /**
     *
     */
    private void listBind() {
        if (adapterSettings == null) {
            adapterSettings = new AdapterSettings(dataModel);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 4,
                    LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(1500);
            itemAnimator.setChangeDuration(1000);
            recyclerView.setItemAnimator(itemAnimator);
            recyclerView.setHasFixedSize(false);
            recyclerView.setAdapter(adapterSettings);
        }
        adapterSettings.notifyDataSetChanged();
    }



    /**
     * ---------------------------------------------------------------------
     * ---------------------------------------------------------------------
     * ---------------------------------------------------------------------
     * ---------------------------------------------------------------------
     * ---------------------------------------------------------------------
     * ---------------------------------------------------------------------
     * ---------------------------------------------------------------------
     */
    private class AdapterSettings extends RecyclerView.Adapter {

        private ArrayList<?> dataModel;

        /**
         *
         * @param dataModel
         */
        public AdapterSettings(ArrayList<?> dataModel) {
            this.dataModel = dataModel;
        }

        /**
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.settings_item_layout, parent, false);
            return new SettingsViewHolder(itemView);
        }

        /**
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((SettingsViewHolder) holder).bindDataToView((FeedModel) dataModel.get(position), position);
        }


        /**
         *
         * @return
         */
        @Override
        public int getItemCount() {
            return dataModel.size();
        }
    }


    /**
     * ---------------------------------------------------------------------
     * ---------------------------------------------------------------------
     * ---------------------------------------------------------------------
     * ---------------------------------------------------------------------
     * ---------------------------------------------------------------------
     * ---------------------------------------------------------------------
     * ---------------------------------------------------------------------
     */
    private class SettingsViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgLeft, imgLayerLeft;
        private FrameLayout frameLeft;
        private TextView txtAuthorLeft;

        private FeedModel currentDataModel;
        private int currentPosition;
        private View itemView;
        private Util util;


        /**
         *
         * @param itemView
         */
        public SettingsViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.util = new Util();

            imgLeft = (ImageView)itemView.findViewById(R.id.img_left_original);
            imgLayerLeft = (ImageView)itemView.findViewById(R.id.img_layer_left);
            frameLeft = (FrameLayout)itemView.findViewById(R.id.frame_left);
            txtAuthorLeft = (TextView)itemView.findViewById(R.id.txt_author_left);
        }


        /**
         *
         * @param model
         * @param position
         */
        public void bindDataToView(FeedModel model, int position) {
            this.currentDataModel = model;
            this.currentPosition = position;

            if (model.isView()) {
                imgLeft.setVisibility(View.GONE);
                imgLayerLeft.setVisibility(View.GONE);
                itemView.findViewById(R.id.ll_daily_wallpaper).setVisibility(View.VISIBLE);
                txtAuthorLeft.setVisibility(View.GONE);
            } else {
                imgLayerLeft.setVisibility(View.VISIBLE);
//                imgLayerLeft.setBackgroundColor(Color.parseColor(model.getColor()));
                loadPicture();
                txtAuthorLeft.setVisibility(View.GONE);
                itemView.findViewById(R.id.ll_daily_wallpaper).setVisibility(View.GONE);
            }
        }


        /**
         * checks which url would be able to load image
         */
        private void loadPicture() {
            util.loadImage(context, currentDataModel.getUrlSmall(), imgLeft, imgLayerLeft);
        }
    }
}
