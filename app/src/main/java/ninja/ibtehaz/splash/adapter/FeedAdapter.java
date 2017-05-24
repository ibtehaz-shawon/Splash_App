package ninja.ibtehaz.splash.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.db_helper.SplashDb;
import ninja.ibtehaz.splash.models.FeedModel;
import ninja.ibtehaz.splash.utility.Util;
import ninja.ibtehaz.splash.viewHolder.FeedViewHolder;

/**
 * Created by ibtehaz on 2/20/2017.
 */

public class FeedAdapter extends RecyclerView.Adapter implements FeedViewHolder.DailyWallpaper {

    public class ViewType {
        int FEED_ONE = 1;
        int FEED_TWO = 2;
        int FEED_THREE = 3;
    }

    private ViewType viewType;

    private Context context;
    private ArrayList<FeedModel> dataModel;

    /**
     *
     * @param context
     * @param dataModel
     */
    public FeedAdapter(Context context, ArrayList<FeedModel> dataModel) {
        this.context = context;
        this.dataModel = dataModel;
        this.viewType = new ViewType();
    }


    /**
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_item_layout, parent, false);
        return new FeedViewHolder(itemView, context, this, null);

    }

    /**
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((FeedViewHolder)holder).bindDataToView(dataModel.get(position), position);
    }


    /**
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return dataModel.size();
    }


    /**
     * This is for FeedData. Collection Id will always be nullable.
     * @param isOffline | if the images will be stored in offline mode
     * @param quality | quality of the downloading image
     * @param wallpaperAmount | amount of wallpaper to be stored at a time, both online/offline
     * @param changeTime | when will the change occur
     * @param isRandom | if user enabled to get random data from server or first 5/10 feed datat
     * @param collectionId @nullable
     */
    @Override
    public void onDailyPaperSet(boolean isOffline, int quality, int wallpaperAmount, int changeTime, boolean isRandom, String collectionId) {
        ArrayList<FeedModel> dataSet = new ArrayList<>();
        int counter = 0, index = 0;

        if (isRandom) {
            // call server from here
            new Util().makeToast(context, "get random photo from server!");
        } else {
            while (true) {
                if (index == wallpaperAmount) break;
                FeedModel model = dataModel.get(counter);
                if (model.getPhotoId() != null) { //first image id is null here. and it cannot be null
                    dataSet.add(model);
                    index++;
                }
                counter++;
            }

            if (isOffline) {
                new SplashDb().insertLocalImage(dataSet, context, quality, changeTime, wallpaperAmount);
            } else {
                new SplashDb().insertFeedData(dataSet);
            }
            dataSet.clear();
        }
    }


    /**
     * get which item type to load on a certain view position
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position % 7 == 0) {
            return viewType.FEED_ONE;
        } else {
            return viewType.FEED_TWO;
        }
    }
}
