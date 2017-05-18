package ninja.ibtehaz.splash.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.models.FeedModel;
import ninja.ibtehaz.splash.utility.Util;

/**
 * this activity shows the detailed information of a photo
 * Created by ibtehaz on 2/21/2017.
 */

public class DetailActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = DetailActivity.class.getSimpleName();
    private Context context;
    private ImageView imgBack, imgOriginal, imgProfilePic;
    private ProgressBar progressBar;
    private Button btnSetWallpaper;
    private TextView txtUserDisplayName;
    private Util util;
    private FeedModel dataModel;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity_v2);

        /*---------------------------------------*/
        init();
        /*---------------------------------------*/
        loadData();
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
        imgOriginal = (ImageView)findViewById(R.id.img_original);
        imgProfilePic = (ImageView)findViewById(R.id.img_profile_ic);
        txtUserDisplayName = (TextView)findViewById(R.id.txt_user_name);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
    }


    /**
     * loads the data on the view
     */
    private void loadData() {
        progressBar.setIndeterminate(true);
        util.loadImage(context, dataModel.getUrlRegular(), imgOriginal, progressBar);
        util.loadProfilePic(context, dataModel.getUserProfilePic(), imgProfilePic);
        txtUserDisplayName.setText(context.getString(R.string.photo_by_text) + " " + dataModel.getUserDisplayName());
        Log.d(TAG, dataModel.getPhotoId() + " %%% ");
    }


    /**
     * on click listener interface
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_wallpaper:
                util.makeToast(context, context.getString(R.string.wallpaper_setup_process_start));
                util.setupWallpaperFromBackground(context,
                        dataModel.getUrlRaw());
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
}

