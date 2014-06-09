package com.mozilla.fennec.search.models.units;

public class Address {
  private String street;
  private String city;
  private String state;
  private String country;
  private String zip;

  public Address(String street, String city, String state, String country, String zip) {
    this.street = street;
    this.city = city;
    this.state = state;
    this.country = country;
    this.zip = zip;
  }

  public String getStreet() {
    return street;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public String getCountry() {
    return country;
  }

  public String getZip() {
    return zip;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (street != null)
      sb.append(street).append('\n');
    if (city != null)
      sb.append(city);
      if (state != null)
      sb.append(", ");
    if (state != null)
      sb.append(state).append(' ');
    if (zip != null)
      sb.append(zip);
    return sb.toString();
  }

  public static class AddressBuilder {
    private String street;
    private String city;
    private String state;
    private String country;
    private String zip;

    public AddressBuilder setStreet(String street) {
      this.street = street;
      return this;
    }

    public AddressBuilder setCity(String city) {
      this.city = city;
      return this;
    }

    public AddressBuilder setState(String state) {
      this.state = state;
      return this;
    }

    public AddressBuilder setCountry(String country) {
      this.country = country;
      return this;
    }

    public AddressBuilder setZip(String zip) {
      this.zip = zip;
      return this;
    }

    public Address createAddress() {
      return new Address(street, city, state, country, zip);
    }
  }
}
