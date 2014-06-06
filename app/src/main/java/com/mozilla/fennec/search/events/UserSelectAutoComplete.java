package com.mozilla.fennec.search.events;

public class UserSelectAutoComplete extends QueryEvent {
  public UserSelectAutoComplete(String query) {
    super(query);
  }

  public UserSelectAutoComplete() {
    super();
  }
}
