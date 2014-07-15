/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.search.autocomplete;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.mozilla.search.R;

import java.util.List;

/**
 * A fragment that helps the user construct their search.
 *
 * This fragment communicates with its parent activity via the AcceptsSearchQuery
 * interface. Once the user indicates that they'd like to perform a search
 * (by clicking on the keyboard's search button or by selecting a search suggestion),
 * we call onSearch for the parent activity.
 *
 * TODO: Add clear button to search input
 */
public class SearchFragment extends Fragment implements
        TextView.OnEditorActionListener, AcceptsJumpTaps, AcceptsSearchResults {

    private AutoCompleteAdapter autoCompleteAdapter;
    private AutoCompleteAgentManager autoCompleteAgentManager;

    // Covers and dims the underlying UI.
    private FrameLayout backdropFrame;

    // Keyboard
    private InputMethodManager inputMethodManager;
    private View mainView;
    private EditText searchBar;
    private ListView suggestionDropdown;
    private State state;


    private enum State {
        WAITING,  // The user is doing something else in the app.
        RUNNING   // The user is in search mode.
    }

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.search_auto_complete, container, false);
        backdropFrame = (FrameLayout) mainView.findViewById(R.id.auto_complete_backdrop);
        searchBar = (EditText) mainView.findViewById(R.id.auto_complete_search_bar);
        suggestionDropdown = (ListView) mainView.findViewById(R.id.auto_complete_dropdown);

        inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // Attach a listener for the "search" key on the keyboard.
        searchBar.addTextChangedListener(new TextChangedListener());
        searchBar.setOnEditorActionListener(this);
        searchBar.setOnClickListener(new SearchBarClickListener());

        backdropFrame.setOnClickListener(new BackdropClickListener());

        autoCompleteAdapter = new AutoCompleteAdapter(getActivity(), this);
        // We manually notify of changes.
        autoCompleteAdapter.setNotifyOnChange(false);

        suggestionDropdown.setAdapter(autoCompleteAdapter);
        suggestionDropdown.setOnItemClickListener(new SuggestionClickListener());

        try {
            autoCompleteAgentManager =
                    new AutoCompleteAgentManager(getActivity().getMainLooper(), this);
            autoCompleteAgentManager.startSearch("");
        } catch (AutoCompleteBaseAgent.AgentException e) {
            e.printStackTrace();
        }

        transitionToRunning();

        return mainView;
    }

    /**
     * Cleanup resources.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        inputMethodManager = null;
        mainView = null;
        searchBar = null;
        if (suggestionDropdown != null) {
            suggestionDropdown.setOnItemClickListener(null);
            suggestionDropdown.setAdapter(null);
            suggestionDropdown = null;
        }
        autoCompleteAdapter = null;
        autoCompleteAgentManager = null;
    }

    /**
     * Send a search intent and put the widget into waiting.
     */
    private void startSearch(String queryString) {
        if (getActivity() instanceof AcceptsSearchQuery) {
            searchBar.setText(queryString);
            searchBar.setSelection(queryString.length());
            transitionToWaiting();
            ((AcceptsSearchQuery) getActivity()).onSearch(queryString);
        } else {
            throw new RuntimeException("Parent activity does not implement AcceptsSearchQuery.");
        }
    }

    /**
     * Hide the keyboard, suggestions widget, and backdrop.
     */
    private void transitionToWaiting() {
        if (state == State.WAITING) {
            return;
        }
        searchBar.setFocusable(false);
        searchBar.setFocusableInTouchMode(false);
        searchBar.clearFocus();
        inputMethodManager.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
        suggestionDropdown.setVisibility(View.GONE);
        backdropFrame.setVisibility(View.GONE);
        state = State.WAITING;
    }

    /**
     * Show the keyboard, suggestions widget, and backdrop.
     */
    private void transitionToRunning() {
        if (state == State.RUNNING) {
            return;
        }
        searchBar.setFocusable(true);
        searchBar.setFocusableInTouchMode(true);
        searchBar.requestFocus();
        inputMethodManager.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
        suggestionDropdown.setVisibility(View.VISIBLE);
        backdropFrame.setVisibility(View.VISIBLE);
        state = State.RUNNING;
    }

    /**
     * Handler for the "search" button on the keyboard.
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            startSearch(v.getText().toString());
            return true;
        }
        return false;
    }

    /**
     * Update the listview with the new suggestions.
     */
    @Override
    public void onSuggestionsReceived(List<AutoCompleteModel> results, AutoCompleteBaseAgent worker) {
        autoCompleteAdapter.clear();
        for (AutoCompleteModel model : results) {
            autoCompleteAdapter.add(model);
        }
        autoCompleteAdapter.notifyDataSetChanged();
    }

    /**
     * Update the search bar with the new input.
     */
    @Override
    public void onJumpTap(String suggestion) {
        searchBar.setText(suggestion);
        // Move cursor to end of search input.
        searchBar.setSelection(suggestion.length());
        autoCompleteAgentManager.startSearch(suggestion);
    }

    /**
     * Click handler for the backdrop.
     */
    private class BackdropClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            transitionToWaiting();
        }
    }

    /**
     * Listen for the user typing on their keyboard.
     */
    private class TextChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            autoCompleteAgentManager.startSearch(s.toString());
        }

    }

    /**
     * A click listener for a user selecting a search suggestion.
     */
    private class SuggestionClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String query = ((AutoCompleteModel) suggestionDropdown.getItemAtPosition(position))
                    .getMainText();
            startSearch(query);
        }
    }

    /**
     * A click listener that allows the user to start searching.
     */
    private class SearchBarClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.hasFocus()) {
                return;
            }
            transitionToRunning();
        }
    }
}
