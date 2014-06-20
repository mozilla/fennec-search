package org.mozilla.search.stream;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import org.mozilla.search.DetailActivity;
import org.mozilla.search.R;


public class CardStreamFragment extends ListFragment {

  private ImageView mHeaderView;
  private ArrayAdapter<PreloadAgent.TmpItem> mAdapter;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public CardStreamFragment() {
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getListView().setDivider(null);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mHeaderView = (ImageView) getLayoutInflater(savedInstanceState).inflate(R.layout.stream_header, null);
    getListView().addHeaderView(mHeaderView, null, false);
    if (null == mAdapter) {
      mAdapter = new ArrayAdapter<PreloadAgent.TmpItem>(getActivity(),
          R.layout.card, R.id.card_title, PreloadAgent.ITEMS) {
        /**
         * Return false here disables the ListView from highlighting the click events
         * for each of the items. Each card should handle its own click events.
         */
        @Override
        public boolean isEnabled(int position) {
          return false;
        }
      };
    }

    setListAdapter(mAdapter);
  }


  @Override
  public void onDetach() {
    super.onDetach();
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }

  public void handleSearch(String s) {

    String url = "https://search.yahoo.com/search?p=" + s;
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.putExtra(DetailActivity.URL_MESSAGE, url);
    i.setData(Uri.parse(url));
    startActivity(i);
  }
}
