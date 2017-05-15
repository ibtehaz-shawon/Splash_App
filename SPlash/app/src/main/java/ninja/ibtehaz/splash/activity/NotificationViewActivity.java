package ninja.ibtehaz.splash.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.models.SplashDbModel;

/**
 * Created by ibtehaz on 5/15/17.
 */

public class NotificationViewActivity extends BaseActivity {


    private Context context;
    private ArrayList<SplashDbModel> dataList;

    /**
     * on creates method creates a view
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);

        init();
    }


    /**
     *
     */
    private void init() {
        this.context = this;
        dataList = (ArrayList<SplashDbModel>) getIntent().getSerializableExtra("splashDbModel");

        if (dataList == null) {
            Log.d("InternalStorage", "data list is null too!");
        }
    }
}
