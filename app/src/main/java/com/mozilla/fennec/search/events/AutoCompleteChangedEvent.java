package com.mozilla.fennec.search.events;

import java.util.List;

public class AutoCompleteChangedEvent {
  private final List<String> mResults;

  public AutoCompleteChangedEvent(List<String> results) {
    mResults = results;
  }

  public List<String> getResults() {
    return mResults;
  }
}
