/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.search.test;

import android.test.ActivityInstrumentationTestCase2;

import org.mozilla.search.Constants;
import org.mozilla.search.MainActivity;
import org.mozilla.search.PostSearchFragment;
import org.mozilla.search.autocomplete.SearchFragment;
import org.mozilla.search.PreSearchFragment;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testPreConditions() {
        assertPreSearchVisible();
    }

    public void testSearch() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().onSearch("cable");
                getActivity().getSupportFragmentManager().executePendingTransactions();
            }
        });
        getInstrumentation().waitForIdleSync();

        assertPostSearchVisible();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().onBackPressed();
            }
        });
        getInstrumentation().waitForIdleSync();

        assertPreSearchVisible();
    }

    public void testDestroy() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().onSearch("cable");
                getActivity().getSupportFragmentManager().executePendingTransactions();
            }
        });
        getInstrumentation().waitForIdleSync();
        assertPostSearchVisible();

        getActivity().finish();
        getInstrumentation().waitForIdleSync();
        setActivity(null);

        // Restart the activity.
        getActivity();

        getInstrumentation().waitForIdleSync();

        assertPreSearchVisible();
    }

    private PostSearchFragment getPostSearchFragment() {
        return (PostSearchFragment) getActivity()
                .getSupportFragmentManager().findFragmentByTag(Constants.POSTSEARCH_FRAGMENT);
    }

    private PreSearchFragment getPreSearchFragment() {
        return (PreSearchFragment) getActivity()
                .getSupportFragmentManager().findFragmentByTag(Constants.PRESEARCH_FRAGMENT);
    }

    private SearchFragment getSearchFragment() {
        return (SearchFragment) getActivity()
                .getSupportFragmentManager().findFragmentByTag(Constants.SEARCH_FRAGMENT);
    }

    private void assertPreSearchVisible() {
        assertTrue(getPostSearchFragment().isHidden());
        assertTrue(getPreSearchFragment().isVisible());
        assertTrue(getSearchFragment().isVisible());
    }

    private void assertPostSearchVisible() {
        assertTrue(getPostSearchFragment().isVisible());
        assertTrue(getPreSearchFragment().isHidden());
        assertTrue(getSearchFragment().isVisible());
    }
}
