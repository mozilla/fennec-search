package com.mozilla.fennec.search.models.types;

import java.io.Serializable;

public class Distance implements Serializable{
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
