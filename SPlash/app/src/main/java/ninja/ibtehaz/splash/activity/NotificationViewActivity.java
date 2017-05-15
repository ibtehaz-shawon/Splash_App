package ninja.ibtehaz.splash.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import ninja.ibtehaz.splash.R;

/**
 * Created by ibtehaz on 5/15/17.
 */

public class NotificationViewActivity extends BaseActivity {


    private Context context;

    /**
     * on creates method creates a view
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
    }


    /**
     *
     */
    private void init() {
       this.context = this;
    }
}
