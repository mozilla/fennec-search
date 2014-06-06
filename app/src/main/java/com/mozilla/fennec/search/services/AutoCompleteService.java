package com.mozilla.fennec.search.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mozilla.fennec.search.agents.HttpAgent;
import com.mozilla.fennec.search.events.AutoCompleteChangedEvent;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;

import de.greenrobot.event.EventBus;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AutoCompleteService extends IntentService {
  // TODO: Rename actions, choose action names that describe tasks that this
  // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
  private static final String ACTION_GET_AUTO_COMPLETE_SUGGESTIONS =
      "com.mozilla.fennec.search.action.ACTION_GET_AUTO_COMPLETE_SUGGESTIONS";

  // TODO: Rename parameters
  private static final String QUERY_STRING = "com.mozilla.fennec.search.extra.QUERY_STRING";

  public AutoCompleteService() {
    super("AutoCompleteService");
  }

  /**
   * Starts this service to perform action Foo with the given parameters. If
   * the service is already performing a task this action will be queued.
   *
   * @see IntentService
   */
  // TODO: Customize helper method
  public static void startSearch(Context context, String queryString) {
    Intent intent = new Intent(context, AutoCompleteService.class);
    intent.setAction(ACTION_GET_AUTO_COMPLETE_SUGGESTIONS);
    intent.putExtra(QUERY_STRING, queryString);
    context.startService(intent);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      if (ACTION_GET_AUTO_COMPLETE_SUGGESTIONS.equals(action)) {
        final String queryString = intent.getStringExtra(QUERY_STRING);
        handleAutoCompleteSearch(queryString);
      }
    }
  }

  /**
   * Handle action Foo in the provided background thread with the provided
   * parameters.
   */
  private void handleAutoCompleteSearch(String queryString) {

    URI uri;
    try {
      uri = new URI("https", "www.google.com", "/complete/search", "client=firefox&q=" + queryString, null);
    } catch (URISyntaxException e) {
      Log.e("Query string encoding error", queryString);
      return;
    }
    String body = null;
    JSONArray responseJs = null;
    try {
      body = HttpAgent.fetchHttp(uri.toASCIIString());
    } catch (IOException e) {
      Log.e("HttpFetchError", uri.toASCIIString());
      return;
    }

    Log.i("response", body);

    try {
      responseJs = new JSONArray(body);
    } catch (JSONException e) {
      Log.e("Json parse error", body);
      return;
    }

    if (responseJs.length() == 2) {
      LinkedList<String> response = new LinkedList<String>();
      try {
        JSONArray matches = responseJs.getJSONArray(1);
        int tot = Math.min(3, matches.length());
        for (int i = 0; i < tot; i++) {
          response.add(matches.getString(i));
        }
      } catch (JSONException e) {
        Log.e("Json array access error", body);
        return;
      }

      EventBus.getDefault().post(new AutoCompleteChangedEvent(new ArrayList<String>(
          response
      )));

    }


  }

}
