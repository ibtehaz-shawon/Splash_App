package ninja.ibtehaz.splash.activity;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.db_helper.SplashDb;
import ninja.ibtehaz.splash.models.SplashDbModel;
import ninja.ibtehaz.splash.utility.Util;

/**
 * @author ibtehaz
 * this class shows the saved image and clears the memory (delete all the photos) if user wants
 */
public class SavedImageActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<SplashDbModel> dataModel;
    private AdapterSavedImage adapterSavedImage;

    /**
     * on creates item initializer
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_image);

        init();

        getAllLocalData();

        listBind();
    }


    /**
     * initializing the view
     */
    private void init() {
        context = this;
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        findViewById(R.id.img_delete).setVisibility(View.VISIBLE);
        findViewById(R.id.img_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SplashDb().removeAll(context);
                dataModel.clear();
                adapterSavedImage.notifyDataSetChanged();

                new Util().showSuccessToast(context.getString(R.string.all_data_removed), SavedImageActivity.this);

                //initiate empty view
                initEmptyView(context.getString(R.string.no_saved_photo));
            }
        });
    }


    /**
     * get all locally stored data
     */
    private void getAllLocalData() {
        dataModel = new SplashDb().returnAllImage();

        //filtering all the Null values
        for (int i = 0; i < dataModel.size(); i++) {
            SplashDbModel model = ((SplashDbModel)dataModel.get(i));
            if (model.getUrlSmall() == null || model.getUrlRaw() == null) {
                dataModel.remove(i);
            }
        }

        if (dataModel.size() == 0)initEmptyView(context.getString(R.string.no_saved_photo));
    }


    /**
     * binds data to the view
     */
    private void listBind() {
        if (adapterSavedImage == null) {
            adapterSavedImage = new AdapterSavedImage(dataModel);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 4,
                    LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(1500);
            itemAnimator.setChangeDuration(1000);
            recyclerView.setItemAnimator(itemAnimator);
            recyclerView.setHasFixedSize(false);
            recyclerView.setAdapter(adapterSavedImage);
        }
        adapterSavedImage.notifyDataSetChanged();
    }


    /**
     * initiate this layout if there is no item
     */
    private void initEmptyView(String message) {
        recyclerView.setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_empty_view)).setText(message.trim());
        findViewById(R.id.ll_empty_view).setVisibility(View.VISIBLE);

        findViewById(R.id.img_delete).setClickable(false);
        findViewById(R.id.img_delete).setAlpha(0.3f);
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
    private class AdapterSavedImage extends RecyclerView.Adapter {

        private ArrayList<?> dataModel;

        /**
         *
         * @param dataModel
         */
        public AdapterSavedImage(ArrayList<?> dataModel) {
            this.dataModel = dataModel;
        }

        /**
         * initialize the view holder
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.settings_item_layout, parent, false);
            return new SavedImageViewHolder(itemView);
        }

        /**
         * binds each item to the view
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((SavedImageViewHolder) holder).bindDataToView((SplashDbModel) dataModel.get(position), position);
        }


        /**
         * returns size of the item
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
    private class SavedImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgLeft, imgLayerLeft;
        private FrameLayout frameLeft;
        private TextView txtAuthorLeft;

        private SplashDbModel currentDataModel;
        private int currentPosition;
        private View itemView;
        private Util util;


        /**
         *
         * @param itemView
         */
        public SavedImageViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.util = new Util();

            imgLeft = (ImageView)itemView.findViewById(R.id.img_left_original);
            imgLayerLeft = (ImageView)itemView.findViewById(R.id.img_layer_left);
            frameLeft = (FrameLayout)itemView.findViewById(R.id.frame_left);
            txtAuthorLeft = (TextView)itemView.findViewById(R.id.txt_author_left);
        }


        /**
         * binds each data to the viewholder's view
         * @param model
         * @param position
         */
        public void bindDataToView(SplashDbModel model, int position) {
            this.currentDataModel = model;
            this.currentPosition = position;

            imgLayerLeft.setVisibility(View.VISIBLE);
            txtAuthorLeft.setVisibility(View.GONE);
            itemView.findViewById(R.id.ll_daily_wallpaper).setVisibility(View.GONE);

            if (model.isOffline()) {
                if (model.getLocalFileName() != null) {
                    new Util().getInternalStorageImage(
                            model.getLocalFileName(),
                            context,
                            imgLeft);

                    itemView.findViewById(R.id.ll_archived).setVisibility(View.VISIBLE);
                }
            } else {
                itemView.findViewById(R.id.ll_archived).setVisibility(View.GONE);
                loadPicture();
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
