package app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vicco.bitso.HomeActivity;
import com.example.vicco.bitso.R;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Utils.Utils;
import Utils.UtilsSharedPreferences;
import connectivity.HttpHandler;

/**
 * Created by vicco on 20/02/17.
 */

public class CaptureActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String ALIAS_SECRET = "secret";
    public static final String ALIAS_API = "api";
    public static final String ALIAS_CLIENT = "client";
    public static final String SP_SECRET = ALIAS_SECRET;
    public static final String SP_API = ALIAS_API;
    public static final String SP_CLIENT = ALIAS_CLIENT;
    public static final String SP_SET_KEYS = "setKeys";

    private final String TAG = CaptureActivity.class.getSimpleName();

    private IntentIntegrator mIntegrator;
    private ProgressDialog mProgressDialog;
    private String mSecret;
    private String mId;
    private int mClient;

    private Button iScanCredentialsBtn;
    private TextView iContinueToActivityTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing_capture);

        // Members
        {
            mIntegrator = new IntentIntegrator(this);
            mIntegrator.setOrientationLocked(true);
            mIntegrator.setBeepEnabled(Boolean.FALSE);
            mIntegrator.setCaptureActivity(MarginCaptureActivity.class);
            mIntegrator.setPrompt(getResources().getString(R.string.place_qr));
        }

        // Interface elements
        {
            iScanCredentialsBtn = (Button) findViewById(R.id.scanCredentialsBtn);
            iContinueToActivityTV = (TextView) findViewById(R.id.continueToActivityTV);
        }

        // Interface and interactions
        {
            iScanCredentialsBtn.setOnClickListener(this);

            checkUserAlreadyLogged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.scanCredentialsBtn:
                mIntegrator.initiateScan();
                break;
            case R.id.continueToActivityTV:
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                String message =  getResources().getString(R.string.cancelled_scan);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            } else {
                String token =  result.getContents();
                int equalsIndex = token.indexOf('=');
                token = token.substring(equalsIndex + 1);
                new GetUserCredentials().execute(token);
            }
        } else {
            // If needed in a fragment activity
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkUserAlreadyLogged(){
        UtilsSharedPreferences.initSharedPreferences(this);
        if(UtilsSharedPreferences.readBoolean(SP_SET_KEYS)){
            String userId = UtilsSharedPreferences.readString(SP_CLIENT);
            userId = Utils.decryptString(ALIAS_CLIENT, userId);
            iContinueToActivityTV.setText("Continue as " + userId);
            iContinueToActivityTV.setOnClickListener(this);
        }else{
            iContinueToActivityTV.setVisibility(View.INVISIBLE);
        }
    }

    private class GetUserCredentials extends AsyncTask<String, Void, Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(CaptureActivity.this);
            mProgressDialog.setMessage("Login...");
            mProgressDialog.setCancelable(Boolean.FALSE);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            HttpHandler httpHandler = new HttpHandler();
            try {
                String parameters = "token=" + params[0] + "&appname=Android";
                String credentialsResponse = httpHandler.sendPost(parameters);
                return setUpCredentials(credentialsResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);

            // Dismiss progress dialog
            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }

            if(response){
                HttpHandler.setInitialized(Boolean.FALSE);
                Intent intent = new Intent(CaptureActivity.this, HomeActivity.class);
                startActivity(intent);
            }else{
                String message =  getResources().
                        getString(R.string.no_credencials_fetched);
                Toast.makeText(CaptureActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        private boolean setUpCredentials(String credentialsResponse){
            // Create keys in KeyStore
            Utils.createNewKey(ALIAS_SECRET, CaptureActivity.this);
            Utils.createNewKey(ALIAS_API, CaptureActivity.this);
            Utils.createNewKey(ALIAS_CLIENT, CaptureActivity.this);

            try {
                JSONObject jsonObject = new JSONObject(credentialsResponse);
                if(!jsonObject.has("error")) {
                    UtilsSharedPreferences.initSharedPreferences(CaptureActivity.this);
                    Boolean credentialsSetup = saveCredentials(jsonObject);
                    UtilsSharedPreferences.writeBoolean(SP_SET_KEYS, credentialsSetup);
                    return credentialsSetup;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        private boolean saveCredentials(JSONObject jsonObject){
            String secret = null;
            String encryptedSecret =  null;
            String api =  null;
            String encryptedAPI = null;
            String client =  null;
            String encryptedClient = null;
            try {
                secret = jsonObject.getString(ALIAS_SECRET);
                encryptedSecret = Utils.encryptString(ALIAS_SECRET, secret);
                secret = "";
                secret =  null;

                api = jsonObject.getString("id");
                encryptedAPI = Utils.encryptString(ALIAS_API, api);
                api = "";
                api = null;

                client = jsonObject.getString(ALIAS_CLIENT);
                encryptedClient = Utils.encryptString(ALIAS_CLIENT, client);
                client = "";
                client = null;

                // Save on shared preferences
                boolean savedSecret = UtilsSharedPreferences.writeString(SP_SECRET, encryptedSecret);
                boolean savedAPI = UtilsSharedPreferences.writeString(SP_API, encryptedAPI);
                boolean savedClient = UtilsSharedPreferences.writeString(SP_CLIENT, encryptedClient);

                Log.d(TAG, "SECRET: " + encryptedSecret + " API: " + encryptedAPI + " CLIENT: " + encryptedClient);

                return (savedSecret & savedAPI & savedClient);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}