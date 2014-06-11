package com.mozilla.fennec.search.services;

import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class AutoCompleteService {

  public static final String RESULTS = "com.mozilla.fennec.search.services.AutoCompleteService.RESULTS";
  public static final int STATUS_OK = 0;
  private final AsyncHttpClient client = new AsyncHttpClient();

  public void search(String queryString, final ResultReceiver receiver) {
    String url = "https://search.yahoo.com/sugg/ff?output=fxjson&appid=ffm&nresults=3&command=" + Uri.encode(queryString);

    client.get(url, new AsyncHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        super.onSuccess(statusCode, headers, responseBody);


        Log.i("Response", new String(responseBody));

        JSONArray responseJs;
        try {
          responseJs = new JSONArray(new String(responseBody));

          if (responseJs.length() >= 2) {
            ArrayList<String> response = new ArrayList<String>();
            JSONArray matches = responseJs.getJSONArray(1);
            int tot = matches.length();
            for (int i = 0; i < tot; i++) {
              response.add(matches.getString(i));
            }
            Bundle results = new Bundle();
            results.putStringArrayList(RESULTS, response);
            receiver.send(STATUS_OK, results);
          }

        } catch (JSONException e1) {
          e1.printStackTrace();
        }


      }
    });
  }
}
