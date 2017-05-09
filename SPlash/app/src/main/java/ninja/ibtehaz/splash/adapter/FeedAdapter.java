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
        return new FeedViewHolder(itemView, context, this);

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
     * TODO -> get random data if needed from server
     * @param isOffline
     */
    @Override
    public void onDailyPaperSet(boolean isOffline) {
        ArrayList<FeedModel> dataSet = new ArrayList<>();
        ArrayList<String> duplicate = new ArrayList<>();
        int counter = 0;
        int index;
        boolean flag = true;
        int duplicateCounter = 0;

        while (flag) {
            duplicateCounter = 0;
            index = 0;
            for (; ;) {
                if (index == 5) break; //TODO -> to parallaly test offline, online mode
                FeedModel model = dataModel.get(counter);
                if (model.getPhotoId() != null) { //first image id is null here. and it cannot be null
                    dataSet.add(model);
                    index++;
                }
                counter++;
            }

            if (isOffline) {
                duplicateCounter = new SplashDb().insertLocalImage(dataSet, context);
                if (duplicateCounter == 0) break;
            } else {
                duplicate = new SplashDb().insertFeedData(dataSet);
                if (duplicate.size() == 0) break;
            }
            dataSet.clear();
        }
    }
}
