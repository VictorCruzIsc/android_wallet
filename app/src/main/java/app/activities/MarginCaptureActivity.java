package app.activities;

import com.example.vicco.bitso.R;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * Created by vicco on 20/02/17.
 */

public class MarginCaptureActivity extends CaptureActivity {
    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.activity_zxing_margin_capure);
        return (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
    }
}
