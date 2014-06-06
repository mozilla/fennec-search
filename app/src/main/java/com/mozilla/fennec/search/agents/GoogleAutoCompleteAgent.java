package com.mozilla.fennec.search.agents;

import android.app.Activity;
import android.util.Log;

import com.mozilla.fennec.search.models.CardModel;
import com.mozilla.fennec.search.models.autocomplete.AutoCompleteModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class GoogleAutoCompleteAgent extends JsonAgent {

  private AutoCompleteModel mModel;

  public GoogleAutoCompleteAgent(Activity activity) {
    super(activity);
    mModel = new AutoCompleteModel("https://www.google.com/search?q=%s");
  }

  @Override
  protected String fetchJson(Query query) {

    String url =
        String.format("https://www.google.com/complete/search?client=firefox&q=%s",
            query.getQueryString());
    try {
      Log.i("Start fetch", url);
      return String.format("{\"results\":%s}", fetchHttp(url));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected CardModel createCardModel(JsonResponse response) {
    Log.i("google", response.getResponse().toString());
    try {
      ArrayList<String> results = new ArrayList<String>();
      JSONArray jsResults = response.getResponse().getJSONArray("results").getJSONArray(1);
      for (int i = 0; i < jsResults.length(); i++) {
        results.add(jsResults.getString(i));
      }
      mModel.setSearchSuggestions(results);
      return mModel;
    } catch (JSONException e) {
      Log.e("Google Search", e.toString());
      return null;
    }
  }
}
