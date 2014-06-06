package com.mozilla.fennec.search.models;

public class TitleDescriptionCardModel extends TitleCardModel {
  private String description;

  public TitleDescriptionCardModel(String title, String description) {
    super(title);
    this.description = description;
  }


  public String getDescription() {
    return description;
  }
}
