package ninja.ibtehaz.splash.viewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.models.CollectionModel;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibtehaz on 3/13/17.
 */

public class CollectionViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    private ImageView imgOriginal, imgLayer;
    private TextView txtCollectionTitle, txtUserName;
    private Util util;

    public CollectionViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        this.util = new Util();

        imgOriginal = (ImageView)itemView.findViewById(R.id.img_cover);
        imgLayer = (ImageView)itemView.findViewById(R.id.img_layer);
        txtCollectionTitle = (TextView)itemView.findViewById(R.id.txt_title);
        txtUserName = (TextView)itemView.findViewById(R.id.txt_author);

    }


    /**
     *
     * @param model
     */
    public void onBindData(CollectionModel model) {
        imgLayer.setBackgroundColor(Color.parseColor(model.getCoverColor()));
        util.loadImage(context, model.getUrlRegular(), imgOriginal, imgLayer);
        txtCollectionTitle.setText(model.getCollectionTitle());
        txtUserName.setText(model.getUsername());
    }
}
