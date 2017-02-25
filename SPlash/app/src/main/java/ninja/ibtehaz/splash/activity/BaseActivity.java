package ninja.ibtehaz.splash.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ninja.ibtehaz.splash.utility.PreferenceUtil;

/**
 * Created by ibteh on 2/20/2017.
 */

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private PreferenceUtil mMobileData = null;
    private String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMobileData = PreferenceUtil.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Initialize the loader for Child class whenever necessary.
     */
    public void initProgressLoader() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    /**
     * Sets whether this dialog is cancelable with the
     */
    protected void setProgressCancelable(boolean isCancelable) {
        if (progressDialog != null) {
            progressDialog.setCancelable(isCancelable);
        }
    }

    /**
     * Show progress dialog.
     *
     * @param message The message show in the progress dialog initially.
     */
    public void showDialog(String message) {
        if (progressDialog == null) {
            initProgressLoader();
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    /**
     * Cancel progress dialog.
     */
    public void cancelDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * Cancel progress dialog.
     */
    public boolean isDialogShowing() {
        if (progressDialog != null) {
            return progressDialog.isShowing();
        } else return false;
    }
}
