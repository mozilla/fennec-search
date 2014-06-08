package com.mozilla.fennec.search.agents;

import android.app.Activity;
import android.util.Log;

import com.mozilla.fennec.search.cards.AcceptsCard;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JsonAgent extends HttpAgent<JsonResponse> {


  public JsonAgent(Activity activity, AcceptsCard cardSink) {
    super(activity, cardSink);
  }

  @Override
  protected JsonResponse doInBackground(Query... queries) {
    if (queries.length != 1) {
      throw new IllegalArgumentException("doInBackground takes one query obj.");
    }
    String jsonStr = fetchJson(queries[0]);
    JSONObject json;
    if (jsonStr == null) {
      return JsonResponse.failure("No JSON response received from server.");
    }
    try {
      json = new JSONObject(jsonStr);
    } catch (JSONException e) {
      Log.e("JsonParseError", e.toString());
      return JsonResponse.failure("Failed to parse JSON response.");
    }

    return JsonResponse.success(json);
  }

  /**
   * Implementers should use the query object to fetch results.
   * @param query The query. Includes the user's search term, if any, and their
   *              location.
   * @return A string that can be parsed as JSON, or null if there's an error.
   */
  protected abstract String fetchJson(Query query);


}
