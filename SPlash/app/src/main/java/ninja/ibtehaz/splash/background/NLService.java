package ninja.ibtehaz.splash.background;

import android.service.notification.NotificationListenerService;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by ibtehaz on 5/15/17.
 * @author ibtehaz
 * this class currently does NOTHING!
 */

@SuppressLint("NewApi")
public class NLService extends NotificationListenerService {

    private String TAG = NLService.class.getSimpleName();
    public static final String NOT_TAG = "ninja.ibtehaz.splash.NOTIFICATION_LISTENER";
    public static final String NOT_POSTED = "POSTED";
    public static final String NOT_REMOVED = "REMOVED";
    public static final String NOT_EVENT_KEY = "not_key";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     *
     * @param sbn
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "**********  onNotificationPosted");
        Intent i = new Intent(NOT_TAG);
        i.putExtra(NOT_EVENT_KEY, NOT_POSTED);
        sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "********** onNOtificationRemoved");
        Intent i = new Intent(NOT_TAG);
        i.putExtra(NOT_EVENT_KEY, NOT_REMOVED);
        sendBroadcast(i);
    }
}
