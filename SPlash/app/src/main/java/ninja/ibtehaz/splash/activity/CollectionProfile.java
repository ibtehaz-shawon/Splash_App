package ninja.ibtehaz.splash.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.fragments.CollectionProfileFragments;
import ninja.ibtehaz.splash.models.CollectionModel;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibtehaz on 3/14/17.
 */

public class CollectionProfile extends BaseActivity
        implements View.OnClickListener {

    private final String TAG = "collection_photo";
    private Context context;
    private LinearLayout coordinatorLayout;

    private CollectionModel collectionModel;
    private String collectionId, method;
    private boolean isCurated, isEmpty;

    private TextView txtTitle;

    private Util util;
    private ImageView imgBack;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_profile);
        /*------------------------------------------------------*/
        init();
        /*------------------------------------------------------*/
        getExtra();
        /*------------------------------------------------------*/
        initiateFragments(collectionId, method, isCurated);
        /*------------------------------------------------------*/
    }


    /**
     *
     */
    private void getExtra() {
        this.collectionModel = (CollectionModel)getIntent().getSerializableExtra("collection_data");
        this.collectionId = collectionModel.getCollectionId();
        if (collectionModel.isCurated()) this.isCurated = true;
        else this.isCurated = false;

        if (isCurated) method = "1";
        else method = "2";

        txtTitle.setText(util.capitalizeWords(collectionModel.getCollectionTitle()));
    }


    /**
     *
     */
    private void init() {
        this.context = this;
        this.util = new Util();
        this.isEmpty = false;

        coordinatorLayout = (LinearLayout)findViewById(R.id.profile_coordinate_layout);
        imgBack = (ImageView)findViewById(R.id.img_back);
        txtTitle = (TextView)findViewById(R.id.txt_title);

        findViewById(R.id.app_bar_layout).setBackgroundResource(R.color.transparent);
        findViewById(R.id.toolbar).setBackgroundResource(R.drawable.dark_primary_no_radius);
        coordinatorLayout.setBackgroundResource(R.drawable.gradient_feed_bg);
        setSupportActionBar((android.support.v7.widget.Toolbar)findViewById(R.id.toolbar));

        imgBack.setOnClickListener(this);
    }

    /**
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }



    /**
     * initiates the fragments
     */
    private void initiateFragments(String collectionId, String method, boolean isCurated) {
        ProfileAdapterController profileAdapterController = new ProfileAdapterController(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);

        CollectionProfileFragments fragments = new CollectionProfileFragments();
        Bundle bundle1 = new Bundle();
        bundle1.putString("collectionId", collectionId);
        bundle1.putSerializable("collectionModel", collectionModel);
        bundle1.putString("method", method);
        bundle1.putBoolean("isCurated", isCurated);
        fragments.setArguments(bundle1);

        profileAdapterController.addFragment(fragments);
        viewPager.setAdapter(profileAdapterController);
        viewPager.setOffscreenPageLimit(1);
    }




    /**
     * ------------------------------------------------------------------
     * ------------------------------------------------------------------
     * ------------------------------------------------------------------
     * |||||||||||||||||||||     adapter fragment  ||||||||||||||||||||||
     * ------------------------------------------------------------------
     * ------------------------------------------------------------------
     * ------------------------------------------------------------------
     */
    public class ProfileAdapterController extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments = new ArrayList<>();

        /**
         *
         * @param fm
         */
        public ProfileAdapterController(FragmentManager fm) {
            super(fm);
        }

        /**
         *
         * @param fragment
         */
        private void addFragment(Fragment fragment) {
            fragments.add(fragment);
        }

        /**
         *
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        /**
         *
         * @return
         */
        @Override
        public int getCount() {
            return 1;
        }
    }
}
