package org.mozilla.search.autocomplete;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AutoCompleteAdapter extends ArrayAdapter<AutoCompleteModel> {

  private AcceptsJumpTaps mAcceptsJumpTaps;

  public <T extends Context & AcceptsJumpTaps> AutoCompleteAdapter(Context context,
                                                                   AcceptsJumpTaps acceptsJumpTaps) {
    // Uses '0' for the template id since we are overriding getView
    // and supplying our own view.
    super(context, 0);
    mAcceptsJumpTaps = acceptsJumpTaps;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    AutoCompleteRowView view;
    TextView text;

    if (convertView == null) {
      view = new AutoCompleteRowView(getContext());
    } else {
      view = (AutoCompleteRowView) convertView;
    }

    view.setOnJumpListener(mAcceptsJumpTaps);


    AutoCompleteModel model = getItem(position);

    view.setMainText(model.getMainText());

    return view;
  }
}
