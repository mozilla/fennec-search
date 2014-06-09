package com.mozilla.fennec.search.events;

public class AutoCompleteSelectEvent extends QueryEvent {

  public AutoCompleteSelectEvent(String query) {
    super(query);
  }

  public AutoCompleteSelectEvent() {
    super();
  }
}
