package com.mozilla.fennec.search.models.restaurant;

import android.net.Uri;

import com.mozilla.fennec.search.models.units.Distance;

public class RestaurantRow {
  private Uri thumbnail;
  private Uri providerPage;
  private String snippet;
  private String name;
  private int numRatings;
  private double rating;
  private Distance distance;

  public RestaurantRow(Uri thumbnail, Uri providerPage, String snippet, String name, int numRatings, double rating, Distance distance) {
    this.thumbnail = thumbnail;
    this.providerPage = providerPage;
    this.snippet = snippet;
    this.name = name;
    this.numRatings = numRatings;
    this.rating = rating;
    this.distance = distance;
  }

  public Uri getThumbnail() {
    return thumbnail;
  }

  public Uri getProviderPage() {
    return providerPage;
  }

  public String getSnippet() {
    return snippet;
  }

  public String getName() {
    return name;
  }

  public int getNumRatings() {
    return numRatings;
  }

  public double getRating() {
    return rating;
  }

  public Distance getDistance() {
    return distance;
  }

  public static class RestaurantRowBuilder {
    private Uri thumbnail;
    private Uri providerPage;
    private String snippet;
    private String name;
    private int numRatings;
    private double rating;
    private Distance distance;

    public RestaurantRowBuilder setThumbnail(Uri thumbnail) {
      this.thumbnail = thumbnail;
      return this;
    }

    public RestaurantRowBuilder setProviderPage(Uri providerPage) {
      this.providerPage = providerPage;
      return this;
    }

    public RestaurantRowBuilder setSnippet(String snippet) {
      this.snippet = snippet;
      return this;
    }

    public RestaurantRowBuilder setName(String name) {
      this.name = name;
      return this;
    }

    public RestaurantRowBuilder setNumRatings(int numRatings) {
      this.numRatings = numRatings;
      return this;
    }

    public RestaurantRowBuilder setRating(double rating) {
      this.rating = rating;
      return this;
    }

    public RestaurantRowBuilder setDistance(Distance distance) {
      this.distance = distance;
      return this;
    }

    public RestaurantRow createRestaurantRow() {
      return new RestaurantRow(thumbnail, providerPage, snippet, name, numRatings, rating, distance);
    }
  }
}