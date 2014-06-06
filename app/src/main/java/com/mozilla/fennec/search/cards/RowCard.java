package com.mozilla.fennec.search.cards;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.models.RowModel;

public abstract class RowCard<T> extends TitleCard<RowModel<T>> {
  private final ViewGroup mBody;
  private int mBodyLayout;
  private int mRowLayout;

  public RowCard(Activity activity, int bodyLayout, int rowLayout) {
    super(activity);
    mBodyLayout = bodyLayout;
    mRowLayout = rowLayout;

    mBody = (ViewGroup) activity.getLayoutInflater().inflate(bodyLayout,
        (ViewGroup) activity.findViewById(R.id.card_stream), false);
    setBody(mBody);
  }

  @Override
  public void ingest(RowModel<T> model) {
    super.ingest(model);

    View rowView;
    model.getRows();

    for (final T rowModel : model.getRows()) {
      rowView = mActivity.getLayoutInflater().inflate(mRowLayout, mBody, false);
      populateRowView(rowView, rowModel);
      mBody.addView(rowView);
    }
  }

  protected abstract void populateRowView(View rowView, T rowModel);


}
