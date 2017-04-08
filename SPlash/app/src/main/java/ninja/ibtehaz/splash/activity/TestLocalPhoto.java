package ninja.ibtehaz.splash.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.db_helper.SplashDb;
import ninja.ibtehaz.splash.models.FeedModel;
import ninja.ibtehaz.splash.models.SplashDbModel;

/**
 * Created by ibteh on 4/4/2017.
 */

public class TestLocalPhoto extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_photo);
        Context context = this;
        ImageView img = (ImageView)findViewById(R.id.img_image);
        ArrayList<SplashDbModel> allData = new SplashDb().returnAllImage();
        for (int i = 0; i < allData.size(); i++) {
            if (allData.get(i).isOffline()) {
                try {
                    File f = new File(context.getFilesDir(), allData.get(i).getLocalFileName());
                    Bitmap output = BitmapFactory.decodeStream(new FileInputStream(f));
                    img.setImageBitmap(output);
                    break;
                } catch (IOException iox) {
                    iox.printStackTrace();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        }
    }
}
