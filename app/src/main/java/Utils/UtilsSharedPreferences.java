package Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import app.activities.CaptureActivity;

/**
 * Created by vicco on 23/02/17.
 */

public class UtilsSharedPreferences {
    public static final String DEFAULT_STRING_VALUE = "noValue";
    public static final Integer DEFAULT_INTEGER_VALUE = -1;
    public static final Boolean DEFAULT_BOOLEAN_VALUE = Boolean.FALSE;

    private static final String SHAREDPREFERENCES_FILE = "com.src.sharedpreferences";
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    public static String readString(String key, Context context){
        if(mSharedPreferences == null){
            initSharedPreferences(context);
        }
        return mSharedPreferences.getString(key, DEFAULT_STRING_VALUE);
    }

    public static int readInt(String key, Context context){
        if (mSharedPreferences == null) {
            initSharedPreferences(context);
        }
        return mSharedPreferences.getInt(key, DEFAULT_INTEGER_VALUE);
    }

    public static boolean readBoolean(String key, Context context){
        if (mSharedPreferences == null) {
            initSharedPreferences(context);
        }
        return mSharedPreferences.getBoolean(key, DEFAULT_BOOLEAN_VALUE);
    }

    public static boolean writeString(String key, String value, Context context){
        if(mEditor == null){
            initEditor(context);
        }
        mEditor.putString(key, value);
        return mEditor.commit();
    }

    public static boolean writeInt(String key, int value, Context context){
        if(mEditor == null){
            initEditor(context);
        }
        mEditor.putInt(key, value);
        return mEditor.commit();
    }

    public static boolean writeBoolean(String key, boolean value, Context context){
        if(mEditor == null){
            initEditor(context);
        }
        mEditor.putBoolean(key, value);
        return  mEditor.commit();
    }

    private static void initSharedPreferences(Context context){
        mSharedPreferences = context.getSharedPreferences(SHAREDPREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    private static void initEditor(Context context){
        if(mSharedPreferences == null){
            initSharedPreferences(context);
        }
        mEditor = mSharedPreferences.edit();
    }

    public static boolean saveUserCredentials(String credentials, Context context){
        String secret = null;
        String encryptedSecret =  null;
        String api =  null;
        String encryptedAPI = null;
        String client =  null;
        String encryptedClient = null;

        try {
            JSONObject jsonObject = new JSONObject(credentials);
            if(!jsonObject.has("error")) {
                Utils.createNewKey(CaptureActivity.ALIAS_SECRET, context);
                Utils.createNewKey(CaptureActivity.ALIAS_API, context);
                Utils.createNewKey(CaptureActivity.ALIAS_CLIENT, context);

                secret = jsonObject.getString(CaptureActivity.ALIAS_SECRET);
                encryptedSecret = Utils.encryptString(
                        CaptureActivity.ALIAS_SECRET, secret);
                secret = "";
                secret =  null;

                api = jsonObject.getString("id");
                encryptedAPI = Utils.encryptString(CaptureActivity.ALIAS_API, api);
                api = "";
                api = null;

                client = jsonObject.getString(CaptureActivity.ALIAS_CLIENT);
                encryptedClient = Utils.encryptString(
                        CaptureActivity.ALIAS_CLIENT, client);
                client = "";
                client = null;

                // Save on shared preferences
                boolean savedSecret = UtilsSharedPreferences.
                        writeString(CaptureActivity.SP_SECRET, encryptedSecret, context);
                boolean savedAPI = UtilsSharedPreferences.
                        writeString(CaptureActivity.SP_API, encryptedAPI, context);
                boolean savedClient = UtilsSharedPreferences.
                        writeString(CaptureActivity.SP_CLIENT, encryptedClient, context);

                boolean credentialsSaved = (savedSecret & savedAPI & savedClient);

                UtilsSharedPreferences.writeBoolean(
                        CaptureActivity.SP_SET_KEYS, credentialsSaved, context);

                return credentialsSaved;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }
}