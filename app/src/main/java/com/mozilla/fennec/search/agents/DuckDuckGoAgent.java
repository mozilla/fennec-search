package com.mozilla.fennec.search.agents;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.cards.AcceptsCard;
import com.mozilla.fennec.search.models.CardModel;
import com.mozilla.fennec.search.models.disambiguation.DisambiguationModel;
import com.mozilla.fennec.search.models.entity.EntityModel;
import com.mozilla.fennec.search.models.types.URI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DuckDuckGoAgent extends JsonAgent {

  public DuckDuckGoAgent(Activity activity, AcceptsCard cardSink) {
    super(activity, cardSink);
  }

  @Override
  protected String fetchJson(Query query) {
    String url =
        String.format("http://api.duckduckgo.com/?format=json&pretty=1&q=%s",
            query.getQueryString());
    Log.i("url", url);
    Log.i("Start fetch", url);
    return fetchHttp(url);
  }

  @Override
  protected CardModel createCardModel(JsonResponse response) {

    try {
      JSONObject responseJs = response.getResponse();
      String responseType = responseJs.getString("Type");
      Log.i("createCardModel", responseType);
      /**
       * Type: response category, i.e.
       *  A (article),
       *  D (disambiguation),
       *  C (category),
       *  N (name),
       *  E (exclusive), or nothing.
       */
      if (responseType.equals("A"))
        return singleEntity(responseJs);
      else if (responseType.equals("D")) {
        return disambiguation(responseJs);
      }

    } catch (JSONException e) {
      Log.e("createCardModel", e.toString());
    }
    return null;
  }

  private DisambiguationModel disambiguation(JSONObject responseJs) throws JSONException {
    JSONArray results = responseJs.getJSONArray("RelatedTopics");
    DisambiguationModel model = new DisambiguationModel("Duck Duck Go", R.drawable.duckduck);
    int tot = Math.min(results.length(), 3);
    for (int i = 0; i < tot; i++) {
      String title = "";
      String subtitle = "";
      String thumbnail = "";
      JSONObject result = results.getJSONObject(i);
      if (result.has("Result")) {
        String textHtml = result.getString("Result");

        String[] parts = textHtml.split("<[^>]*>");
        if (parts.length == 3) {
          title = parts[1];
          subtitle = parts[2];
        }
      }
      if (result.has("Icon")) {
        thumbnail = result.getJSONObject("Icon").getString("URL");
      }

      model.addRow(new DisambiguationModel.DisambiguationEntry(title, subtitle, new URI(thumbnail)));
    }
    return model;
  }

  private EntityModel singleEntity(JSONObject response) throws JSONException {
    Uri thumbnail = Uri.parse(response.getString("Image"));
    String title = response.getString("Heading");
    String description = response.getString("Abstract");
    Uri reference = Uri.parse(response.getString("AbstractURL"));

    return new EntityModel(thumbnail, title, description, reference, R.drawable.duckduck);
  }
}
