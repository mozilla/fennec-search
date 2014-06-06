package com.mozilla.fennec.search.cards;

import android.app.Activity;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.models.weather.WeatherConditions;
import com.mozilla.fennec.search.models.weather.WeatherForecast;
import com.mozilla.fennec.search.models.weather.WeatherModel;

public class WeatherCard extends TitleCard<WeatherModel> {

  private View mBody;
  private ImageView currentWeatherIcon;
  private TextView currentTemp;
  private TextView mForecast;

  public WeatherCard(Activity activity) {
    super(activity);
    mBody = activity.getLayoutInflater().inflate(R.layout.card_weather,
        (ViewGroup) activity.findViewById(R.id.card_stream), false);

    currentWeatherIcon = (ImageView) mBody.findViewById(R.id.weather_current_condition_icon);
    currentTemp = (TextView) mBody.findViewById(R.id.weather_current_temperature);
    mForecast = (TextView) mBody.findViewById(R.id.forecast);
  }
  @Override
  public String getCardTag() {
    return "weather";
  }

  private int getIcon(WeatherConditions condition, boolean isDaytime) {
    switch(condition) {
      case RAIN:
        return R.drawable.weather_rain;

      case SNOW:
        return R.drawable.weather_snow;

      case HAIL:
      case SLEET:
        return R.drawable.weather_hail;

      case WIND:
        return R.drawable.weather_wind;

      case CLOUD:
        return R.drawable.weather_cloud;

      case PART_CLOUD:
        if (isDaytime)
          return R.drawable.weather_part_cloud_day;
        else
          return R.drawable.weather_part_cloud_night;

      case THUNDERSTORM:
        return R.drawable.weather_thunder;

      // The default case is returning a sun / moon right now....
      case CLEAR:
      case FOG:
      case TORNADO:
      default:
        if (isDaytime)
          return R.drawable.weather_sun;
        else
          return R.drawable.weather_moon;
    }
  }


  private String getConditionString(WeatherConditions condition) {
    switch(condition) {
      case RAIN:
        return "rain";

      case SNOW:
        return "show";

      case HAIL:
        return "hail";
      case SLEET:
        return "sleet";

      case WIND:
        return "windy";

      case CLOUD:
        return "cloudy";

      case PART_CLOUD:
        return "partly cloudy";

      case THUNDERSTORM:
        return "thunder";

      case CLEAR:
        return "clear";
      case FOG:
        return "foggy";
      case TORNADO:
        return "tornado";
    }
    return "";
  }

  public void ingest(WeatherModel model) {
    currentWeatherIcon.setImageResource(getIcon(model.getCondition(), model.isDaylight()));
    currentTemp.setText(model.getCurrentTemperature().getFahrenheitString());
    setTitle("Weather");
    mForecast.setText(makeDescription(model));
    setBody(mBody);
  }



  private String makeDescription(WeatherModel model) {
    StringBuilder desc = new StringBuilder();
    Time now = new Time();
    now.setToNow();

    if (model.isDaylight()) {
      int hourRemain = model.getTodaysForecast().getSunsetTime().hour - now.hour;
      if (hourRemain == 1 || hourRemain == 0) {
        desc.append("Less than an hour of daylight remaining.").append('\n');
      }
      else if (hourRemain > 0) {
        desc.append("About ").append(hourRemain).append(" hours of daylight remaining.").append('\n');
      }

    }

    desc.append("\tSunrise: ").append(
        model.getTodaysForecast().getSunriseTime().format("%I:%M %p")).append('\n');
    desc.append("\tSunset: ").append(
        model.getTodaysForecast().getSunsetTime().format("%I:%M %p")).append('\n');

    desc.append('\n');
    for(WeatherForecast forecast : model.getForecast()) {
      if ((forecast.getForecastDate().yearDay - now.yearDay) >= 0 && (forecast.getForecastDate().yearDay - now.yearDay) < 3) {
        desc.append(forecast.getForecastDate().format("%a:\t "));
        desc.append(getConditionString(forecast.getCondition()));
        desc.append("\n\t");
        desc.append(forecast.getLowTemp().getFahrenheitString());
        desc.append(" | ");
        desc.append(forecast.getHighTemp().getFahrenheitString());
        desc.append('\n');

      }
    }
    return desc.toString();
  }
}
