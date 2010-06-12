package com.jaeckel.locator;

import android.app.Service;
import android.os.IBinder;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationProvider;
import android.location.LocationListener;
import android.widget.Toast;
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

    private boolean use_gps_positioning = true;
    private boolean gps_positioning_enabled = true;
    private boolean use_network_positioning = true;
    private boolean network_positioning_enabled = true;
    private HttpClient httpclient = new DefaultHttpClient();

    @Override
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    // find our location and Toast it

    @Override
    public void onCreate() {


        loc = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean enabledOnly = false;
        List<String> providers = loc.getProviders(enabledOnly);

        for (String pName : providers) {


            if (loc.isProviderEnabled(pName)) {
                // LocationProvider provider = loc.getProvider(pName);

                Location location = loc.getLastKnownLocation(pName);
                if (location != null) {
//                    Toast.makeText(this, "Provider " + pName + ": " + location.getLatitude() + "/" + location.getLongitude() +
//                            "{" + location.getAccuracy() + "m}", Toast.LENGTH_LONG).show();

                    sendLocation(location);

                } else {
                    Toast.makeText(this, "No LastKnownLocation for Provider: " + pName + " available", Toast.LENGTH_LONG).show();

                }


            } else {
                Toast.makeText(this, "LocationProvider: " + pName + " not enabled", Toast.LENGTH_SHORT).show();
            }

        }

        float meters = 15;
        long millis = 5 * 60000; // five minutes
//        long millis = 10000; // ten seconds for testing


        loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, millis, meters, listener);
        loc.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, millis, meters, listener);


//        Location location = loc.getLastKnownLocation("gps");
//        if (location != null) {
//            Toast.makeText(this, location.getLatitude() + "/" + location.getLongitude(), Toast.LENGTH_LONG).show();
//
//        } else {
//            Toast.makeText(this, "No LastKnownLocation available", Toast.LENGTH_LONG).show();
//
//        }
    }

    LocationListener listener = new LocationListener() {

        public void onLocationChanged(final Location location) {
            System.out.println("----< onLocationChanged: " + location);

//            Toast.makeText(PositioningService.this, "PS: onLocationChanged: " + location, Toast.LENGTH_LONG).show();

//            http://androidlocatorservice.appspot.com/position/save?keyid=bbbbbbb&position=fnord&keybitcount=1026

            sendLocation(location);
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
        String encryptedString = kbfp.encrypt( location.toString()  );

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


        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
