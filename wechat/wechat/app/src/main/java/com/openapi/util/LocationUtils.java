package com.openapi.util;

/**
 * Created by muskong on 2017/9/4.
 */

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.openapi.Constants;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

import static android.content.Context.LOCATION_SERVICE;
import static android.location.Criteria.ACCURACY_FINE;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;

/**
 * Utilities for dealing with the location service
 */
public class LocationUtils {

    private static Location latestLocation = null;
    /**
     * Get the location with the later date
     *
     * @param location1
     * @param location2
     * @return location
     */
    private static Location getLatest(final Location location1,
                                      final Location location2) {
        if (location1 == null)
            return location2;

        if (location2 == null)
            return location1;

        if (location2.getTime() > location1.getTime())
            return location2;
        else{
            Log.i(Constants.tag, "getLatest#######");
            return location1;
        }

    }

    /**
     * Get the latest location trying multiple providers
     * <p>
     * Calling this method requires that your application's manifest contains the
     * {@link android.Manifest.permission#ACCESS_FINE_LOCATION} permission
     *
     * @param context
     * @return latest location set or null if none
     */
    public static Location getLatestLocation(final Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setCostAllowed(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        String provider = manager.getBestProvider(criteria, true);
        Log.i(Constants.tag, "getLatestLocation####### " + provider);

        if (provider != null){
            latestLocation = manager.getLastKnownLocation(provider);
        }
        else{
            latestLocation = null;
            Log.i(Constants.tag, "getLatestLocation####### 1111");
        }

        if(latestLocation == null){
            LocationProvider gpsProvider = manager.getProvider(LocationManager.GPS_PROVIDER);//1.通过GPS定位，较精确。也比較耗电
            LocationProvider netProvider = manager.getProvider(LocationManager.NETWORK_PROVIDER);//2.通过网络定位。对定位精度度不高或省点情况可考虑使用
            if (netProvider != null) {
                manager.requestLocationUpdates(NETWORK_PROVIDER, 1000, 0, locationListener);
            }

            if (latestLocation == null && gpsProvider != null) {
                manager.requestLocationUpdates(GPS_PROVIDER, 1000, 0, locationListener);
            }
        }

//        if(latestLocation == null){
//            manager.requestLocationUpdates(GPS_PROVIDER, 1000, 0, locationListener);
//        }
//
//        if(latestLocation == null){
//            manager.requestLocationUpdates(GPS_PROVIDER, 1000, 0, locationListener);
//        }
//
//        if(latestLocation == null) {
//            manager.requestLocationUpdates(NETWORK_PROVIDER, 1000, 0, locationListener);
//        }
//
//        if(latestLocation == null) {
//            manager.requestLocationUpdates(PASSIVE_PROVIDER, 1000, 0, locationListener);
//        }

        return latestLocation;
    }

    static LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
            Log.e(Constants.tag, provider);
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
            latestLocation = null;
            Log.e(Constants.tag, provider);
        }

        // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            latestLocation = location;
            if (location != null) {
                Log.i(Constants.tag, "Location changed : Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
//                latitude = location.getLatitude(); // 经度
//                longitude = location.getLongitude(); // 纬度
//                time = location.getTime();
//                altitude = location.getAltitude();
            }
        }
    };

    /**
     * Get address for location
     *
     * @param context
     * @param location
     * @return possibly null address retrieved from location's latitude and
     * longitude
     */
    public static Address getAddress(final Context context,
                                     final Location location) {
        if (location == null)
            return null;

        final Geocoder geocoder = new Geocoder(context);
        final List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
        } catch (IOException e) {
            return null;
        }
        if (addresses != null && !addresses.isEmpty())
            return addresses.get(0);
        else
            return null;
    }

    //=============当前时区=============
    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return createGmtOffsetString(true, true, tz.getRawOffset());
    }

    private static String createGmtOffsetString(boolean includeGmt,
                                                boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }
}