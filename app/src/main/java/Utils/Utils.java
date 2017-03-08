package Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

import Exceptions.BitsoException;

/**
 * Created by vicco on 22/02/17.
 */

public class Utils {
    private static final String TAG = "Utils";
    private static KeyStore mKeyStore;

    public static String LEDGER_DATE_FORMAT = "hh:mm a";

    static{
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createNewKey(String alias, Context context) {
        if(mKeyStore != null){
            try {
                if (!mKeyStore.containsAlias(alias)) {
                    Calendar start = Calendar.getInstance();
                    Calendar end = Calendar.getInstance();
                    end.add(Calendar.YEAR, 1);
                    KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                            .setAlias(alias)
                            .setSubject(new X500Principal("CN=Bitso, O=Android Authority"))
                            .setSerialNumber(BigInteger.ONE)
                            .setStartDate(start.getTime())
                            .setEndDate(end.getTime())
                            .build();

                    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");

                    generator.initialize(spec);

                    KeyPair keyPair = generator.generateKeyPair();
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }else{
            keyStoreNotInitialized();
        }
    }

    public static String encryptString(String alias, String plainText){
        String encryptedString = null;
        if(mKeyStore != null) {
            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(alias, null);
                RSAPublicKey rsaPublicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

                // Encrypt text
                if((plainText == null) || plainText.trim().isEmpty()){
                    Log.e(TAG, "Plain text is empty");
                    return encryptedString;
                }

                Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding",
                        "AndroidOpenSSL");
                input.init(Cipher.ENCRYPT_MODE, rsaPublicKey);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                CipherOutputStream cipherOutputStream =
                        new CipherOutputStream(outputStream, input);
                cipherOutputStream.write(plainText.getBytes("UTF-8"));
                cipherOutputStream.close();

                byte [] encryptedOutput = outputStream.toByteArray();
                encryptedString = Base64.encodeToString(encryptedOutput, Base64.DEFAULT);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnrecoverableEntryException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            keyStoreNotInitialized();
        }
        return encryptedString;
    }

    public static String decryptString(String alias, String encryptedText){
        String decryptedString = null;
        if(mKeyStore != null){
            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(alias, null);
                //RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();

                Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

                CipherInputStream cipherInputStream = new CipherInputStream(
                        new ByteArrayInputStream(Base64.decode(encryptedText, Base64.DEFAULT)), output);
                ArrayList<Byte> values = new ArrayList<>();
                int nextByte;
                while ((nextByte = cipherInputStream.read()) != -1) {
                    values.add((byte)nextByte);
                }

                byte[] bytes = new byte[values.size()];
                for(int i = 0; i < bytes.length; i++) {
                    bytes[i] = values.get(i).byteValue();
                }

                decryptedString = new String(bytes, 0, bytes.length, "UTF-8");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnrecoverableEntryException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            keyStoreNotInitialized();
        }
        return decryptedString;
    }

    public static boolean isAliasInKeyStore(String alias){
        boolean isAliasContained = Boolean.FALSE;
        if(mKeyStore != null){
            try {
                isAliasContained = mKeyStore.containsAlias(alias);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        }else{
            keyStoreNotInitialized();
        }
        return isAliasContained;
    }

    public static boolean deleteKeyStoreKey(String alias){
        boolean aliasDeleted = Boolean.FALSE;
        if(mKeyStore != null){
            try {
                mKeyStore.deleteEntry(alias);
                aliasDeleted = !isAliasInKeyStore(alias);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        }else{
            keyStoreNotInitialized();
        }
        return aliasDeleted;
    }

    private static void keyStoreNotInitialized(){
        String message = "KeyStore not initialized";
        Log.e(TAG, "KeyStore not initialized");
        throw new BitsoException(message);
    }

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null);
    }

    public static String formatDate(Date date, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date).toString();
    }
}