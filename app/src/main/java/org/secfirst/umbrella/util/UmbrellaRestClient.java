package org.secfirst.umbrella.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.secfirst.umbrella.BuildConfig;
import org.secfirst.umbrella.R;
import org.thoughtcrime.ssl.pinning.PinningSSLSocketFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Locale;

public class UmbrellaRestClient {

    private static final String BASE_URL = "https://api.secfirst.org";
    private static final String VERSION = "v1";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static AsyncHttpClient getClientForApiUpdates(Context context) {
        String[] pins = new String[] {
                "4374d7082697887f810d337bf6a913b6cb5c8b57",
                "1212959a1fd001c80d152b1ee0410bbf90d1323e"
        };
        try {
            client.setSSLSocketFactory(new PinningSSLSocketFactory(context ,pins, 0));
            client.addHeader("Accept-Language", Locale.getDefault().toString());
        } catch (UnrecoverableKeyException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
        return client;
    }

    public static void get(String url, RequestParams params, String token, Context context, AsyncHttpResponseHandler responseHandler) {
        client = getClientForApiUpdates(context);
        if (isRequestReady(context, token)) client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getCustomFeed(String url, RequestParams params, Context context, AsyncHttpResponseHandler responseHandler) {
        client = new AsyncHttpClient();
        if (isRequestReady(context)) client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static boolean isRequestReady(Context context) {
        return isRequestReady(context, null);
    }

    private static boolean isRequestReady(Context context, String token) {
        boolean isReady = UmbrellaUtil.isNetworkAvailable(context);
        if (isReady) {
            if (token!=null) client.addHeader("token", token);
        } else {
            Toast.makeText(context, context.getString(R.string.no_network_message), Toast.LENGTH_LONG).show();
        }
        return isReady;
    }

    private static String getAbsoluteUrl(String url) {
        return BASE_URL + "/" + VERSION + "/"+url;
    }

}
