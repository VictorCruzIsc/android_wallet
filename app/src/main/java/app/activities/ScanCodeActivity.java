package app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vicco.bitso.MainActivity;
import com.example.vicco.bitso.R;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by vicco on 20/02/17.
 */

public class ScanCodeActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = ScanCodeActivity.class.getSimpleName();

    private Button iScanCredentialsBtn;
    private TextView iContinueToActivityTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

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
                Log.d(TAG, "Scan button pressed");
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setOrientationLocked(false);
                integrator.setCaptureActivity(SmallCaptureActivity.class);
                integrator.initiateScan();
                break;
            case R.id.continueToActivityTV:
                Log.d(TAG, "Call MainActivity");
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
