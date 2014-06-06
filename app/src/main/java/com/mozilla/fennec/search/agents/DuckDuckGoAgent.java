package com.mozilla.fennec.search.agents;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.mozilla.fennec.search.models.entity.EntityModel;

import org.json.JSONException;

import java.io.IOException;

public class DuckDuckGoAgent extends JsonAgent {
  public DuckDuckGoAgent(Activity activity) {
    super(activity);
  }

  @Override
  protected String fetchJson(Query query) {
    String url =
        String.format("http://api.duckduckgo.com/?format=json&pretty=1&q=%s",
            query.getQueryString());
    Log.i("url", url);
    try {
      Log.i("Start fetch", url);
      return fetchHttp(url);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected EntityModel createCardModel(JsonResponse response) {
    Uri thumbnail = null;
    String title = "";
    String description = "";
    Uri reference = null;

    try {
      if (response.getResponse().getString("Image").equals(""))
        return null;

      thumbnail = Uri.parse(response.getResponse().getString("Image"));
      title = response.getResponse().getString("Heading");
      description = response.getResponse().getString("Abstract");
      reference = Uri.parse(response.getResponse().getString("AbstractURL"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return new EntityModel(thumbnail, title, description, reference);
  }
}
