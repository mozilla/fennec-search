package com.mozilla.fennec.search.agents;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.cards.AcceptsCard;
import com.mozilla.fennec.search.cards.DisambiguationCard;
import com.mozilla.fennec.search.cards.EntityCard;
import com.mozilla.fennec.search.cards.IsCard;
import com.mozilla.fennec.search.cards.RestaurantCard;
import com.mozilla.fennec.search.cards.RowCard;
import com.mozilla.fennec.search.cards.TitleCard;
import com.mozilla.fennec.search.models.BasicCardModel;
import com.mozilla.fennec.search.models.CardModel;
import com.mozilla.fennec.search.models.disambiguation.DisambiguationModel;
import com.mozilla.fennec.search.models.entity.EntityModel;
import com.mozilla.fennec.search.models.restaurant.RestaurantList;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public abstract class HttpAgent<T> extends AsyncTask<Query, Void, T> {


  private Activity mActivity;
  private AcceptsCard mCardSink;

  public HttpAgent(Activity activity, AcceptsCard cardSink) {
    mActivity = activity;
    mCardSink = cardSink;
  }

  public static String fetchHttp(String myurl)  {

    InputStream is = null;
    try {
      URL url = new URL(myurl);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(10000 /* milliseconds */);
      conn.setConnectTimeout(15000 /* milliseconds */);
      conn.setRequestMethod("GET");
      conn.setDoInput(true);
      // Starts the query
      conn.connect();
      int response = conn.getResponseCode();
      Log.d("HTTP Fetch", "The response is: " + response);
      is = conn.getInputStream();


      return convertStreamToString(is);

      // Makes sure that the InputStream is closed after the app is
      // finished using it.
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return "";
  }

  static String convertStreamToString(InputStream is) {

    java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

  public void runAsync(Query query) {
    this.execute(query);
  }

  protected abstract T doInBackground(Query... queries);

  protected abstract CardModel createCardModel(T response) throws JSONException;

  @Override
  protected void onPostExecute(T result) {
    CardModel model = null;
    try {
      model = createCardModel(result);
    } catch (JSONException e) {
      e.printStackTrace();
      return;
    }
    IsCard card = null;

    if (model == null)
      return;

   if (model instanceof BasicCardModel) {
      card = new TitleCard(mActivity);
      ((TitleCard) card).setTitle(((BasicCardModel) model).getTitle());
      ((TitleCard) card).setBody(((BasicCardModel) model).getDescription());
      ((TitleCard) card).setCardTag(((BasicCardModel) model).getTitle());
      ((TitleCard) card).setIcon(((BasicCardModel) model).getIcon());
    } else if (model instanceof RestaurantList) {
      card = new RestaurantCard(mActivity);
      card.ingest(model);
    } else if (model instanceof WikipediaAgent.WikipediaRowModel) {
      card =
          new RowCard<WikipediaAgent.WikipediaRow>(mActivity, R.layout.card_multi_result, R.layout.card_multi_result_row) {

            @Override
            protected void populateRowView(View rowView, final WikipediaAgent.WikipediaRow rowModel) {
              TextView textView = (TextView) rowView.findViewById(R.id.rowText);
              textView.setText(rowModel.getRowText());
              rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Intent browserIntent = new Intent(Intent.ACTION_VIEW, rowModel.getRowUri());
                  mActivity.startActivity(browserIntent);
                }
              });
            }
          };
      card.ingest(model);

    } else if (model instanceof EntityModel) {
      card = new EntityCard(mActivity);
      card.ingest((EntityModel) model);
    } else if (model instanceof DisambiguationModel) {
      card = new DisambiguationCard(mActivity);
      card.ingest((DisambiguationModel) model);
    }

    if (card != null)
      mCardSink.addCard(card);
  }
}
