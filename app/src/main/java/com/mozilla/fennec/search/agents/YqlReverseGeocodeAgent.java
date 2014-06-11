package com.mozilla.fennec.search.agents;


import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.mozilla.fennec.search.cards.AcceptsCard;
import com.mozilla.fennec.search.models.CardModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class YqlReverseGeocodeAgent extends JsonAgent {


  public YqlReverseGeocodeAgent(Activity activity, AcceptsCard cardSink) {
    super(activity, cardSink);
  }

  private String createUrl(double lat, double lng) {
    String query = String.format(
        "select * from geo.placefinder where gflags=\"R\" and text=\"%f,%f\"",
        lat,
        lng);
    return "https://query.yahooapis.com/v1/public/yql?format=json&q=" + Uri.encode(query);
  }

  @Override
  protected String fetchJson(Query query) {
    return "{\"query\":{\"count\":1,\"created\":\"2014-06-09T23:33:11Z\",\"lang\":\"en-US\",\"results\":{\"Result\":{\"quality\":\"87\",\"addressMatchType\":\"INTERPOLATED\",\"latitude\":\"37.789713\",\"longitude\":\"-122.389212\",\"offsetlat\":\"37.789713\",\"offsetlon\":\"-122.389212\",\"radius\":\"400\",\"name\":\"37.789713,-122.389212\",\"line1\":\"366 The Embarcadero\",\"line2\":\"San Francisco, CA 94105\",\"line3\":null,\"line4\":\"United States\",\"house\":\"366\",\"street\":\"The Embarcadero\",\"xstreet\":null,\"unittype\":null,\"unit\":null,\"postal\":\"94105\",\"neighborhood\":\"South Beach\",\"city\":\"San Francisco\",\"county\":\"San Francisco County\",\"state\":\"California\",\"country\":\"United States\",\"countrycode\":\"US\",\"statecode\":\"CA\",\"countycode\":null,\"uzip\":\"94105\",\"hash\":\"716063C28FEE3FCD\",\"woeid\":\"12797156\",\"woetype\":\"11\"}}}}";
//    try {
//      String result = fetchHttp(createUrl(query.getLatitude(), query.getLongitude()));
//      Log.i("Yql", result);
//    } catch (IOException e) {
//      Log.e("fetchJson", e.toString());
//    }
//    return null;
  }

  @Override
  protected CardModel createCardModel(JsonResponse response) {
    try {
      JSONObject jsAddr = response.getResponse().getJSONObject("query").getJSONObject("results").getJSONObject("Result");

    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }
}
