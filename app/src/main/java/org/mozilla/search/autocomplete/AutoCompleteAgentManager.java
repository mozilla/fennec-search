/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.search.autocomplete;

import android.os.Looper;

import java.util.List;

/**
 * A mediator between an ArrayAdapter and asynchronous search providers.
 *
 * Clients instantiate this class with an ArrayAdapter into which
 * results are placed. To retrieve new suggestions, a client calls `getSuggestions`;
 * the method returns to the caller directly, but utilizes a
 * background thread to fetch search suggestions.
 */
class AutoCompleteAgentManager extends AutoCompleteBaseAgent implements AcceptsSearchResults {
    private AutoCompleteYahooAgent yahooAgent;

    public AutoCompleteAgentManager(Looper foregroundLooper, AcceptsSearchResults callback) throws AgentException {
        super(foregroundLooper, callback);
        try {
            yahooAgent = new AutoCompleteYahooAgent(foregroundLooper, this);
        } catch (AgentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delegate the search query to the yahooAgent. In the future, this could
     * send queries to other agents as well in parallel.
     */
    @Override
    List<AutoCompleteModel> processQueryInBackground(String queryString) {
        if (yahooAgent != null) {
            yahooAgent.startSearch(queryString);
        }

        // Since we are delegating the work to the YahooAgent, we return null here.
        // This instructs the background thread to *not* send a notification
        // to the callback handler. Instead, we wait until the results are
        // returned within `onSuggestionsReceived`, and forward the results
        // to our callback handler.
        return null;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        if (yahooAgent != null) {
            yahooAgent.shutdown();
        }
    }

    /**
     * Send the results from our agent back to the callback handler.
     */
    @Override
    public void onSuggestionsReceived(List<AutoCompleteModel> results, AutoCompleteBaseAgent worker) {
        getCallback().onSuggestionsReceived(results, this);
    }
}
