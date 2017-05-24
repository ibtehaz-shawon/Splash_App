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
import ninja.ibtehaz.splash.viewHolder.FeedViewHolder;
import ninja.ibtehaz.splash.viewHolder.ViewHolderEmpty;

/**
 * Created by ibtehaz on 5/18/17.
 */

public class CollectionProfileAdapter extends RecyclerView.Adapter implements FeedViewHolder.DailyWallpaper {

    private class ViewType {
        int PROFILE_HEADER = 0;
        int PROFILE_BODY = 1;
        int PROFILE_EMPTY_BODY = 2;
    }

    private ViewType viewType;
    private Context context;
    private CollectionModel profileDataModel;
    private ArrayList<FeedModel> feedModel;
    private int EMPTY_VIEW;
    private String errorMessage;


    /**
     * collection profile constructor. holds every value needed to view
     * @param context
     * @param collectionModel
     * @param dataModel
     */
    public CollectionProfileAdapter(Context context, CollectionModel collectionModel,
                                    ArrayList<FeedModel> dataModel, String errorMessage) {
        viewType = new ViewType();
        this.context = context;
        this.profileDataModel = collectionModel;
        this.feedModel = dataModel;
        this.errorMessage = errorMessage;

        if (feedModel.size() == 0) this.EMPTY_VIEW = 1;
        else this.EMPTY_VIEW = 0;
    }


    /**
     * sets up error message if value changes
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }



    /**
     * creates an instance of viewholder based on view type
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (feedModel.size() == 0) this.EMPTY_VIEW = 1;
        else this.EMPTY_VIEW = 0;

        if (viewType == this.viewType.PROFILE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collection_profile,
                    parent, false);
            return new CollectionProfileViewHolder(view, context);
        } else if (viewType == this.viewType.PROFILE_BODY){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item_layout,
                    parent, false);
            return new FeedViewHolder(view, context, this, profileDataModel.getCollectionId());
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_view, parent, false);
            return new ViewHolderEmpty(view);
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
        } else if (holder instanceof FeedViewHolder){
            if (EMPTY_VIEW == 1) {
                ((FeedViewHolder)holder).bindDataToView(feedModel.get(position - 2), position);
            } else {
                ((FeedViewHolder)holder).bindDataToView(feedModel.get(position - 1), position);
            }
        } else {
            ((ViewHolderEmpty)holder).onBindData(errorMessage);
        }
    }


    /**
     * total items on the list. contains both total feed size and collection profile as header
     * @return
     */
    @Override
    public int getItemCount() {
        //total feed data + header + if empty, then extra 1 more view
        return feedModel.size() + 1 + EMPTY_VIEW;
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
            if (feedModel.size() == 0) {
                return viewType.PROFILE_EMPTY_BODY;
            } else {
                return viewType.PROFILE_BODY;
            }
        }
    }


    /**
     *
     * @param isOffline
     */
    @Override
    public void onDailyPaperSet(boolean isOffline, int quality, int wallpaperAmount, int changeTime, boolean isRandom, String collectionId) {}
}
