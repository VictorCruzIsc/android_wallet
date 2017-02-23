package Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vicco on 23/02/17.
 */

public class UtilsSharedPreferences {
    private static final String SHAREDPREFERENCES_FILE = "com.src.sharedpreferences";
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;
    public static final String DEFAULT_STRING_VALUE = "noValue";
    public static final Integer DEFAULT_INTEGER_VALUE = -1;
    public static final Boolean DEFAULT_BOOLEAN_VALUE = Boolean.FALSE;

    public static void initSharedPreferences(Context context){
        if(mSharedPreferences == null){
            mSharedPreferences = context.getSharedPreferences(SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
            mEditor = mSharedPreferences.edit();
        }
    }

    public static String readString(String key){
        return mSharedPreferences.getString(key, DEFAULT_STRING_VALUE);
    }

    public static int readInt(String key){
        return mSharedPreferences.getInt(key, DEFAULT_INTEGER_VALUE);
    }

    public static boolean readBoolean(String key){
        return mSharedPreferences.getBoolean(key, DEFAULT_BOOLEAN_VALUE);
    }

    public static boolean writeString(String key, String value){
        mEditor.putString(key, value);
        return mEditor.commit();
    }

    public static boolean writeInt(String key, int value){
        mEditor.putInt(key, value);
        return mEditor.commit();
    }

    public static boolean writeBoolean(String key, boolean value){
        mEditor.putBoolean(key, value);
        return  mEditor.commit();
    }
}