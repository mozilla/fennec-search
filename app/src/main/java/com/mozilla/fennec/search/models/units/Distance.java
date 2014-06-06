package com.mozilla.fennec.search.models.units;

public class Distance {
  private double meters;
  public static Distance fromMeters(double meters) {
    Distance instance = new Distance();
    instance.meters = meters;
    return instance;
  }

  public String getKiloMeterString() {

    return String.format("%.1f km", meters / 1000);
  }
}
