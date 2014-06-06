package com.mozilla.fennec.search.widgets;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.events.AutoCompleteChangedEvent;
import com.mozilla.fennec.search.events.UserSelectAutoComplete;
import com.mozilla.fennec.search.events.UserSubmitQueryEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class AutoCompleteFragment extends Fragment {

  private static final int MAX_ROWS = 3;
  private static final String[] START_QUERIES = {"sf giants", "30 rock", "coffee"};
  private LinearLayout resultListView;
  private View rootView;
  private List<View> rowPool;

  public AutoCompleteFragment() {
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.auto_complete_widget, container, false);
    resultListView = (LinearLayout) rootView.findViewById(R.id.result_list);

    EventBus.getDefault().register(this);

    rowPool = new ArrayList<View>();

    for (int i = 0; i < MAX_ROWS; i++) {
      AutoCompleteRow row = new AutoCompleteRow(getActivity());
      row.setText(START_QUERIES[i]);
      rowPool.add(row);
      resultListView.addView(row);
    }
    return rootView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    EventBus.getDefault().unregister(this);
    resultListView = null;
    rootView = null;
    rowPool = null;
  }

  public void onEventMainThread(AutoCompleteChangedEvent event) {
    List<String> results = event.getResults();
    int numRows = Math.min(results.size(), MAX_ROWS);
    for (int i = 0; i < numRows; i++) {
      ((AutoCompleteRow) rowPool.get(i)).setText(results.get(i));
    }
  }


  private static class AutoCompleteRow extends LinearLayout {

    private TextView rowText;
    private Button jumpButton;

    public AutoCompleteRow(Context context) {
      super(context);
      LayoutInflater inflater = (LayoutInflater) context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      inflater.inflate(R.layout.auto_complete_row, this, true);
      setOrientation(HORIZONTAL);
      setLayoutParams(new LinearLayout.LayoutParams(
          LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

      rowText = (TextView) findViewById(R.id.rowText);
      jumpButton = (Button) findViewById(R.id.jumpButton);


      rowText.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          UserSubmitQueryEvent event = new UserSubmitQueryEvent(getText());
          EventBus.getDefault().post(event);
        }
      });
      jumpButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Log.i("ClickHandler", "Row Button");
          UserSelectAutoComplete event = new UserSelectAutoComplete();
          event.setQuery(getText());
          EventBus.getDefault().post(event);
        }
      });
    }

    public String getText() {
      return rowText.getText().toString();
    }

    public void setText(String s) {
      rowText.setText(s);
    }
  }
}
