package com.mozilla.fennec.search.cards;

import android.view.View;

public interface IsCard<M> {

  String getCardTag();

  View getView();

  int getLayout();

  public void ingest(M model);
}

