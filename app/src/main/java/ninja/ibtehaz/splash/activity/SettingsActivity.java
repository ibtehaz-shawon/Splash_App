package ninja.ibtehaz.splash.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.db_helper.SplashDb;

/**
 * this class contains the settings related value app wise
 * Created by ibtehaz on 4/1/2017.
 */

public class SettingsActivity extends BaseActivity implements View.OnClickListener {


    private Context context;
    private TextView txtLocallyStored, txtChangeTime, txtTotalWallpaperSet,
            txtTotalPhotos;
    private Switch switchDailyWallpaper;
    private Button btnShowPhotos;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();
        inflateData();
        btnShowPhotos.setOnClickListener(this);
        switchDailyWallpaper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //TODO do something!
                }
            }
        });
    }


    /**
     * initiates the view
     */
    private void init() {
        context = this;
        btnShowPhotos = (Button)findViewById(R.id.btn_show_photos);
        switchDailyWallpaper = (Switch)findViewById(R.id.switch_daily_wallpaper);
        txtChangeTime = (TextView)findViewById(R.id.txt_change_time);
        txtLocallyStored = (TextView)findViewById(R.id.txt_locally_stored);
        txtTotalPhotos = (TextView)findViewById(R.id.txt_total_photos);
        txtTotalWallpaperSet = (TextView)findViewById(R.id.txt_total_wallpaper_set);

        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.txt_title)).setText(context.getString(R.string.settings));
    }

    /**
     * on click listener implementation
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_photos:
                break;

        }
    }


    /**
     * called when there is no data locally
     */
    private void noSavedData() {
        btnShowPhotos.setEnabled(false);
        btnShowPhotos.setAlpha(0.3f);
        btnShowPhotos.setText(context.getString(R.string.no_saved_photo));
    }


    /**
     * hardware back implementation
     */
    @Override
    public void onBackPressed() {
        finish();
    }


    /**
     * inflate data to UI
     */
    private void inflateData() {
        if (new SplashDb().isDailyWallpaper()) {
            switchDailyWallpaper.setChecked(true);
        } else {
            switchDailyWallpaper.setChecked(false);
        }

        txtTotalPhotos.setText(new SplashDb().totalPhotos() + "");
        txtChangeTime.setText(new SplashDb().returnChangeTime() + "");
        txtLocallyStored.setText(new SplashDb().getLocallyStoredCounter() + "");
        txtTotalWallpaperSet.setText("0");

        if (new SplashDb().totalPhotos() == 0l) {
            switchDailyWallpaper.setChecked(false);
            switchDailyWallpaper.setAlpha(0.3f);
            switchDailyWallpaper.setClickable(false);
            noSavedData();
        }
    }
}
