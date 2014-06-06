package com.mozilla.fennec.search.models.weather;

import android.text.format.Time;
import android.util.Log;

import com.mozilla.fennec.search.models.TitleCardModel;
import com.mozilla.fennec.search.models.units.Temperature;

import java.util.ArrayList;
import java.util.List;

public class WeatherModel extends TitleCardModel {
  private Temperature currentTemperature;
  private WeatherConditions currentCondition;
  private List<WeatherForecast> forecast;

  public WeatherModel(Temperature currentTemperature, WeatherConditions currentCondition) {
    super("Weather");
    this.currentTemperature = currentTemperature;
    this.currentCondition = currentCondition;
    this.forecast = new ArrayList<WeatherForecast>();
  }

  public List<WeatherForecast> getForecast() {
    return forecast;
  }

  public void pushForecast(WeatherForecast forecast) {
    this.forecast.add(forecast);
  }

  public Temperature getCurrentTemperature() {
    return currentTemperature;
  }

  public WeatherForecast getTodaysForecast() {
    return forecast.get(0);
  }

  public Boolean isDaylight() {
    Time now = new Time();
    now.setToNow();
    Time sunriseTime = getTodaysForecast().getSunriseTime();
    Time sunsetTime = getTodaysForecast().getSunsetTime();
    Log.i("sunrise", sunriseTime.toString());
    Log.i("sunset", sunsetTime.toString());
    return now.after(sunriseTime) && now.before(sunsetTime);
  }

  public WeatherConditions getCondition() {
    return currentCondition;
  }
}
