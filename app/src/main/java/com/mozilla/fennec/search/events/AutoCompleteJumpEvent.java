package com.mozilla.fennec.search.events;

public class AutoCompleteJumpEvent extends QueryEvent {
  public AutoCompleteJumpEvent(String query) {
    super(query);
  }

  public AutoCompleteJumpEvent() {
    super();
  }
}
