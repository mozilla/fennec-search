package com.mozilla.fennec.search.models;

public class BasicCardModel extends TitleCardModel {
  private String description;

  public BasicCardModel(String title, String description, int icon) {
    super(title, icon);
    this.description = description;
  }


  public String getDescription() {
    return description;
  }
}
