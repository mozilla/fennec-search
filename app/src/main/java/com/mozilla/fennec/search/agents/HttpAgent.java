package com.mozilla.fennec.search.agents;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.cards.AcceptsCard;
import com.mozilla.fennec.search.cards.EntityCard;
import com.mozilla.fennec.search.cards.RestaurantCard;
import com.mozilla.fennec.search.cards.RowCard;
import com.mozilla.fennec.search.cards.TitleCard;
import com.mozilla.fennec.search.cards.WeatherCard;
import com.mozilla.fennec.search.models.BasicCardModel;
import com.mozilla.fennec.search.models.CardModel;
import com.mozilla.fennec.search.models.entity.EntityModel;
import com.mozilla.fennec.search.models.restaurant.RestaurantModel;
import com.mozilla.fennec.search.models.weather.WeatherModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import de.greenrobot.event.EventBus;

public abstract class HttpAgent<T> extends AsyncTask<Query, Void, T> {

  private Activity mActivity;
  private AcceptsCard mCardSink;
  public  HttpAgent(Activity activity, AcceptsCard cardSink) {
    mActivity = activity;
    mCardSink = cardSink;
  }

  public void runAsync(Query query) {
    this.execute(query);
  }

  protected abstract T doInBackground(Query... queries);

  protected abstract CardModel createCardModel(T response);

  @Override
  protected void onPostExecute(T result) {
    CardModel model = createCardModel(result);
    if (model instanceof WeatherModel) {
      WeatherCard card = new WeatherCard(mActivity);
      card.ingest((WeatherModel) model);
      mCardSink.addCard(card);
    } else if (model instanceof BasicCardModel) {
      TitleCard card = new TitleCard(mActivity);
      card.setTitle(((BasicCardModel) model).getTitle());
      card.setBody(((BasicCardModel) model).getDescription());
      card.setCardTag(((BasicCardModel) model).getTitle());
      EventBus.getDefault().post(card);
    } else if (model instanceof RestaurantModel) {
      RestaurantCard card = new RestaurantCard(mActivity);
      card.ingest((RestaurantModel) model);
      EventBus.getDefault().post(card);
    } else if (model instanceof WikipediaAgent.WikipediaRowModel) {
      RowCard<WikipediaAgent.WikipediaRow> card =
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
      card.ingest((WikipediaAgent.WikipediaRowModel) model);
      EventBus.getDefault().post(card);

    } else if (model instanceof EntityModel) {
      EntityCard card = new EntityCard(mActivity);
      card.ingest((EntityModel) model);
      EventBus.getDefault().post(card);
    }
  }

  public static String fetchHttp(String myurl) throws IOException {
    InputStream is = null;
    // Only display the first 500 characters of the retrieved
    // web page content.
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
        is.close();
      }
    }
    return "";
  }

  static String convertStreamToString(InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }
}
