package com.mozilla.fennec.search.agents;

import android.location.Location;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Query {
  private String mQueryString;
  private double mLatitude;
  private double mLongitude;

  public Query(String query, double latitude, double longitude) {
    this(query);
    mLatitude = latitude;
    mLongitude = longitude;
  }

  public Query(String query, Location location) {
    this(query, location.getLatitude(), location.getLongitude());
  }

  public Query(Location location) {
    this("", location);
  }

  public Query(String query) {
    try {
      mQueryString = URLEncoder.encode(query, "utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      mQueryString = "";
    }
  }

  public String getQueryString() {
    return mQueryString;
  }

  public double getLatitude() {
    return mLatitude;
  }

  public double getLongitude() {
    return mLongitude;
  }
}
