package org.mozilla.search.autocomplete;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

/**
 * A fragment to handle autocomplete. Its interface with the outside
 * world should be very very limited.
 * <p/>
 * TODO: Add clear button to search input
 * TODO: Add more search providers (other than the dictionary)
 * TODO: Wire search to send an intent
 */
public class AutoCompleteFragment extends Fragment implements AdapterView.OnItemClickListener,
    TextView.OnEditorActionListener, AcceptsJumpTaps {

  private View mMainView;
  private FrameLayout mBackdropFrame;
  private EditText mSearchBar;
  private ListView mSuggestionDropdown;
  private InputMethodManager mInputMethodManager;
  private AutoCompleteAdapter mAutoCompleteAdapter;
  private AutoCompleteAgent mAutoCompleteAgent;
  private State mState;

  private enum State {
    WAITING,  // The user is doing something else in the app.
    RUNNING   // The user is in search mode.
  }

  public AutoCompleteFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {


    mMainView = inflater.inflate(R.layout.auto_complete, container, false);
    mBackdropFrame = (FrameLayout) mMainView.findViewById(R.id.auto_complete_backdrop);
    mSearchBar = (EditText) mMainView.findViewById(R.id.auto_complete_search_bar);
    mSuggestionDropdown = (ListView) mMainView.findViewById(R.id.auto_complete_dropdown);

    mInputMethodManager = (InputMethodManager) getActivity().getSystemService(
        Context.INPUT_METHOD_SERVICE);

    // Attach a listener for the "search" key on the keyboard.
    mSearchBar.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        mAutoCompleteAgent.search(s.toString());
      }
    });
    mSearchBar.setOnEditorActionListener(this);
    mSearchBar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (v.hasFocus())
          return;
        transitionToRunning();
      }
    });

    mBackdropFrame.setOnClickListener(new BackdropClickListener());

    mAutoCompleteAdapter = new AutoCompleteAdapter(getActivity(), this);

    // Disable notifying on change. We're going to be changing the entire dataset, so
    // we don't want multiple re-draws.
    mAutoCompleteAdapter.setNotifyOnChange(false);

    mSuggestionDropdown.setAdapter(mAutoCompleteAdapter);

    initRows();

    mAutoCompleteAgent = new AutoCompleteAgent(getActivity(), new MainUiHandler(mAutoCompleteAdapter));

    // This will hide the autocomplete box and background frame.
    // Is there a case where we *shouldn't* hide this upfront?
    transitionToWaiting();

    // Attach listener for tapping on a suggestion.
    mSuggestionDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String query = ((AutoCompleteModel)mSuggestionDropdown.getItemAtPosition(position)).getMainText();
        startSearch(query);
      }
    });

    return mMainView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (null != mInputMethodManager)
      mInputMethodManager = null;
    if (null != mMainView)
      mMainView = null;
    if (null != mSearchBar)
      mSearchBar = null;
    if (null != mSuggestionDropdown) {
      mSuggestionDropdown.setOnItemClickListener(null);
      mSuggestionDropdown.setAdapter(null);
      mSuggestionDropdown = null;
    }
    if (null != mAutoCompleteAdapter)
      mAutoCompleteAdapter = null;
  }

  /**
   * Handler for clicks of individual items.
   */
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    // TODO: Extract the query from the row
    String s = null;
    Log.i("click", "foo");
    startSearch(s);
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


  private void initRows() {
    // TODO: Query history for these items.
    mAutoCompleteAdapter.add(new AutoCompleteModel("mozilla"));
    mAutoCompleteAdapter.add(new AutoCompleteModel("kittens"));
    mAutoCompleteAdapter.add(new AutoCompleteModel("pho sf"));
    mAutoCompleteAdapter.add(new AutoCompleteModel("arrested development"));

    mAutoCompleteAdapter.notifyDataSetChanged();
  }


  /**
   * Send a search intent and put the widget into waiting.
   */
  private void startSearch(String queryString) {
    if (getActivity() instanceof AcceptsSearchQuery) {
      mSearchBar.setText(queryString);
      mSearchBar.setSelection(queryString.length());
      transitionToWaiting();
      ((AcceptsSearchQuery)getActivity()).onSearch(queryString);
    } else {
      throw new RuntimeException("Parent activity does not implement AcceptsSearchQuery.");
    }
  }

  private void transitionToWaiting() {
    if (mState == State.WAITING)
      return;
    mSearchBar.setFocusable(false);
    mSearchBar.setFocusableInTouchMode(false);
    mSearchBar.clearFocus();
    mInputMethodManager.hideSoftInputFromWindow(mSearchBar.getWindowToken(), 0);
    mSuggestionDropdown.setVisibility(View.GONE);
    mBackdropFrame.setVisibility(View.GONE);
    mState = State.WAITING;
  }

  private void transitionToRunning() {
    if (mState == State.RUNNING)
      return;
    mSearchBar.setFocusable(true);
    mSearchBar.setFocusableInTouchMode(true);
    mSearchBar.requestFocus();
    mInputMethodManager.showSoftInput(mSearchBar, InputMethodManager.SHOW_IMPLICIT);
    mSuggestionDropdown.setVisibility(View.VISIBLE);
    mBackdropFrame.setVisibility(View.VISIBLE);
    mState = State.RUNNING;
  }

  @Override
  public void onJumpTap(String suggestion) {
    mSearchBar.setText(suggestion);
    // Move cursor to end of search input.
    mSearchBar.setSelection(suggestion.length());
    mAutoCompleteAgent.search(suggestion);
  }


  /**
   * Receives messages from the SuggestionAgent's background thread.
   */
  private static class MainUiHandler extends Handler {

    AutoCompleteAdapter mAutoCompleteAdapter;

    public MainUiHandler(AutoCompleteAdapter autoCompleteAdapter) {
      mAutoCompleteAdapter = autoCompleteAdapter;
    }

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (null == msg.obj)
        return;

      mAutoCompleteAdapter.clear();
      String[] res = (String[]) msg.obj;
      int firstPass = Math.min(mAutoCompleteAdapter.getCount(), res.length);
      for (int i = 0; i < firstPass; i++) {
        mAutoCompleteAdapter.getItem(i).setMainText(res[i]);

      }

      if (res.length > mAutoCompleteAdapter.getCount()) {
        for (int i = firstPass; i < res.length; i++) {
          mAutoCompleteAdapter.add(new AutoCompleteModel(res[i]));
        }
      }

      mAutoCompleteAdapter.notifyDataSetChanged();

    }
  }

  /**
   * Click handler for the backdrop. This should:
   * - Remove focus from the search bar
   * - Hide the keyboard
   * - Hide the backdrop
   * - Hide the suggestion box.
   */
  public class BackdropClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      transitionToWaiting();
    }
  }
}
