package com.mozilla.fennec.search.agents;

import android.app.Activity;
import android.util.Log;

import com.mozilla.fennec.search.Configuration;
import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.cards.AcceptsCard;
import com.mozilla.fennec.search.models.restaurant.RestaurantList;
import com.mozilla.fennec.search.models.restaurant.RestaurantModel;
import com.mozilla.fennec.search.models.types.Address;
import com.mozilla.fennec.search.models.types.Distance;
import com.mozilla.fennec.search.models.types.URI;
import com.yelp.api.Yelp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class YelpAgent extends JsonAgent {

  public YelpAgent(Activity activity, AcceptsCard cardSink) {
    super(activity, cardSink);
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
    String results = yelpClient.search(query.getQueryString(), query.getLatitude(), query.getLongitude());
    Log.i("yelpResults", results);
    return results;
  }

  @Override
  protected RestaurantList createCardModel(JsonResponse response) {
    RestaurantList model = new RestaurantList("Yelp", R.drawable.yelp);
    try {
      JSONArray results = response.getResponse().getJSONArray("businesses");
      if (results.length() == 0)
        return null;
      for (int i = 0; i < results.length(); i++) {
        JSONObject result = results.getJSONObject(i);
        RestaurantModel.RestaurantModelBuilder builder = new RestaurantModel.RestaurantModelBuilder();

        if (result.has("name"))
          builder.setName(result.getString("name"));
        if (result.has("image_url")) {
          builder.setThumbnailImage(new URI(result.getString("image_url").replace("ms.jpg", "sl.jpg")))
              // Yelp uses the suffix on the image to determine its size
              // https://stackoverflow.com/questions/22000077/how-to-request-larger-images-from-yelp-api
              .setImage(new URI(result.getString("image_url").replace("ms.jpg", "o.jpg")));
        }

        if (result.has("display_phone"))
          builder.setPhoneNumber(result.getString("display_phone"));
        if (result.has("rating_img_url_large"))
          builder.setRatingImage(new URI(result.getString("rating_img_url_large")));
        if (result.has("snippet_text"))
          builder.setSnippet(result.getString("snippet_text"));
        if (result.has("rating"))
          builder.setRating(result.getDouble("rating"));
        if (result.has("review_count"))
          builder.setNumRatings(result.getInt("review_count"));
        if (result.has("distance"))
          builder.setDistance(Distance.fromMeters(result.getInt("distance")));
        if (result.has("mobile_url"))
          builder.setProviderPage(new URI(result.getString("mobile_url")));
        if (result.has("id"))
          builder.setYelpId(result.getString("id"));
        builder.setAddress(extract_address(result));
        model.addRow(builder.createRestaurantModel());
      }

    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
    return model;
  }

  private Address extract_address(JSONObject result) throws JSONException {
    final JSONObject addressJson = result.getJSONObject("location");
    Address.AddressBuilder builder = new Address.AddressBuilder();

    if (addressJson.has("address")) {
      JSONArray streetParts = addressJson.getJSONArray("address");
      if (streetParts.length() > 0)
        builder.setStreet(streetParts.getString(0));
    }


    if (addressJson.has("city"))
      builder.setCity(addressJson.getString("city"));

    if (addressJson.has("state_code"))
      builder.setState(addressJson.getString("state_code"));

    if (addressJson.has("postal_code"))
      builder.setZip(addressJson.getString("postal_code"));

    if (addressJson.has("country_code"))
      builder.setCountry(addressJson.getString("country_code"));

    return builder.createAddress();
  }


}
