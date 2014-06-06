package com.mozilla.fennec.search.events;

public class UserSubmitQueryEvent extends QueryEvent {

  public UserSubmitQueryEvent(String query) {
    super(query);
  }

  public UserSubmitQueryEvent() {
    super();
  }
}
