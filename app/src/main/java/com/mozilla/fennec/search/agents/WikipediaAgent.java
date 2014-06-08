package com.mozilla.fennec.search.agents;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.mozilla.fennec.search.cards.AcceptsCard;
import com.mozilla.fennec.search.models.RowModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class WikipediaAgent extends JsonAgent {

  public WikipediaAgent(Activity activity, AcceptsCard cardSink) {
    super(activity, cardSink);
  }

  @Override
  protected String fetchJson(Query query) {

    String url =
        String.format("http://en.wikipedia.org/w/api.php?action=opensearch&search=%s",
            query.getQueryString());
    Log.i("url", url);
    try {
      Log.i("Start fetch", url);
      return String.format("{\"results\":%s}", fetchHttp(url));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected WikipediaRowModel createCardModel(JsonResponse response) {
    WikipediaRowModel model = new WikipediaRowModel();
    try {
      JSONArray results = response.getResponse().getJSONArray("results").getJSONArray(1);
      if (results.length() == 0)
        return null;
      for (int i = 0; i < results.length(); i++) {
        model.addRow(new WikipediaRow(results.getString(i),
            Uri.parse(String.format("http://en.wikipedia.org/wiki/%s", results.getString(i)))));
      }
      return model;
    } catch (JSONException e) {
      return null;
    }
  }

  public class WikipediaRow {
    private String mRowText;
    private Uri mRowUri;

    private WikipediaRow(String mRowText, Uri rowUri) {
      this.mRowText = mRowText;
      this.mRowUri = rowUri;
    }

    public String getRowText() {
      return mRowText;
    }

    public Uri getRowUri() {
      return mRowUri;
    }
  }

  public class WikipediaRowModel extends RowModel<WikipediaRow> {
    public WikipediaRowModel() {
      super("Wikipedia");
    }
  }
}
