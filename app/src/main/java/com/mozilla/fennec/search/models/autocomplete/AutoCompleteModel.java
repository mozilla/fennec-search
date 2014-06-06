package com.mozilla.fennec.search.models.autocomplete;

import com.mozilla.fennec.search.models.CardModel;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteModel implements CardModel {


  private List<String> mSearchSuggestions;
  private String mSearchFormatString;

  public AutoCompleteModel(String mSearchFormatString) {
    this.mSearchFormatString = mSearchFormatString;
  }

  public void setSearchSuggestions(List<String> searchSuggestions) {
    mSearchSuggestions = searchSuggestions;
  }

  public List<SearchSuggestion> getSearchSuggestions() {
    List<SearchSuggestion> suggestions = new ArrayList<SearchSuggestion>();

    for (String query : mSearchSuggestions) {
      suggestions.add(
          new SearchSuggestion(query, String.format(mSearchFormatString, query)));
    }
    return suggestions;
  }

  public class SearchSuggestion {
    private String mQuery;
    private String mSearchUrl;

    public SearchSuggestion(String query, String searchUrl) {
      mQuery = query;
      mSearchUrl = searchUrl;
    }

    public String getSearchUrl() {
      return mSearchUrl;
    }

    public String getQuery() {
      return mQuery;
    }
  }
}
