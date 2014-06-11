package com.mozilla.fennec.search.models;

public class TitleCardModel implements CardModel, HasTitle {
  private String mTitle;
  private int mIcon;

  public TitleCardModel(String mTitle, int mIcon) {
    this.mTitle = mTitle;
    this.mIcon = mIcon;
  }

  public String getTitle() {
    return mTitle;
  }

  public void setTitle(String mTitle) {
    this.mTitle = mTitle;
  }

  public int getIcon() {
    return mIcon;
  }

  public void setIcon(int mIcon) {
    this.mIcon = mIcon;
  }
}
