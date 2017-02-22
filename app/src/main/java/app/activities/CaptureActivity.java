package app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

import connectivity.HttpHandler;

/**
 * Created by vicco on 20/02/17.
 */

public class CaptureActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String CREDENTIALS = "Credentials";
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
            iContinueToActivityTV.setOnClickListener(this);
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

    private class GetUserCredentials extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(CaptureActivity.this);
            mProgressDialog.setMessage("Login...");
            mProgressDialog.setCancelable(Boolean.FALSE);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpHandler httpHandler = new HttpHandler();
            try {
                String parameters = "token=" + params[0] + "&appname=Android";
                return httpHandler.sendPost(parameters);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            // Dismiss progress dialog
            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }

            if(response != null){
                try {
                    JSONObject json = new JSONObject(response);
                    if(json.has("error")){
                        JSONObject jsonError = json.getJSONObject("error");
                        String message =  jsonError.getString("message");
                        Toast.makeText(CaptureActivity.this, message, Toast.LENGTH_LONG).show();
                    }else{
                        Intent intent = new Intent(CaptureActivity.this, HomeActivity.class);
                        intent.putExtra(CREDENTIALS, response);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                String message =  getResources().
                        getString(R.string.no_credencials_fetched);
                Toast.makeText(CaptureActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}