package com.mozilla.fennec.search.agents;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.mozilla.fennec.search.Configuration;
import com.mozilla.fennec.search.models.restaurant.RestaurantModel;
import com.mozilla.fennec.search.models.restaurant.RestaurantRow;
import com.mozilla.fennec.search.models.units.Distance;
import com.yelp.api.Yelp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class YelpAgent extends JsonAgent {

  public YelpAgent(Activity activity) {
    super(activity);
  }

  @Override
  protected String fetchJson(Query query) {
    Yelp yelpClient = Yelp.getInstance(
        Configuration.YELP_CONSUMER_KEY,
        Configuration.YELP_CONSUMER_SECRET,
        Configuration.YELP_TOKEN,
        Configuration.YELP_TOKEN_SECRET);

    // This follows the convention of fetchJson since Scribe (which YelpClient is using)
    // returns null on failure
    // See: https://github.com/fernandezpablo85/scribe-java/blob/master/src/main/java/org/scribe/model/Response.java#L67
    String results = yelpClient.search(query.getQueryString(), query.getmLatitude(), query.getmLongitude());
    Log.i("yelpResults", results);
    return results;
  }

  @Override
  protected RestaurantModel createCardModel(JsonResponse response) {
    RestaurantModel model = new RestaurantModel("Yelp");
    try {
      JSONArray results = response.getResponse().getJSONArray("businesses");
      if (results.length() == 0)
        return null;
      for (int i = 0; i < results.length(); i++) {
        JSONObject result = results.getJSONObject(i);
        RestaurantRow.RestaurantRowBuilder builder = new RestaurantRow.RestaurantRowBuilder();

        model.addRow(builder
                .setName(result.getString("name"))
                .setThumbnail(Uri.parse(result.getString("image_url")))
                .setRating(result.getDouble("rating"))
                .setDistance(Distance.fromMeters(result.getInt("distance")))
                .setProviderPage(Uri.parse(result.getString("mobile_url")))
                .createRestaurantRow()
        );
      }

    } catch (JSONException e) {
      return null;
    }
    return model;
  }


}
