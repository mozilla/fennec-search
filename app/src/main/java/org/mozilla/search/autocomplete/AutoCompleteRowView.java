package org.mozilla.search.autocomplete;


import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mozilla.search.R;

public class AutoCompleteRowView extends LinearLayout {

  private TextView mTextView;
  private Button mJumpButton;
  private AcceptsJumpTaps mOnJumpListener;

  public AutoCompleteRowView(Context context) {
    super(context);
    init();
  }

  private void init() {
    setOrientation(LinearLayout.HORIZONTAL);
    setGravity(Gravity.CENTER_VERTICAL);

    LayoutInflater inflater = (LayoutInflater) getContext()
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.auto_complete_row, this, true);

    mTextView = (TextView) findViewById(R.id.auto_complete_row_text);
    mJumpButton = (Button) findViewById(R.id.auto_complete_row_jump_button);

    mJumpButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (null == mOnJumpListener) {
          Log.e("SuggestionRow.onJump", "jump listener is null");
          return;
        }

        mOnJumpListener.onJumpTap(mTextView.getText().toString());
      }
    });
  }

  public void setMainText(String s) {
    mTextView.setText(s);
  }

  public void setOnJumpListener(AcceptsJumpTaps onJumpListener) {
    mOnJumpListener = onJumpListener;
  }
}
