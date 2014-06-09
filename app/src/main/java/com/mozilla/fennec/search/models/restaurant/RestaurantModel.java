package com.mozilla.fennec.search.models.restaurant;

import com.mozilla.fennec.search.models.units.Address;
import com.mozilla.fennec.search.models.units.Distance;
import com.mozilla.fennec.search.models.units.URI;

import java.io.Serializable;

public class RestaurantModel implements Serializable {
  private URI thumbnailImage;
  private URI image;
  private URI ratingImage;
  private URI providerPage;
  private String snippet;
  private String name;
  private int numRatings;
  private double rating;
  private Distance distance;
  private String yelpId;
  private Address address;
  private String phoneNumber;

  public RestaurantModel(URI thumbnailImage, URI image, URI ratingImage, URI providerPage, String snippet, String name, int numRatings, double rating, Distance distance, String yelpId, Address address, String phoneNumber) {
    this.thumbnailImage = thumbnailImage;
    this.image = image;
    this.ratingImage = ratingImage;
    this.providerPage = providerPage;
    this.snippet = snippet;
    this.name = name;
    this.numRatings = numRatings;
    this.rating = rating;
    this.distance = distance;
    this.yelpId = yelpId;
    this.address = address;
    this.phoneNumber = phoneNumber;
  }

  public URI getThumbnailImage() {
    return thumbnailImage;
  }

  public URI getImage() {
    return image;
  }

  public URI getRatingImage() {
    return ratingImage;
  }

  public URI getProviderPage() {
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

  public String getYelpId() {
    return yelpId;
  }

  public Address getAddress() {
    return address;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public static class RestaurantModelBuilder {
    private URI thumbnailImage;
    private URI image;
    private URI ratingImage;
    private URI providerPage;
    private String snippet;
    private String name;
    private int numRatings;
    private double rating;
    private Distance distance;
    private String yelpId;
    private Address address;
    private String phoneNumber;

    public RestaurantModelBuilder setThumbnailImage(URI thumbnailImage) {
      this.thumbnailImage = thumbnailImage;
      return this;
    }

    public RestaurantModelBuilder setImage(URI image) {
      this.image = image;
      return this;
    }

    public RestaurantModelBuilder setRatingImage(URI ratingImage) {
      this.ratingImage = ratingImage;
      return this;
    }

    public RestaurantModelBuilder setProviderPage(URI providerPage) {
      this.providerPage = providerPage;
      return this;
    }

    public RestaurantModelBuilder setSnippet(String snippet) {
      this.snippet = snippet;
      return this;
    }

    public RestaurantModelBuilder setName(String name) {
      this.name = name;
      return this;
    }

    public RestaurantModelBuilder setNumRatings(int numRatings) {
      this.numRatings = numRatings;
      return this;
    }

    public RestaurantModelBuilder setRating(double rating) {
      this.rating = rating;
      return this;
    }

    public RestaurantModelBuilder setDistance(Distance distance) {
      this.distance = distance;
      return this;
    }

    public RestaurantModelBuilder setYelpId(String yelpId) {
      this.yelpId = yelpId;
      return this;
    }

    public RestaurantModelBuilder setAddress(Address address) {
      this.address = address;
      return this;
    }

    public RestaurantModelBuilder setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    public RestaurantModel createRestaurantModel() {
      return new RestaurantModel(thumbnailImage, image, ratingImage, providerPage, snippet, name, numRatings, rating, distance, yelpId, address, phoneNumber);
    }
  }
}