package ninja.ibtehaz.splash.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.db_helper.SplashDb;
import ninja.ibtehaz.splash.models.FeedModel;

/**
 * Created by ibteh on 4/4/2017.
 */

public class TestLocalPhoto extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_photo);

        TextView txt = (TextView)findViewById(R.id.txt_local_id);
        ArrayList<FeedModel> allData = new SplashDb().returnAllImage();

        for (int i = 0; i < allData.size(); i++) {
            if (allData.get(i).getLocationName() != null) {
                txt.setText(txt.getText() + " "+i+ " "+ allData.get(i).getLocationName() + "\n");
            } else {
                txt.setText(txt.getText() + " "+i+ " null returned! " + "\n");
            }
        }
    }
}
