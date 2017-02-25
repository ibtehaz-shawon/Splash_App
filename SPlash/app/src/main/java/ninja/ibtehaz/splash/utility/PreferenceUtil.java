package ninja.ibtehaz.splash.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ibteh on 2/20/2017.
 */

public class PreferenceUtil {

    private static PreferenceUtil mPreferenceInstance;

    private static SharedPreferences mSharedPreferences;

    /**
     * Private constructor is used to initiate SingleTon instance
     * @param  context
     * */

    private PreferenceUtil(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }


    /**
     * Singleton instance
     * @param  context
     * */

    public static synchronized PreferenceUtil getInstance(Context context) {
        if (mPreferenceInstance == null) {
            mPreferenceInstance = new PreferenceUtil(context);
        }
        return mPreferenceInstance;
    }



    /**
     * Get preference value using key
     * @param  key
     * @param defaultValue
     * */

    public String getString(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    /**
     * Set preference string
     * @param  key
     * @param value
     * */

    public void setString(String key, String value) {
        getEditor().putString(key, value).commit();
    }

    /**
     * Get boolean preference if the value is available otherwise it will return false
     * @param  key
     * */

    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    /**
     * Get boolean preference
     * @param  key
     * @param defaultValue
     * */

    public boolean getBoolean(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Set boolean preference
     * @param  key
     * @param value
     * */

    public void setBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value).commit();
    }

    /**
     * Set int preference
     * @param  key
     * @param value
     * */

    public void setInt(String key, int value) {
        getEditor().putInt(key, value).commit();
    }

    /**
     * Get int preference
     * @param  key
     * @param defaultValue
     * */

    public int getInt(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Get int preference where default value is zero
     * @param  key
     * */

    public int getInt(String key) {
        return mSharedPreferences.getInt(key, 0);
    }

    /**
     * Set double preference
     * @param  key
     * @param value
     * */

    public void setDouble(String key, double value) {
        getEditor().putLong(key, Double.doubleToRawLongBits(value)).commit();
    }

    /**
     * Get double preference
     * @param  key
     * @param defaultValue
     * */

    public double getDouble(String key, double defaultValue) {
        return Double.longBitsToDouble(mSharedPreferences.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    /**
     * Get double preference where default value is zero
     * @param  key
     * */

    public double getDouble(String key) {
        return Double.longBitsToDouble(mSharedPreferences.getLong(key, 0));
    }

    /**
     * Set long preference
     * @param  key
     * @param value
     * */

    public void setLong(String key, long value) {
        getEditor().putLong(key, value).commit();
    }

    /**
     * Get long preference
     * @param  key
     * @param defaultValue
     * */

    public long getLong(String key, long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    /**
     * Get long preference where default value is zero
     * @param  key
     * */

    public long getLong(String key) {
        return mSharedPreferences.getLong(key, 0);
    }

    /**
     * Editor to write into SharedPreference file
     * */

    private SharedPreferences.Editor getEditor(){
        return mSharedPreferences.edit();
    }
}
