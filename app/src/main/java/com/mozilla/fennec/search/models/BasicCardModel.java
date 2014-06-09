package com.mozilla.fennec.search.models;

public class BasicCardModel extends TitleCardModel {
  private String description;

  public BasicCardModel(String title, String description) {
    super(title);
    this.description = description;
  }


  public String getDescription() {
    return description;
  }
}
