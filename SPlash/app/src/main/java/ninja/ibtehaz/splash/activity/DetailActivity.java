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
 * Created by ibteh on 2/21/2017.
 */

public class DetailActivity extends BaseActivity implements View.OnClickListener {

    private Context context;
    private ImageView imgBack, imgOriginal, imgProfilePic;
    private CircularFillableLoaders circularFillableLoaders;
    private ProgressBar progressBar;
    private Button btnSetWallpaper;
    private TextView txtUserDisplayName;
    private Util util;
    private FeedModel dataModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        /*---------------------------------------*/
        init();
        /*---------------------------------------*/
        loadData();
        /*---------------------------------------*/
        btnSetWallpaper.setOnClickListener(this);
    }


    /**
     * -----
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
        circularFillableLoaders = (CircularFillableLoaders)findViewById(R.id.circular_animated_loader);
        circularFillableLoaders.setVisibility(View.GONE);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
    }


    /**
     *
     */
    private void loadData() {
        progressBar.setIndeterminate(true);
        util.loadImage(context, dataModel.getUrlRegular(), imgOriginal, progressBar);
        util.loadProfilePic(context, dataModel.getUserProfilePic(), imgProfilePic);
        txtUserDisplayName.setText("Photo by "+dataModel.getUserDisplayName());
        Log.d("Helo", dataModel.getPhotoId() + " %%% ");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_wallpaper:
                util.setupWallpaperFromBackground(context,
                        dataModel.getUrlRegular(),
                        circularFillableLoaders, this);
                break;
        }
    }
}

