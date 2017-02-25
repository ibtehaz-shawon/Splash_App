package ninja.ibtehaz.splash.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.models.FeedModel;
import ninja.ibtehaz.splash.viewHolder.FeedViewHolder;

/**
 * Created by ibteh on 2/20/2017.
 */

public class FeedAdapter extends RecyclerView.Adapter {

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


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_item_layout, parent, false);
        return new FeedViewHolder(itemView, context);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((FeedViewHolder)holder).bindDataToView(dataModel.get(position), position);
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }
}
