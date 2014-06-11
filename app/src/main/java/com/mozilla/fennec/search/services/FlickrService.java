package com.mozilla.fennec.search.services;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.mozilla.fennec.search.Configuration;
import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.agents.Query;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mozilla.fennec.search.agents.HttpAgent.fetchHttp;

public class FlickrService extends AsyncTask<Query, Void, String> {

  private Activity activity;
  private ImageView dest;

  public FlickrService(Activity activity, ImageView dest) {
    this.activity = activity;
    this.dest = dest;
  }

  private String buildUrl(Query query) {
    Uri.Builder builder = new Uri.Builder();
    builder.scheme("https").authority("api.flickr.com").appendPath("services").appendPath("rest");
    builder.appendQueryParameter("api_key", Configuration.FLICKR_KEY);
    builder.appendQueryParameter("sort", "interestingness-desc");
    builder.appendQueryParameter("method", "flickr.photos.search");
    builder.appendQueryParameter("tags", query.getQueryString());
    builder.appendQueryParameter("format", "json");
    builder.appendQueryParameter("extras", "url_z");
    builder.appendQueryParameter("nojsoncallback", "1");
    builder.appendQueryParameter("lat", String.valueOf(query.getLatitude()));
    builder.appendQueryParameter("lon", String.valueOf(query.getLongitude()));
    builder.appendQueryParameter("per_page", "15");

    return builder.build().toString();
  }

  private JSONObject fetch(String url) throws JSONException {
    String textResponse = fetchHttp(url);
    return new JSONObject(textResponse);
  }

  protected List<String> getUrls(JSONObject response) throws JSONException {
    JSONArray results = response.getJSONObject("photos").getJSONArray("photo");
    ArrayList<String> urls = new ArrayList<String>();

    int tot = results.length();

    for (int i = 0; i < tot; i++) {
      JSONObject photo = results.getJSONObject(i);
      urls.add(photo.getString("url_z"));
    }

    return urls;
  }

  @Override
  protected String doInBackground(Query... queries) {
    if (queries.length != 1) {
      return "";
    }

    try {
      List<String> urls = getUrls(fetch(buildUrl(queries[0])));
      return urls.size() > 0 ? urls.get(0) : null;
    } catch (JSONException e) {
      Log.e("doInBackground", e.toString());
      return "";
    }
  }

  @Override
  protected void onPostExecute(String s) {
    if (s == null || s.isEmpty()) {
      Picasso.with(activity).load(R.drawable.stadium_hero).into(dest);
    } else {
      Picasso.with(activity).load(s)
          .placeholder(R.drawable.stadium_hero)
          .error(R.drawable.stadium_hero).resize(360, 200).centerCrop().into(dest);
    }

  }
}
