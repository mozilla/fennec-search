package com.mozilla.fennec.search.events;

public class QueryEvent {
  private String mQuery;

  public QueryEvent() {
  }

  public QueryEvent(String query) {
    mQuery = query;
  }

  public String getQuery() {
    return mQuery;
  }

  public void setQuery(String s) {
    mQuery = s;
  }
}
