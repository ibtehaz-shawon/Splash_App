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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
        Context context = this;
        ImageView img = (ImageView)findViewById(R.id.img_image);
//        TextView txt = (TextView)findViewById(R.id.txt_local_id);
        ArrayList<FeedModel> allData = new SplashDb().returnAllImage();
//
//        for (int i = 0; i < allData.size(); i++) {
//            if (allData.get(i).getLocationName() != null) {
//                txt.setText(txt.getText() + " "+i+ " "+ allData.get(i).getLocationName() + "\n");
//            } else {
//                txt.setText(txt.getText() + " "+i+ " null returned! " + "\n");
//            }
//        }

        try {
            FileInputStream fis = context.openFileInput("316");
            Log.d("1234", "odjkklgflgjfklgjklfjgksjkfsjdfisdilgfsdlgls g54345343");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            ArrayList<Byte> imageData = new ArrayList<>();
            int current;


            Log.d("1234", "o54545345343543535534");
            while ((current = bufferedReader.read()) != -1) {
                imageData.add((byte)current);
            }

            byte[] imageArray = new byte[imageData.size()];

            for (int i = 0; i < imageData.size(); i++) {
                imageArray[i] = imageData.get(i);
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inScaled = true;
            options.inSampleSize = 1;

            Bitmap output = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length, options);
            Log.d("1234", "odjosjgosgo;sg ");

            if (output == null) {
                Log.d("1234", "baal amar!!");
            } else {
                Log.d("1234", "l;jdgsdgjsdl;klllllllllllllllllllllllllllllllllllllllllllllllllsg ");
            }
            img.setImageBitmap(output);
        } catch (IOException iox) {
            iox.printStackTrace();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
