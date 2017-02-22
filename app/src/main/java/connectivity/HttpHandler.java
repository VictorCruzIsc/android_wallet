package connectivity;

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

/**
 * Created by vicco on 31/01/17.
 */

public class HttpHandler {
    private final String TAG = HttpHandler.class.getSimpleName();
    private final String USER_AGENT = "Bitso-Android";
    private final String CRYPTO_SPEC = "HmacSHA256";

    private String mBitsoKey;
    private String mBitsoSecret;
    private String mBitsoDev;

    public HttpHandler() {
        mBitsoKey = "OzFHrbosZD";
        mBitsoSecret = "dc14d07a6856d5a4d035d5703f86b66b";
        mBitsoDev = "https://dev.bitso.com";
    }

    public HttpHandler(String mBitsoKey, String mBitsoSecret, String mBitsoDev) {
        this.mBitsoKey = mBitsoKey;
        this.mBitsoSecret = mBitsoSecret;
        this.mBitsoDev = mBitsoDev;
    }

    public String makeServiceCall(String requestPath, String httpMethod,
                                  String requestParameters, boolean authentication){
        String requestURL = mBitsoDev + requestPath;
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

    private String getAuthenticationHeader(String requestPath,
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
            return String.format("Bitso %s:%s:%s", mBitsoKey, nonce, signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendPost(String parameters) throws IOException {
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
}
