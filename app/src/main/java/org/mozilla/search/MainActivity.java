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


/**
 * The main entrance for the Android search intent.
 * <p/>
 * State management is delegated to child fragments. Fragments communicate
 * with each other by passing messages through this activity. The only message passing right
 * now, the only message passing occurs when a user wants to submit a search query. That
 * passes through the onSearch method here.
 */
public class MainActivity extends FragmentActivity implements AcceptsSearchQuery,
        FragmentManager.OnBackStackChangedListener {

    private PostSearchFragment detailActivity;

    @Override
    protected void onCreate(Bundle stateBundle) {
        super.onCreate(stateBundle);

        // Sets the content view for the Activity
        setContentView(R.layout.search_activity_main);

        // Gets an instance of the support library FragmentManager
        FragmentManager localFragmentManager = getSupportFragmentManager();

        // If the incoming state of the Activity is null, sets the initial view to be thumbnails
        if (null == stateBundle) {

            // Starts a Fragment transaction to track the stack
            FragmentTransaction localFragmentTransaction = localFragmentManager.beginTransaction();

            localFragmentTransaction.add(R.id.header_fragments, new SearchFragment(),
                    Constants.SEARCH_FRAGMENT);

            localFragmentTransaction.add(R.id.presearch_fragments, new PreSearchFragment(),
                    Constants.PRESEARCH_FRAGMENT);

            // Commits this transaction to display the Fragment
            localFragmentTransaction.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (null == detailActivity) {
            detailActivity = new PostSearchFragment();
        }

        if (null == getSupportFragmentManager().findFragmentByTag(Constants.POSTSEARCH_FRAGMENT)) {
            FragmentTransaction txn = getSupportFragmentManager().beginTransaction();
            txn.add(R.id.gecko_fragments, detailActivity, Constants.POSTSEARCH_FRAGMENT);
            txn.hide(detailActivity);

            txn.commit();
        }
    }

    @Override
    public void onSearch(String s) {
        FragmentManager localFragmentManager = getSupportFragmentManager();
        FragmentTransaction localFragmentTransaction = localFragmentManager.beginTransaction();

        localFragmentTransaction
                .hide(localFragmentManager.findFragmentByTag(Constants.POSTSEARCH_FRAGMENT))
                .addToBackStack(null);

        localFragmentTransaction
                .show(localFragmentManager.findFragmentByTag(Constants.POSTSEARCH_FRAGMENT))
                .addToBackStack(null);

        localFragmentTransaction.commit();


        ((PostSearchFragment) getSupportFragmentManager()
                .findFragmentByTag(Constants.POSTSEARCH_FRAGMENT))
                .setUrl("https://search.yahoo.com/search?p=" + Uri.encode(s));
    }

    @Override
    public void onBackStackChanged() {

    }
}
