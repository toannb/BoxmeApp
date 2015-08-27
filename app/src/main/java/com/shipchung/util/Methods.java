package com.shipchung.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shipchung.config.Constants;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/3/2015.
 */
public class Methods {

    public static boolean checkInternet(Context pContext) {
        ConnectivityManager cm = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting() && netInfo.isConnected() && netInfo.isAvailable());
    }

    public static void fullScreen(Activity pActivity) {
        pActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String plainText) throws Exception {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    private static Cipher getCipher(int cipherMode) throws Exception {
        String encryptionAlgorithm = "AES/ECB/PKCS5Padding";
        SecretKeySpec keySpecification = setKey(Constants.KEYCODE);
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        cipher.init(cipherMode, keySpecification);
        return cipher;
    }

    public static String decrypt(String strToDecrypt) {
        String decryptedString = "";
        try {
            SecretKeySpec secretKey = setKey(Constants.KEYCODE);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            decryptedString = new String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return decryptedString;
    }

    public static SecretKeySpec setKey(String myKey) {
        MessageDigest sha = null;
        SecretKeySpec secretKey = null;
        try {
            byte[] key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            System.out.println(key.length);
            System.out.println(new String(key, "UTF-8"));
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return secretKey;
    }

    public static String getAdvertisementID(Context pContext) {
        final TelephonyManager tm = (TelephonyManager) pContext.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(pContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return "and@" + deviceId;
    }

    public static void alertNotify(Context context, String content, int color) {
        Activity activity = (Activity) context;
        //Creating the LayoutInflater instance
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Getting the View object as defined in the customtoast.xml file
        View layout = li.inflate(R.layout.custom_toast,
                (ViewGroup) activity.findViewById(R.id.custom_toast_layout));
        LinearLayout linearLayout = (LinearLayout) layout.findViewById(R.id.custom_toast_layout);
        linearLayout.setBackgroundColor(color);
        TextView txtContent = (TextView) layout.findViewById(R.id.content);
        txtContent.setText(content);
        //Creating the Toast object
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setView(layout);//setting the view of custom toast layout
        for (int i = 0; i < 2; i++) {
            toast.show();
        }
    }

    public static void successNotify(Context context, String content) {
        int color = context.getResources().getColor(R.color.success_color);
        Methods.alertNotify(context, content, color);
    }

    public static void checkError(Context context, int statusCode) {
        String content = "";
        int color = context.getResources().getColor(R.color.error_color);
        switch (statusCode) {
            case 0:
                content = context.getResources().getString(R.string.error_0);
                break;
            case 401:
                content = context.getResources().getString(R.string.error_401);
                break;
            case 403:
                content = context.getResources().getString(R.string.error_403);
                break;
            case 404:
                content = context.getResources().getString(R.string.error_404);
                break;
            case 422:
                content = context.getResources().getString(R.string.error_422);
                break;
            case 502:
                content = context.getResources().getString(R.string.error_502);
                break;
        }
        alertNotify(context, content, color);
    }
}
