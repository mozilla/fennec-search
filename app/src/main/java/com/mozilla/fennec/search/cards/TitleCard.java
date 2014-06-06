package com.mozilla.fennec.search.cards;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.models.HasTitle;

public class TitleCard<M extends HasTitle> extends BaseCard<M> {

  private TextView mTitleTextView = null;

  public TitleCard(Activity activity) {
    super(activity);

    mTitleTextView = (TextView) mCardView.findViewById(R.id.card_title);
    mTitleTextView.setVisibility(View.VISIBLE);
  }

  @Override
  public void ingest(M model) {
    super.ingest(model);
    setTitle(model.getTitle());
  }

  public void setTitle(String title) {
    if (mTitleTextView != null) {
      mTitleTextView.setText(title);
    }
  }
}



