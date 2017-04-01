package ninja.ibtehaz.splash.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import java.util.List;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.db_helper.SplashDb;

/**
 * Created by ibteh on 4/1/2017.
 */

public class SettingsActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TextView txt = (TextView)findViewById(R.id.txt_test);

        SplashDb db_test = new SplashDb();
        List<SplashDb> data = db_test.returnTitle();

        for (int i = 0; i < data.size(); i++) {
            SplashDb db_test1 = data.get(i);
            String line = db_test1.getTitle() + " _-_ "+db_test1.getDescription() + " ||} "+db_test1.getId();
            txt.setText(txt.getText().toString() + '\n' + line);
        }
    }
}
