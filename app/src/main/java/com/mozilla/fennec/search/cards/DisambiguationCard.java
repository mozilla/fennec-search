package com.mozilla.fennec.search.cards;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mozilla.fennec.search.AutoCompleteActivity;
import com.mozilla.fennec.search.MainActivity;
import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.models.disambiguation.DisambiguationModel;
import com.squareup.picasso.Picasso;

public class DisambiguationCard extends RowCard<DisambiguationModel.DisambiguationEntry> {
  public DisambiguationCard(Activity activity) {
    super(activity, R.layout.card_disambiguation, R.layout.card_disambiguation_row);
  }

  @Override
  protected void populateRowView(View rowView, final DisambiguationModel.DisambiguationEntry rowModel) {
    ((TextView)rowView.findViewById(R.id.title)).setText(rowModel.getTitle());
    ((TextView)rowView.findViewById(R.id.subtitle)).setText(rowModel.getSubtitle());
    if (!rowModel.getThumbnail().toString().isEmpty())
      Picasso.with(getActivity()).load(rowModel.getThumbnail().toString())
          .into((ImageView) rowView.findViewById(R.id.thumbnail));

    rowView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent searchIntent = new Intent(getActivity(), MainActivity.class);
        Log.i("New search", rowModel.getTitle());
        searchIntent.putExtra(AutoCompleteActivity.QUERY, rowModel.getTitle());
        getActivity().startActivity(searchIntent);
      }
    });
  }
}
