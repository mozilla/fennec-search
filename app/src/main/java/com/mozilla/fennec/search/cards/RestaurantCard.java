package com.mozilla.fennec.search.cards;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.RichResultActivity;
import com.mozilla.fennec.search.models.restaurant.RestaurantModel;
import com.squareup.picasso.Picasso;

public class RestaurantCard extends RowCard<RestaurantModel> {
  public RestaurantCard(Activity activity) {
    super(activity, R.layout.card_restaurant, R.layout.card_restaurant_row);
  }

  @Override
  protected void populateRowView(final View rowView, final RestaurantModel rowModel) {
    ((TextView) rowView.findViewById(R.id.restaurantName)).setText(rowModel.getName());
    ((TextView) rowView.findViewById(R.id.distance)).setText(rowModel.getDistance().getKiloMeterString());
    ImageView imgView = (ImageView) rowView.findViewById(R.id.thumbnail);
    Picasso.with(mActivity).load(rowModel.getThumbnailImage().toString()).into(imgView);
    rowView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent richViewIntent = new Intent(getActivity(), RichResultActivity.class);
        richViewIntent.putExtra(RichResultActivity.YELP_ID, rowModel);
        getActivity().startActivity(richViewIntent);
      }
    });
  }
}
