package com.mozilla.fennec.search.models.types;

import java.io.Serializable;

public class Temperature implements Serializable {
  private double celsius;
  private static final double NINE_OVER_FIVE = (9.0d / 5.0d);
  private static final double FIVE_OVER_NINE = (5.0d / 9.0d);

  Temperature() {}

  public static Temperature fromCelsius(double degreesC) {
    Temperature instance = new Temperature();
    instance.celsius = degreesC;
    return instance;
  }

  public static Temperature fromFahrenheit(double degreesF) {
    return Temperature.fromCelsius(Temperature.fahrenheitToCelsius(degreesF));
  }

  public double getCelsius() {
    return celsius;
  }

  public String getCelsiusString() {
    return String.format("%.1f \u00B0C", celsius);
  }

  public double getFahrenheit() {
    return celsiusToFahrenheit(celsius);
  }

  public String getFahrenheitString() {
    return String.format("%.1f \u00B0F", getFahrenheit());
  }

  public static double fahrenheitToCelsius(double degreesF) {
    return (degreesF - 32.0f) * FIVE_OVER_NINE;
  }

  public static double celsiusToFahrenheit(double degreesC) {
    return degreesC * NINE_OVER_FIVE + 32.0f;
  }

  @Override
  public String toString() {
    return
        "celsius: " + celsius + '\n' +
        "fahrenheit: " + getFahrenheit();

  }
}
