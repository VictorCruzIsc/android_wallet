package connectivity;

import android.content.Context;

import com.bitso.Bitso;
import com.example.vicco.bitso.HomeActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import Utils.Utils;
import Utils.UtilsSharedPreferences;
import app.activities.CaptureActivity;

/**
 * Created by vicco on 31/01/17.
 */

public class HttpHandler {
    private static final String TAG = HttpHandler.class.getSimpleName();
    private static final String USER_AGENT = "Bitso-Android";
    private static final String CRYPTO_SPEC = "HmacSHA256";
    private static final String mBitso = "https://bitso.com";

    private static String mBitsoAPI = null;
    private static String mBitsoSecret = null;
    private static Boolean mInitialized = Boolean.FALSE;

    public static boolean initHttpHandler(Context context) {
        Bitso bitso = new Bitso("", "", 0, Boolean.TRUE, Boolean.TRUE);
        if(!mInitialized){
            UtilsSharedPreferences.initSharedPreferences(context);
            if(UtilsSharedPreferences.readBoolean(CaptureActivity.SP_SET_KEYS)){
                mBitsoAPI = UtilsSharedPreferences.readString(CaptureActivity.SP_API);
                mBitsoSecret = UtilsSharedPreferences.readString(CaptureActivity.SP_SECRET);
                if(!mBitsoAPI.equals(UtilsSharedPreferences.DEFAULT_STRING_VALUE) &&
                        !mBitsoSecret.equals(UtilsSharedPreferences.DEFAULT_STRING_VALUE)){
                    mBitsoAPI = Utils.decryptString(CaptureActivity.ALIAS_API, mBitsoAPI);
                    mBitsoSecret = Utils.decryptString(CaptureActivity.ALIAS_SECRET, mBitsoSecret);
                    mInitialized = Boolean.TRUE;
                }
            }
        }
        return mInitialized;
    }

    public static String makeServiceCall(String requestPath, String httpMethod,
                                  String requestParameters, boolean authentication){
        String requestURL = mBitso + requestPath;
        String response = null;
        try {
            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if(authentication){
                connection.addRequestProperty("Authorization",
                        getAuthenticationHeader(requestPath, httpMethod,
                                requestParameters));
                connection.setRequestProperty("User-Agent", USER_AGENT);
            }
            connection.setRequestMethod(httpMethod);
            if(connection.getResponseCode() == 200){
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                response = convertInputStreamToString(inputStream);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private static String convertInputStreamToString(InputStream inputStream){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try {
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    private static String getAuthenticationHeader(String requestPath,
                                           String httpMethod,
                                           String requestParameters) {
        long nonce = System.currentTimeMillis();
        byte[] secretBytes = mBitsoSecret.getBytes();
        byte[] arrayOfByte = null;
        String signature = null;
        BigInteger bigInteger = null;
        Mac mac =  null;

        String message = nonce + httpMethod + requestPath + requestParameters;
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretBytes, CRYPTO_SPEC);
        try {
            mac = Mac.getInstance(CRYPTO_SPEC);
            mac.init(secretKeySpec);
            arrayOfByte = mac.doFinal(message.getBytes());
            bigInteger = new BigInteger(1, arrayOfByte);
            signature = String.format("%0" + (arrayOfByte.length << 1) + "x",
                    new Object[]{bigInteger});
            return String.format("Bitso %s:%s:%s", mBitsoAPI, nonce, signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sendPost(String parameters) throws IOException {
        String apiTokenURL = "https://bitso.com/api/v2/redeem_api_token";
        URL url = new URL(apiTokenURL);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Android");

        // send Post Request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(parameters);
        wr.flush();
        wr.close();

        if(con.getResponseCode() == 200){
        int responseCode = con.getResponseCode();
            return convertInputStreamToString(con.getInputStream());
        }
        return null;
    }

    public static void setInitialized(Boolean status){
        mInitialized = status;
    }
}
