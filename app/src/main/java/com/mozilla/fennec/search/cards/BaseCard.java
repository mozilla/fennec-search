package com.mozilla.fennec.search.cards;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.cardstream.CardLayout;
import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.models.CardModel;

import java.util.UUID;

public class BaseCard<M extends CardModel> implements IsCard<M> {
  protected Activity mActivity;
  protected String mTag = null;
  protected CardLayout mCardView = null;
  protected FrameLayout mBodyView = null;

  public BaseCard(Activity activity) {
    mCardView = (CardLayout) activity.getLayoutInflater().inflate(getLayout(),
        (ViewGroup) activity.findViewById(R.id.card_stream), false);
    mBodyView = (FrameLayout) mCardView.findViewById(R.id.card_body);
    mTag = UUID.randomUUID().toString();
    mActivity = activity;
  }

  public String getCardTag() {
    return mTag;
  }

  public void setCardTag(String tag) {
    mTag = tag;
  }

  public CardLayout getView() {
    return mCardView;
  }

  public int getLayout() {
    return R.layout.card_base;
  }

  protected Activity getActivity() {
    return mActivity;
  }

  @Override
  public void ingest(M model) {

  }

  public void setBody(View child) {
    if (mBodyView != null) {
      mBodyView.addView(child);
    }
  }

  public void setBody(String s) {
    TextView body = new TextView(mActivity);
    body.setPadding(20,20,20,20);
    body.setText(s);
    setBody(body);
  }
}
