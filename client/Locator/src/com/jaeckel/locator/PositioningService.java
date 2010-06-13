package com.jaeckel.locator;

import android.app.Service;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Binder;
import android.os.AsyncTask;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationProvider;
import android.location.LocationListener;
import android.widget.Toast;
import android.preference.PreferenceManager;
import com.google.inject.Inject;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;

import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;

/**
 * User: biafra
 * Date: Jun 5, 2010
 * Time: 9:23:42 PM
 */
public class PositioningService extends Service {

    //    @Inject
    private LocationManager loc;
    private KeyBasedProcessor kbfp = new KeyBasedProcessor();

    public final static String BROADCAST_ACTION = "com.jaeckel.locator.LocationUpdateEvent";
    private Intent broadcast = new Intent(BROADCAST_ACTION);

    private boolean use_gps_positioning = true;
    private boolean gps_positioning_enabled = true;
    private boolean use_network_positioning = true;
    private boolean network_positioning_enabled = true;
    private HttpClient httpclient = new DefaultHttpClient();

    private SharedPreferences prefs;

    private final Binder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        PositioningService getService() {
            return (PositioningService.this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    // find our location and Toast it

    @Override
    public void onCreate() {

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(prefsListener);
        loc = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean enabledOnly = false;
        List<String> providers = loc.getProviders(enabledOnly);

        setupLocationUpdates();

    }

    @Override
    public void onDestroy() {

        prefs.unregisterOnSharedPreferenceChangeListener(prefsListener);
    }

    private void setupLocationUpdates() {

        loc.removeUpdates(listener);

        if (prefs.getBoolean("auto_update", false)) {


            float meters = Integer.valueOf(prefs.getString("update_meters", "50"));
            long millis = Integer.valueOf(prefs.getString("update_interval", "300")) * 1000;

            Toast.makeText(this, "Doing auto updates every " + meters + "m or every " + millis + "ms ", Toast.LENGTH_SHORT).show();


            if (loc.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, millis, meters, listener);

            } else {

                loc.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, millis, meters, listener);
            }

        }
    }

    private LocationListener listener = new LocationListener() {

        public void onLocationChanged(final Location location) {
            System.out.println("----< onLocationChanged: " + location);

            broadcast.putExtra("location", location);
            sendBroadcast(broadcast);

            new EncryptAndSendLocationTask().execute(location);
//            sendLocation(location);
        }


        public void onStatusChanged(String s, int i, Bundle bundle) {

            System.out.println("----< onStatusChanged: " + s);
            String statusName;

            if (i == LocationProvider.AVAILABLE) {
                statusName = "AVAILABLE";

                if (s.equals(LocationManager.GPS_PROVIDER)) {
                    use_gps_positioning = true;
                }
                if (!use_gps_positioning && s.equals(LocationManager.NETWORK_PROVIDER)) {

                    use_network_positioning = true;
                }

            } else if (i == LocationProvider.OUT_OF_SERVICE) {
                statusName = "OUT_OF_SERVICE";

                if (s.equals(LocationManager.GPS_PROVIDER)) {
                    use_gps_positioning = false;
                    use_network_positioning = true;

                }
                if (s.equals(LocationManager.NETWORK_PROVIDER)) {
                    use_network_positioning = false;
                }

            } else if (i == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                statusName = "TEMPORARILY_UNAVAILABLE";

                if (s.equals(LocationManager.GPS_PROVIDER)) {
                    use_gps_positioning = false;
                    use_network_positioning = true;
                }
                if (s.equals(LocationManager.NETWORK_PROVIDER)) {
                    use_network_positioning = false;
                }

            } else {

                statusName = "UNKNOWN: " + i;
            }


//            Toast.makeText(PositioningService.this, "PS: onStatusChanged: " + s + " -> " + statusName, Toast.LENGTH_LONG).show();
//            Toast.makeText(PositioningService.this, "PS: use_gps_positioning: " + use_gps_positioning, Toast.LENGTH_SHORT).show();
//            Toast.makeText(PositioningService.this, "PS: use_network_positioning: " + use_network_positioning, Toast.LENGTH_SHORT).show();

        }

        public void onProviderEnabled(String s) {
            System.out.println("----< onProviderEnabled: " + s);
//            Toast.makeText(PositioningService.this, "PS: onProviderEnabled: " + s, Toast.LENGTH_LONG).show();
            if (s.equals(LocationManager.GPS_PROVIDER)) {
                gps_positioning_enabled = true;

            } else if (s.equals(LocationManager.NETWORK_PROVIDER)) {
                network_positioning_enabled = true;

            }
        }

        public void onProviderDisabled(String s) {
            System.out.println("----< onProviderDisabled: " + s);
//            Toast.makeText(PositioningService.this, "PS: onProviderDisabled: " + s, Toast.LENGTH_LONG).show();

            if (s.equals(LocationManager.GPS_PROVIDER)) {
                gps_positioning_enabled = false;

            } else if (s.equals(LocationManager.NETWORK_PROVIDER)) {
                network_positioning_enabled = false;

            }
        }
    };

    private void sendLocation(final Location location) {


//        String encryptedString = kbfp.encrypt("" + location.getLatitude() +"/" + location.getLongitude() + " " + location.getAccuracy()  );
        String encryptedString = kbfp.encrypt(location.toString());

        System.out.println("----< sendLocation: " + location);
        System.out.println("----< encryptedString: " + encryptedString);
        final String encodedString = URLEncoder.encode(encryptedString);
        System.out.println("----< encodedString: " + encodedString);
        System.out.println("----< keyId: " + kbfp.getKeyId());

        // Create a new HttpClient and Post Header
        HttpPost httppost = new HttpPost("http://androidlocatorservice.appspot.com/position/save");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("keyid", "" + kbfp.getKeyId()));


            nameValuePairs.add(new BasicNameValuePair("position", encodedString));
//            nameValuePairs.add(new BasicNameValuePair("keybitcount", 1024));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

//            Toast.makeText(PositioningService.this, "PS: Sent Location: " + response.getStatusLine(), Toast.LENGTH_SHORT).show();
//            Toast.makeText(PositioningService.this, "PS: response: " + response.getEntity().getContent(), Toast.LENGTH_LONG).show();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    class EncryptAndSendLocationTask extends AsyncTask<Location, String, Void> {

        @Override
        protected Void doInBackground(final Location... locations) {

//            Toast.makeText(PositioningService.this, "doInBackground Sent Location...", Toast.LENGTH_SHORT).show();

            for (Location location : locations) {
                sendLocation(location);

            }

            return null;
        }
    }

    private SharedPreferences.OnSharedPreferenceChangeListener prefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

            Toast.makeText(PositioningService.this, "PS: prefs changed: " + s, Toast.LENGTH_SHORT).show();

            if ("auto_update".equals(s) || "update_meters".equals(s) || "update_interval".equals(s)) {

                setupLocationUpdates();
            }
        }
    };

}
