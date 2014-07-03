/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.search;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.mozilla.search.autocomplete.AcceptsSearchQuery;
import org.mozilla.search.autocomplete.SearchFragment;
import org.mozilla.search.stream.PreSearchFragment;


/**
 * The main entrance for the Android search intent.
 * <p/>
 * State management is delegated to child fragments. Fragments communicate
 * with each other by passing messages through this activity. The only message passing right
 * now, the only message passing occurs when a user wants to submit a search query. That
 * passes through the onSearch method here.
 */
public class MainActivity extends FragmentActivity implements AcceptsSearchQuery {

    private PreSearchFragment preSearchFragment;
    private PostSearchFragment postSearchFragment;
    private SearchFragment searchFragment;
    private FragmentManager fragmentManager;

    /**
     * Initialize all of the fragments, and add them to the fragment tree.
     */
    @Override
    protected void onCreate(Bundle stateBundle) {
        super.onCreate(stateBundle);
        setContentView(R.layout.search_activity_main);

        boolean geckoNeedsToBeHidden = true;

        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }

        final FragmentTransaction txn = fragmentManager.beginTransaction();

        preSearchFragment = (PreSearchFragment) fragmentManager.findFragmentByTag(Constants.PRESEARCH_FRAGMENT);
        postSearchFragment = (PostSearchFragment) fragmentManager.findFragmentByTag(Constants.POSTSEARCH_FRAGMENT);
        searchFragment = (SearchFragment) fragmentManager.findFragmentByTag(Constants.SEARCH_FRAGMENT);

        if (preSearchFragment == null) {
            preSearchFragment = new PreSearchFragment();
            txn.add(R.id.presearch_fragments, preSearchFragment, Constants.PRESEARCH_FRAGMENT);
        }

        if (postSearchFragment == null) {
            postSearchFragment = new PostSearchFragment();
            txn.add(R.id.gecko_fragments, postSearchFragment, Constants.POSTSEARCH_FRAGMENT);
            txn.hide(postSearchFragment);
            geckoNeedsToBeHidden = false;
        }

        if (searchFragment == null) {
            searchFragment = new SearchFragment();
            txn.add(R.id.header_fragments, searchFragment, Constants.SEARCH_FRAGMENT);
        }

        // Only commit the transaction if there are pending operations.
        if (!txn.isEmpty()) {
            txn.commit();
            if (geckoNeedsToBeHidden) {
                fragmentManager.executePendingTransactions();
            }
        }

        if (geckoNeedsToBeHidden) {
            showPreSearch();
        }
    }

    @Override
    public void onSearch(String s) {
        if (postSearchFragment.isHidden()) {
            showPostSearch();
        }
        postSearchFragment.setUrl("https://search.yahoo.com/search?p=" + Uri.encode(s));
     }

    private void showPreSearch() {
        final FragmentTransaction txn = fragmentManager.beginTransaction();
        txn.show(preSearchFragment).addToBackStack(null);
        txn.hide(postSearchFragment).addToBackStack(null);
        txn.commit();
    }

    private void showPostSearch() {
        final FragmentTransaction txn = fragmentManager.beginTransaction();
        txn.show(postSearchFragment).addToBackStack(null);
        txn.hide(preSearchFragment).addToBackStack(null);
        txn.commit();
    }

}
