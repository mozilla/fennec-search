package com.mozilla.fennec.search.cards;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.models.HasTitle;
import com.squareup.picasso.Picasso;

public class TitleCard<M extends HasTitle> extends BaseCard<M> {

  private TextView mTitleTextView = null;
  private ImageView mIconView = null;

  public TitleCard(Activity activity) {
    super(activity);

    mTitleTextView = (TextView) mCardView.findViewById(R.id.card_title);
    mTitleTextView.setVisibility(View.VISIBLE);

    mIconView = (ImageView) mCardView.findViewById(R.id.provider_icon);
    mIconView.setVisibility(View.VISIBLE);
  }

  @Override
  public void ingest(M model) {
    super.ingest(model);
    if (model.getTitle() != null)
      setTitle(model.getTitle());
    if (model.getIcon() != 0) {
      setIcon(model.getIcon());
    }
  }

  public void setTitle(String title) {
    if (mTitleTextView != null) {
      mTitleTextView.setText(title);
    }
  }

  public void setIcon(int iconDrawable) {
    Picasso.with(getActivity()).load(iconDrawable).into(mIconView);
  }
}



