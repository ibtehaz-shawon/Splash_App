package ninja.ibtehaz.splash.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.models.CollectionModel;
import ninja.ibtehaz.splash.viewHolder.CollectionViewHolder;
import ninja.ibtehaz.splash.viewHolder.FeedViewHolder;

/**
 * Created by ibtehaz on 3/13/17.
 */

public class CollectionAdapter extends RecyclerView.Adapter {

    private ArrayList<CollectionModel> dataModel;
    private Context context;

    /**
     *
     * @param dataModel
     * @param context
     */
    public CollectionAdapter(ArrayList<CollectionModel> dataModel, Context context) {
        this.dataModel = dataModel;
        this.context = context;
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
                .inflate(R.layout.item_collection, parent, false);
        return new CollectionViewHolder(itemView, context);
    }

    /**
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CollectionViewHolder)holder).onBindData(dataModel.get(position));
    }

    /**
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return dataModel.size();
    }
}
