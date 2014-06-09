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
import com.mozilla.fennec.search.events.AutoCompleteJumpEvent;
import com.mozilla.fennec.search.events.AutoCompleteSelectEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class AutoCompleteFragment extends Fragment {

  private static final int MAX_ROWS = 3;
  private static final String[] START_QUERIES = {"sf giants", "30 rock", "coffee"};
  private LinearLayout resultListView;
  private View rootView;
  private List<AutoCompleteRow> rowPool;

  public AutoCompleteFragment() {
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.auto_complete_widget, container, false);
    resultListView = (LinearLayout) rootView.findViewById(R.id.result_list);

    rowPool = new ArrayList<AutoCompleteRow>();

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
    resultListView = null;
    rootView = null;
    rowPool = null;
  }

  public void clearResults() {
    for (int i = 0; i < MAX_ROWS; i++) {
      rowPool.get(i).setText("");
    }
  }

  public void setResults(List<String> results) {
    int numResults = Math.min(results.size(), MAX_ROWS);
    for (int i = 0; i < MAX_ROWS; i++) {
      rowPool.get(i).setText(results.get(i));
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
          AutoCompleteSelectEvent event = new AutoCompleteSelectEvent(getText());
          EventBus.getDefault().post(event);
        }
      });
      jumpButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Log.i("ClickHandler", "Row Button");
          AutoCompleteJumpEvent event = new AutoCompleteJumpEvent();
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
