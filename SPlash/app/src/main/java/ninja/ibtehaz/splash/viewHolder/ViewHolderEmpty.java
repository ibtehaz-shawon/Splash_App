package ninja.ibtehaz.splash.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ninja.ibtehaz.splash.R;

/**
 * Created by ibtehaz on 5/18/17.
 */

public class ViewHolderEmpty extends RecyclerView.ViewHolder {


    private TextView txtErrorMessage;


    /**
     * constructor to create the view for emptyView
     * @param itemView
     */
    public ViewHolderEmpty(View itemView) {
        super(itemView);
        txtErrorMessage = (TextView)itemView.findViewById(R.id.txt_error_message);
    }

    /**
     * binds the empty view to the screen with a message
     * @param message
     */
    public void onBindData(String message) {
        txtErrorMessage.setText(message.toString());
    }
}
