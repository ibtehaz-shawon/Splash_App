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
        int counter;
        int index = 0;

        for (counter = 0; counter < 10;){
            if (index == 10) break;
            FeedModel model = dataModel.get(counter);
            if (model.getPhotoId() != null) { //first image id is null here. and it cannot be null
                dataSet.add(model);
                index++;
            }
            counter++;
        }

        ArrayList<String> duplicate = new SplashDb().insertFeedData(dataSet);
        new Util().makeToast(context, "Duplicate "+duplicate.size());
    }
}
