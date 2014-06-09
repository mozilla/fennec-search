package com.mozilla.fennec.search;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozilla.fennec.search.agents.JsonResponse;
import com.mozilla.fennec.search.agents.YelpAgent;
import com.mozilla.fennec.search.cards.RestaurantCard;
import com.mozilla.fennec.search.models.RowModel;
import com.mozilla.fennec.search.models.restaurant.RestaurantModel;
import com.squareup.picasso.Picasso;
import com.yelp.api.Yelp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RichResultActivity extends Activity {

  public static final String YELP_ID = "com.mozilla.fennec.search.RichResultActivity.YELP_ID";

  private static final Yelp yelpClient = com.yelp.api.Yelp.getInstance(
      Configuration.YELP_CONSUMER_KEY,
      Configuration.YELP_CONSUMER_SECRET,
      Configuration.YELP_TOKEN,
      Configuration.YELP_TOKEN_SECRET);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_rich_result);

    if (getIntent().hasExtra(YELP_ID)) {
      RestaurantModel model = (RestaurantModel) getIntent().getSerializableExtra(YELP_ID);
      display(model);
    }
  }

  private void display(final RestaurantModel model) {

    Picasso.with(RichResultActivity.this)
        .load(model.getImage().toString())
        .into((ImageView) findViewById(R.id.hero));

    Picasso.with(RichResultActivity.this)
        .load(model.getRatingImage().toString())
        .into((ImageView) findViewById(R.id.rating_stars));

    ((TextView) findViewById(R.id.name)).setText(model.getName());
    ((TextView) findViewById(R.id.snippet)).setText(model.getAddress().getStreet());

    ((TextView) findViewById(R.id.num_reviews)).setText("(" + model.getNumRatings() + ")");

    ((TextView) findViewById(R.id.body_snippet)).setText(model.getSnippet());

    ((TextView) findViewById(R.id.address)).setText(model.getAddress().toString());

    findViewById(R.id.map_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
            Uri.parse("geo:0,0?q=" + model.getAddress().toString()));
        startActivity(intent);
      }
    });

    findViewById(R.id.yelp_button).setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(model.getProviderPage().toString()));
        startActivity(i);
      }
    });

    if (model.getPhoneNumber() != null) {
      findViewById(R.id.call_button).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          String uri = "tel:" + model.getPhoneNumber();
          Intent intent = new Intent(Intent.ACTION_DIAL);
          intent.setData(Uri.parse(uri));
          startActivity(intent);
        }
      });
    }

    else
      findViewById(R.id.call_button).setVisibility(View.GONE);

  }

  public void onBackArrow(View view) {
    onBackPressed();
  }

}
