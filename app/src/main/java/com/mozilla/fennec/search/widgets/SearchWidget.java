package com.mozilla.fennec.search.widgets;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.events.StartAutoCompleteEvent;
import com.mozilla.fennec.search.events.UserSelectAutoComplete;
import com.mozilla.fennec.search.events.UserSubmitQueryEvent;
import com.mozilla.fennec.search.events.UserTypeQueryEvent;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class SearchWidget extends Fragment {

  private EditText searchInput;
  private Button goButton;
  private Button clearButton;
  private State state;

  public SearchWidget() {
    state = State.WAITING;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.search_widget, container, false);

    EventBus.getDefault().register(this);

    searchInput = (EditText) rootView.findViewById(R.id.searchInput);
    goButton = (Button) rootView.findViewById(R.id.button_go);
    clearButton = (Button) rootView.findViewById(R.id.button_clear);

    bindTouchListener();
    bindKeyboardSearchButton();
    bindLiveTypeListener();
    bindGoButton();

    clearButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (state == State.WAITING) {
          showKeyboard();
          EventBus.getDefault().post(new StartAutoCompleteEvent());
        } else {
          state = State.WAITING;
        }
        searchInput.setText("");
        state = State.RUNNING;
      }
    });

    return rootView;
  }

  @Override
  public void onResume() {
    super.onResume();
    State prev = state;
    state = State.WAITING;
    searchInput.setText("");
    state = prev;
  }

  private void bindTouchListener() {
    searchInput.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        state = State.RUNNING;
        EventBus.getDefault().post(new StartAutoCompleteEvent());
        return false;
      }
    });
  }

  private void bindLiveTypeListener() {
    searchInput.addTextChangedListener(new TextWatcher() {

      private UserTypeQueryEvent userQueryEvent = new UserTypeQueryEvent();
      private TimerTask eventEmitter;
      private Timer timer;

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (state == State.WAITING) {
          return;
        }

        // User deleted a char with backspace.
        if (i2 == 1 && i3 == 0) {
          return;
        }

        final String queryString = charSequence.toString();
        if (timer != null) {
          timer.cancel();
          timer.purge();
        }

        userQueryEvent.setQuery(charSequence.toString());

        timer = new Timer();
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            EventBus.getDefault().post(userQueryEvent);
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

  private void bindGoButton() {
    goButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        state = State.WAITING;
        EventBus.getDefault().post(new UserSubmitQueryEvent(searchInput.getText().toString()));
      }
    });
  }

  private void bindKeyboardSearchButton() {
    searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          EventBus.getDefault().post(new UserSubmitQueryEvent(searchInput.getText().toString()));
          handled = true;
        }
        return handled;
      }
    });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    EventBus.getDefault().unregister(this);
    searchInput = null;
    goButton = null;
    clearButton = null;

  }

  public void onEvent(UserSelectAutoComplete event) {
    Log.i("EventHandler", "UserSelectAutoComplete");
    searchInput.setText(event.getQuery());
    // Move the cursor to the end.
    searchInput.setSelection(event.getQuery().length());
  }

  public void onEvent(UserSubmitQueryEvent event) {
    // Hide keyboard
    state = State.WAITING;
    searchInput.setText(event.getQuery());
    searchInput.setSelection(event.getQuery().length());
    hideKeyboard();
  }

  private void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
        Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
  }

  private void showKeyboard() {
    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
        Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(searchInput, 0);
  }

  private enum State {
    WAITING,
    RUNNING
  }
}
