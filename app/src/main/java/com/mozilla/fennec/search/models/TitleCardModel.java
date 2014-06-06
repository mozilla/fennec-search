package com.mozilla.fennec.search.models;

public class TitleCardModel implements CardModel, HasTitle {
  private String mTitle;

  public TitleCardModel(String title) {
    mTitle = title;
  }

  public String getTitle() {
    return mTitle;
  }

  public void setTitle(String title) {
    mTitle = title;
  }
}
