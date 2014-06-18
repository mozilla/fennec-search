package org.mozilla.search.autocomplete;

/**
 * The SuggestionModel is the data model behind the autocomplete rows. Right now it
 * only has a text field. In the future, this could be extended to include other
 * types of rows. For example, a row that has a URL and the name of a website.
 */
public class AutoCompleteModel {

  // The text that should immediately jump out to the user;
  // for example, the name of a restaurant or the title
  // of a website.
  private String mMainText;

  public AutoCompleteModel(String mainText) {
    mMainText = mainText;
  }

  public String getMainText() {
    return mMainText;
  }

  public void setMainText(String mainText) {
    mMainText = mainText;
  }

  public String toString() {
    return mMainText;
  }

}
