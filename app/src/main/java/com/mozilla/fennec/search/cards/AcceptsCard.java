package com.mozilla.fennec.search.cards;

import android.app.Activity;

public interface AcceptsCard {
  public void addCard(IsCard card);
  public void addCard(IsCard card, Boolean canDismiss);
}
