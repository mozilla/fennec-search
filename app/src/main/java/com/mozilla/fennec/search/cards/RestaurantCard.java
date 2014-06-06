package com.mozilla.fennec.search.cards;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.models.restaurant.RestaurantRow;
import com.squareup.picasso.Picasso;

public class RestaurantCard extends RowCard<RestaurantRow> {
  public RestaurantCard(Activity activity) {
    super(activity, R.layout.card_restaurant, R.layout.card_restaurant_row);
  }

  @Override
  protected void populateRowView(final View rowView, final RestaurantRow rowModel) {
    ((TextView) rowView.findViewById(R.id.restaurantName)).setText(rowModel.getName());
    ((TextView) rowView.findViewById(R.id.distance)).setText(rowModel.getDistance().getKiloMeterString());
    ImageView imgView = (ImageView) rowView.findViewById(R.id.thumbnail);
    Picasso.with(mActivity).load(rowModel.getThumbnail().toString()).into(imgView);
    rowView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, rowModel.getProviderPage());
        mActivity.startActivity(browserIntent);
      }
    });
  }
}
