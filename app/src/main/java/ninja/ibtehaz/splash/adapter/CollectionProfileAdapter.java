package ninja.ibtehaz.splash.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.db_helper.SplashDb;
import ninja.ibtehaz.splash.models.CollectionModel;
import ninja.ibtehaz.splash.models.FeedModel;
import ninja.ibtehaz.splash.utility.Util;
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
    private Activity callingActivity;


    /**
     * collection profile constructor. holds every value needed to view
     * @param context
     * @param collectionModel | collection model, hold the collection details
     * @param dataModel | data model, hold all the value of collection data
     * @param errorMessage | error message, in case if error occurs
     * @param activity | calling activity
     */
    public CollectionProfileAdapter(Context context, CollectionModel collectionModel,
                                    ArrayList<FeedModel> dataModel, String errorMessage, Activity activity) {
        viewType = new ViewType();
        this.context = context;
        this.profileDataModel = collectionModel;
        this.feedModel = dataModel;
        this.errorMessage = errorMessage;
        this.callingActivity = activity;

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
    public void onDailyPaperSet(boolean isOffline, int quality, int wallpaperAmount, int changeTime, boolean isRandom, String collectionId) {

        if (!checkDailyPhotoStatus()) return; //check if the app already has 10 photos. 10 is the maximum number
        collectionId = profileDataModel.getCollectionId();

        ArrayList<FeedModel> dataSet = new ArrayList<>();
        int duplicate = 0, counter = 0, loopCounter = 0;

        if (isRandom) {
            // call server from here
            new Util().makeToast(context, "get random photo from server!");
        } else {
            boolean flag = true;

            while (flag) {
                while (true) {
                    if (loopCounter == wallpaperAmount) break;
                    FeedModel model;

                    if (counter < feedModel.size()) {
                        model = feedModel.get(counter);
                    } else {
                        flag = false;
                        break;
                    }

                    if (model.getPhotoId() != null) {
                        //first image id is null here. and it cannot be null
                        dataSet.add(model);
                        loopCounter++;
                    }
                    counter++;
                }

                if (isOffline) {
                    duplicate = new SplashDb().insertLocalImage(dataSet, context, quality, changeTime, wallpaperAmount, collectionId);
                } else {
                    duplicate = new SplashDb().insertFeedData(dataSet, changeTime, wallpaperAmount, collectionId);
                }

                dataSet.clear();
                if (duplicate == 0) {
                    flag = false;
                    new Util().makeToast(context, context.getString(R.string.daily_wallpaper_set));
                }
                else {
                    wallpaperAmount = duplicate;
                    loopCounter = 0;
                }
            }
        }
    }


    /**
     * checks if user has already sets up 10 photo as his daily wallpaper
     */
    private boolean checkDailyPhotoStatus() {
        long value = new SplashDb().totalPhotos();
        if (value == 10l) {
            new Util().showErrorToast(context.getString(R.string.maximum_photos), callingActivity);
            return false;
        } else return true;
    }
}
