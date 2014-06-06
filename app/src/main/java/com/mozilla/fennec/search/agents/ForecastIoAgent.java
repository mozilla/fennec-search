package com.mozilla.fennec.search.agents;

import android.app.Activity;
import android.text.format.Time;

import com.mozilla.fennec.search.models.units.Temperature;
import com.mozilla.fennec.search.models.weather.WeatherConditions;
import com.mozilla.fennec.search.models.weather.WeatherForecast;
import com.mozilla.fennec.search.models.weather.WeatherModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ForecastIoAgent extends JsonAgent {

  private final static Logger LOGGER = Logger.getLogger(ForecastIoAgent.class.getName());

  private final static HashMap<String, WeatherConditions> conditionLookup;

  static {
    conditionLookup = new HashMap<String, WeatherConditions>();
    conditionLookup.put("clear-day", WeatherConditions.CLEAR);
    conditionLookup.put("clear-night", WeatherConditions.CLEAR);
    conditionLookup.put("rain", WeatherConditions.RAIN);
    conditionLookup.put("snow", WeatherConditions.SNOW);
    conditionLookup.put("sleet", WeatherConditions.SLEET);
    conditionLookup.put("wind", WeatherConditions.WIND);
    conditionLookup.put("fog", WeatherConditions.FOG);
    conditionLookup.put("cloudy", WeatherConditions.CLOUD);
    conditionLookup.put("partly-cloudy-day", WeatherConditions.PART_CLOUD);
    conditionLookup.put("partly-cloudy-night", WeatherConditions.PART_CLOUD);
    conditionLookup.put("hail", WeatherConditions.HAIL);
    conditionLookup.put("thunderstorm", WeatherConditions.THUNDERSTORM);
    conditionLookup.put("tornado", WeatherConditions.TORNADO);
  }

  public ForecastIoAgent(Activity activity) {
    super(activity);
  }

  @Override
  protected String fetchJson(Query query) {
    String url =
        String.format("https://api.forecast.io/forecast/28a2e15ab413e47a7bad54c04763813f/%f,%f?units=si",
            query.getmLatitude(), query.getmLongitude());
//    Log.i("url", url);
//    Log.i("mockweather", sink.getActivity().getString(R.string.mock_weather));
//    return sink.getActivity().getString(R.string.mock_weather);
    try {

      return fetchHttp(url);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }

  private WeatherModel initWeatherObject(JSONObject apiResponse) {
    WeatherModel model = null;
    try {
      JSONObject currentStatus = apiResponse.getJSONObject("currently");
      Temperature currentTemp = Temperature.fromCelsius(currentStatus.getDouble("temperature"));
      WeatherConditions currentCondition = conditionLookup.get(currentStatus.getString("icon"));
      model = new WeatherModel(currentTemp, currentCondition);

    } catch (JSONException e) {
      LOGGER.log(Level.SEVERE, "jsonMemberError", e);
    }
    return model;
  }

  private void pushForecasts(WeatherModel model, JSONObject apiResponse) {
    try {
      JSONArray forecasts = apiResponse.getJSONObject("daily").getJSONArray("data");
      JSONObject jsForecast;
      for (int i = 0; i < forecasts.length(); i++) {
        WeatherForecast.WeatherForecastBuilder builder = new WeatherForecast.WeatherForecastBuilder();

        Time sunrise = new Time();
        Time sunset = new Time();
        Time forecastDate = new Time();

        jsForecast = forecasts.getJSONObject(i);

        forecastDate.set(jsForecast.getLong("time") * 1000);
        sunrise.set(jsForecast.getLong("sunriseTime") * 1000);
        sunset.set(jsForecast.getLong("sunsetTime") * 1000);

        builder.setCondition(conditionLookup.get(jsForecast.getString("icon")))
            .setForecastDate(forecastDate)
            .setSunriseTime(sunrise)
            .setSunsetTime(sunset)
            .setHighTemp(Temperature.fromCelsius(jsForecast.getLong("temperatureMax")))
            .setLowTemp(Temperature.fromCelsius(jsForecast.getLong("temperatureMin")));

        model.pushForecast(builder.createWeatherForecast());
      }
    } catch (JSONException e) {
      LOGGER.log(Level.SEVERE, "jsonMemberError", e);
    }

  }

  @Override
  protected WeatherModel createCardModel(JsonResponse response) {
    JSONObject apiResponse = response.getResponse();
    WeatherModel model = initWeatherObject(apiResponse);
    pushForecasts(model, apiResponse);
    return model;
  }
}
