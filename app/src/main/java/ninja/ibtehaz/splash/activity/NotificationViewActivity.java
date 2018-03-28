package ninja.ibtehaz.splash.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.models.SplashDbModel;
import ninja.ibtehaz.splash.utility.Util;

/**
 * Created by ibtehaz on 5/15/17.
 */


public class NotificationViewActivity extends BaseActivity {


    private final String TAG = "InternalStorage";
    private Context context;
    private ArrayList<SplashDbModel> dataList;
    private RecyclerView recyclerView;
    private NotificationViewAdapter notificationViewAdapter;
    private NotificationBroadcastReceiver broadcastReciever;

    /**
     * on creates method creates a view
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);

        init();
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReciever,
                new IntentFilter(Util.NOTIFICATION_BROADCAST_CONSTANT));
        listBindData();
    }


    /**
     *
     */
    private void init() {
        this.context = this;
        SplashDbModel splashDbModel =  (SplashDbModel) getIntent().getSerializableExtra("splashDbModel");
        dataList = splashDbModel.getSplashDbModels();
        broadcastReciever = new NotificationBroadcastReceiver();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_notification);
    }


    @Override
    protected void onDestroy() {
        try {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReciever);
        }catch (IllegalArgumentException illegal) {
            illegal.printStackTrace();
        }
        super.onDestroy();
    }


    /**
     *
     */
    private void listBindData() {
        if (notificationViewAdapter == null) {
            notificationViewAdapter = new NotificationViewAdapter();
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(1500);
            itemAnimator.setChangeDuration(1000);
            recyclerView.setItemAnimator(itemAnimator);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(notificationViewAdapter);
        }

        notificationViewAdapter.notifyDataSetChanged();
    }


    /**
     * this class handles the adapter of Notification views adapter
     */
    private class NotificationViewAdapter extends RecyclerView.Adapter {


        public NotificationViewAdapter() {
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
                    .inflate(R.layout.notification_view_item, parent, false);
            return new NotificationViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((NotificationViewHolder)holder).onBindViewData(dataList.get(position));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }


    /**
     *
     */
    private class NotificationViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgDownloading;
        private TextView txtDownloadStatus;
        private ProgressBar pBarDownloadStatus, pBarImage;

        public NotificationViewHolder(View itemView) {
            super(itemView);

            imgDownloading = (ImageView)itemView.findViewById(R.id.img_original);
            txtDownloadStatus = (TextView)itemView.findViewById(R.id.txt_download_status);
            pBarDownloadStatus = (ProgressBar)itemView.findViewById(R.id.progress_bar);
            pBarImage = (ProgressBar)itemView.findViewById(R.id.progress_bar_image);
        }


        /**
         *
         */
        private void onBindViewData(SplashDbModel model) {
            if (model.isDownloaded()) {
                txtDownloadStatus.setText(context.getString(R.string.download_complete));
                pBarDownloadStatus.setIndeterminate(false);
                pBarDownloadStatus.setVisibility(View.GONE);
            } else {
                txtDownloadStatus.setText(context.getString(R.string.download_incomplete));
                pBarDownloadStatus.setIndeterminate(true);
                pBarDownloadStatus.setVisibility(View.VISIBLE);
            }

            if (model.getUrlSmall() != null) {
                new Util().loadImage(context, model.getUrlSmall(), imgDownloading, pBarImage);
            } else {
                new Util().loadImage(context, model.getUrlRaw(), imgDownloading, pBarImage);
            }
        }
    }


    /**
     *
     */
    public class NotificationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "------------- Broadcast Recieved -------------");
            if (intent.getAction().equals(Util.NOTIFICATION_BROADCAST_CONSTANT)) {
                long uniqueId = intent.getLongExtra(Util.NOTIFICATION_BROADCAST_ID_EXTRA, -1);
                Log.d(TAG, "------------- Broadcast Filter matched -------------{{}} "+uniqueId);
                for (int i = 0; i < dataList.size(); i++) {
                    if (dataList.get(i).getUniqueId() == uniqueId) {
                        dataList.get(i).setDownloaded(true);
                        if (notificationViewAdapter != null) notificationViewAdapter.notifyDataSetChanged();

                        Log.d(TAG, "------------- Broadcast View Found -------------");
                        break;
                    }
                }
            }
        }
    }
}
