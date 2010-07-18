package com.jaeckel.locator.user;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import android.location.Location;

import java.net.URLEncoder;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;

/**
 * User: biafra
 * Date: Jun 16, 2010
 * Time: 11:10:22 PM
 */
public class AccountManager {

    private static HttpClient httpclient = new DefaultHttpClient();


    public static String createAccount(Account account) {
        //https://androidlocatorservice.appspot.com/user/create?name=foo23&pubKeyId=bar&password=fnord&pubKey=sldkjhfkljsdhfksdkjfhskjdfhjsdkfhjdsf
        String result;

        final String encodedPubKey = URLEncoder.encode(account.getPubKey());
        System.out.println("----< encodedPubKey: " + encodedPubKey);

        // Create a new HttpClient and Post Header
        HttpPost httppost = new HttpPost("https://androidlocatorservice.appspot.com/user/create");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("name", "" + account.getName()));
            nameValuePairs.add(new BasicNameValuePair("pubKeyId", account.getPubKeyId()));
            nameValuePairs.add(new BasicNameValuePair("pubKey", encodedPubKey));
            nameValuePairs.add(new BasicNameValuePair("password", account.getPassword()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            String responseString = inputStreamToString(response.getEntity().getContent());
            return "Status: " + response.getStatusLine() + " response: " + responseString;


        } catch (IllegalStateException e) {
            e.printStackTrace();
            result = "Exception: " + e.getMessage();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            result = "Exception: " + e.getMessage();

        } catch (IOException e) {
            e.printStackTrace();
            result = "Exception: " + e.getMessage();

        }


        return result;
    }

    private static String inputStreamToString(InputStream in) throws IOException {
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }

        bufferedReader.close();
        return stringBuilder.toString();
    }
}
