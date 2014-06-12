package com.mozilla.fennec.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.mozilla.fennec.search.events.AutoCompleteJumpEvent;
import com.mozilla.fennec.search.events.AutoCompleteSelectEvent;
import com.mozilla.fennec.search.services.AutoCompleteService;
import com.mozilla.fennec.search.widgets.AutoCompleteFragment;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;


public class AutoCompleteActivity extends FragmentActivity {

  private EditText searchInput;
  private ResultReceiver mReceiver;
  private AutoCompleteFragment mAutoCompleteFragment;

  public static final String QUERY = "com.mozilla.fennec.search.SearchActivity.QUERY";

  private static final String AUTO_COMPLETE_FRAGMENT = "com.mozilla.fennec.search.SearchActivity.AUTO_COMPLETE_FRAGMENT";

  private final AutoCompleteService autoCompleteService = new AutoCompleteService();
  private enum State {
    WAITING, RUNNING
  }

  private State state;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auto_complete);

    state = State.RUNNING;

    searchInput = (EditText) findViewById(R.id.search_input);

    bindLiveType();
    bindKeyboardSearchButton();

    mReceiver = new AutoCompleteReceiver(new Handler());

    FragmentManager fm = getSupportFragmentManager();

    mAutoCompleteFragment = (AutoCompleteFragment) fm.findFragmentByTag(AUTO_COMPLETE_FRAGMENT);

    if (mAutoCompleteFragment == null) {
      FragmentTransaction txn = fm.beginTransaction();
      mAutoCompleteFragment = new AutoCompleteFragment();
      txn.add(R.id.container, mAutoCompleteFragment, AUTO_COMPLETE_FRAGMENT);
      txn.commit();
    }

    if (getIntent().hasExtra(AutoCompleteActivity.QUERY)) {
      String query = getIntent().getStringExtra(AutoCompleteActivity.QUERY);
      if (query != null) {
        searchInput.setText(query);
        searchInput.setSelection(query.length());
      }
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  public void onEventMainThread(AutoCompleteJumpEvent event) {
    String query = event.getQuery();
    if (query != null) {
      searchInput.setText(query);
      searchInput.setSelection(query.length());
    }
  }

  public void onEventMainThread(AutoCompleteSelectEvent event) {
    String query = event.getQuery();
    if (query != null) {
      state = State.WAITING;
      searchInput.setText(query);
      searchInput.setSelection(query.length());

      Intent searchIntent = new Intent(AutoCompleteActivity.this, MainActivity.class);
      searchIntent.putExtra(QUERY, query);
      startActivity(searchIntent);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    searchInput = null;
  }

  public void clearButtonOnClick(View view) {
    state = State.WAITING;
    searchInput.setText("");
    state = State.RUNNING;
  }

  private void bindLiveType() {
    searchInput.addTextChangedListener(new TextWatcher() {

      private Timer timer;

      @Override
      public void onTextChanged(final CharSequence charSequence, int i, int i2, int i3) {
        if (state == State.WAITING) {
          return;
        }


        // User deleted a char with backspace.
        if (i2 == 1 && i3 == 0) {
          return;
        }

        if (timer != null) {
          timer.cancel();
          timer.purge();
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            autoCompleteService.search(charSequence.toString(), mReceiver);
          }
        }, 500);
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }

      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
      }

    });
  }


  private void bindKeyboardSearchButton() {
    searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          Intent searchIntent = new Intent(AutoCompleteActivity.this, MainActivity.class);
          searchIntent.putExtra(QUERY, searchInput.getText().toString());
          startActivity(searchIntent);
          handled = true;
        }
        return handled;
      }
    });
  }

  public class AutoCompleteReceiver extends ResultReceiver {

    public AutoCompleteReceiver(Handler handler) {
      super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
      super.onReceiveResult(resultCode, resultData);
      mAutoCompleteFragment.setResults(resultData.getStringArrayList(AutoCompleteService.RESULTS));
    }
  }


}
