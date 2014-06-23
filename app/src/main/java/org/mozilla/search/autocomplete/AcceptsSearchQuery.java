package org.mozilla.search.autocomplete;


/**
 * Allows rows to pass a search event to the parent fragment.
 */
public interface AcceptsSearchQuery {
    void onSearch(String s);
}
