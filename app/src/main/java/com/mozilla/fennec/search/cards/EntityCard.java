package com.mozilla.fennec.search.cards;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.models.entity.EntityModel;
import com.squareup.picasso.Picasso;

public class EntityCard extends TitleCard<EntityModel> {
  private ViewGroup mBody;

  public EntityCard(Activity activity) {
    super(activity);

    mBody = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.card_entity,
        (ViewGroup) activity.findViewById(R.id.card_stream), false);
    setBody(mBody);
  }

  @Override
  public void ingest(final EntityModel model) {
    super.ingest(model);

    ImageView imgView = (ImageView) mBody.findViewById(R.id.thumbnail);
    Picasso.with(mActivity).load(model.getThumbnail().toString()).into(imgView);

    ((TextView) mBody.findViewById(R.id.description)).setText(model.getDescription());

    mBody.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, model.getReference());
        mActivity.startActivity(browserIntent);
      }
    });
  }
}
