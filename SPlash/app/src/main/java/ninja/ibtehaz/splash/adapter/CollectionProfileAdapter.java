package ninja.ibtehaz.splash.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.models.CollectionModel;
import ninja.ibtehaz.splash.models.FeedModel;
import ninja.ibtehaz.splash.viewHolder.CollectionProfileViewHolder;
import ninja.ibtehaz.splash.viewHolder.CollectionViewHolder;
import ninja.ibtehaz.splash.viewHolder.FeedViewHolder;

/**
 * Created by ibtehaz on 5/18/17.
 */

public class CollectionProfileAdapter extends RecyclerView.Adapter implements FeedViewHolder.DailyWallpaper {

    private class ViewType {
        int PROFILE_HEADER = 0;
        int PROFILE_BODY = 1;
    }

    private ViewType viewType;
    private Context context;
    private CollectionModel profileDataModel;
    private ArrayList<FeedModel> feedModel;

    /**
     * collection profile constructor. holds every value needed to view
     * @param context
     * @param collectionModel
     * @param dataModel
     */
    public CollectionProfileAdapter(Context context, CollectionModel collectionModel,
                                    ArrayList<FeedModel> dataModel) {
        viewType = new ViewType();
        this.context = context;
        this.profileDataModel = collectionModel;
        this.feedModel = dataModel;
    }



    /**
     * creates an instance of viewholder based on view type
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("Error", "ViewType is -->> "+ viewType);

        if (viewType == this.viewType.PROFILE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection_profile,
                    parent, false);
            return new CollectionProfileViewHolder(view, context);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item_layout,
                    parent, false);
            return new FeedViewHolder(view, context, this);
        }
    }


    /**
     * binds data to view holder
     * @param holder multiple instance of holder is possible
     * @param position current item position on the list
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CollectionProfileViewHolder) {
            ((CollectionProfileViewHolder)holder).onBindData(profileDataModel);
        } else {
            ((FeedViewHolder)holder).bindDataToView(feedModel.get(position - 1), position);
        }
    }


    /**
     * total items on the list. contains both total feed size and collection profile as header
     * @return
     */
    @Override
    public int getItemCount() {
        return feedModel.size() + 1;
    }

    /**
     * get which item type to load on a certain view position
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return viewType.PROFILE_HEADER;
        } else {
            return viewType.PROFILE_BODY;
        }
    }


    /**
     *
     * @param isOffline
     */
    @Override
    public void onDailyPaperSet(boolean isOffline) {

    }
}
