package com.mozilla.fennec.search.models.weather;

import android.text.format.Time;

import com.mozilla.fennec.search.models.units.Temperature;

public class WeatherForecast {
  private WeatherConditions condition;
  private Temperature highTemp;
  private Temperature lowTemp;
  private Time sunsetTime;
  private Time sunriseTime;

  // The day being forecasted (not the day the forecast was created).
  private Time forecastDate;

  public WeatherConditions getCondition() {
    return condition;
  }

  public Temperature getHighTemp() {
    return highTemp;
  }

  public Temperature getLowTemp() {
    return lowTemp;
  }

  public Time getSunriseTime() {
    return sunriseTime;
  }

  public Time getSunsetTime() {
    return sunsetTime;
  }

  public Time getForecastDate() {
    return forecastDate;
  }

  public WeatherForecast(WeatherConditions condition, Temperature highTemp, Temperature lowTemp, Time sunsetTime, Time sunriseTime, Time forecastDate) {
    this.condition = condition;
    this.highTemp = highTemp;
    this.lowTemp = lowTemp;
    this.sunsetTime = sunsetTime;
    this.sunriseTime = sunriseTime;
    this.forecastDate = forecastDate;
  }

  public static class WeatherForecastBuilder {
    private WeatherConditions condition;
    private Temperature highTemp;
    private Temperature lowTemp;
    private Time sunsetTime;
    private Time sunriseTime;
    private Time forecastDate;

    public WeatherForecastBuilder setCondition(WeatherConditions condition) {
      this.condition = condition;
      return this;
    }

    public WeatherForecastBuilder setHighTemp(Temperature highTemp) {
      this.highTemp = highTemp;
      return this;
    }

    public WeatherForecastBuilder setLowTemp(Temperature lowTemp) {
      this.lowTemp = lowTemp;
      return this;
    }

    public WeatherForecastBuilder setSunsetTime(Time sunsetTime) {
      this.sunsetTime = sunsetTime;
      return this;
    }

    public WeatherForecastBuilder setSunriseTime(Time sunriseTime) {
      this.sunriseTime = sunriseTime;
      return this;
    }

    public WeatherForecastBuilder setForecastDate(Time forecastDate) {
      this.forecastDate = forecastDate;
      return this;
    }

    public WeatherForecast createWeatherForecast() {
      return new WeatherForecast(condition, highTemp, lowTemp, sunsetTime, sunriseTime, forecastDate);
    }
  }
}

