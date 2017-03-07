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

import com.crashlytics.android.Crashlytics;
import com.example.vicco.bitso.HomeActivity;
import com.example.vicco.bitso.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import io.fabric.sdk.android.Fabric;
import org.json.JSONException;
import org.json.JSONObject;
import Utils.Utils;
import Utils.UtilsSharedPreferences;
import connectivity.HttpHandler;


/**
 * Created by vicco on 20/02/17.
 */

public class CaptureActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = CaptureActivity.class.getSimpleName();

    public static final String ALIAS_SECRET = "secret";
    public static final String ALIAS_API = "api";
    public static final String ALIAS_CLIENT = "client";
    public static final String SP_SET_KEYS = "setKeys";
    public static final String SP_SECRET = ALIAS_SECRET;
    public static final String SP_API = ALIAS_API;
    public static final String SP_CLIENT = ALIAS_CLIENT;

    private IntentIntegrator mIntegrator;

    private Button iScanCredentialsBtn;
    private TextView iContinueToActivityTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
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

                if(!Utils.isNetworkAvailable(this)){
                    Toast.makeText(this, getResources().getString(
                            R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!Utils.isNetworkAvailable(this)){
            Toast.makeText(this, getResources().getString(
                    R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

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
        }
    }

    private void checkUserAlreadyLogged(){
        if(UtilsSharedPreferences.readBoolean(SP_SET_KEYS, this)){
            String userId = UtilsSharedPreferences.readString(SP_CLIENT, this);
            userId = Utils.decryptString(ALIAS_CLIENT, userId);
            iContinueToActivityTV.setText(getResources().getString(R.string.main_continue) + " " + userId);
            iContinueToActivityTV.setOnClickListener(this);
        }else{
            iContinueToActivityTV.setText(getResources().getString(R.string.qr_link));
        }
    }

    private boolean asyncMethodProcessCredentials(String... params){
        String parameters = "token=" + params[0] + "&appname=Android";
        String credentialsResponse = HttpHandler.sendPost(parameters);

        Log.d(TAG, "Credentials response: " + credentialsResponse);

        return UtilsSharedPreferences.saveUserCredentials(credentialsResponse, this);
    }

    private class GetUserCredentials extends AsyncTask<String, Void, Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return asyncMethodProcessCredentials(params);
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
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
    }
}