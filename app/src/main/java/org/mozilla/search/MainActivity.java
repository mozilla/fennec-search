package org.mozilla.search;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import org.mozilla.search.autocomplete.AcceptsSearchQuery;
import org.mozilla.search.autocomplete.AutoCompleteFragment;
import org.mozilla.search.stream.CardStreamFragment;


public class MainActivity extends FragmentActivity implements AcceptsSearchQuery, FragmentManager
    .OnBackStackChangedListener {

  private View mMainView;

  @Override
  protected void onCreate(Bundle stateBundle) {
    super.onCreate(stateBundle);

    // Inflates the main View, which will be the host View for the fragments
    mMainView = getLayoutInflater().inflate(R.layout.activity_main, null);

    // Sets the content view for the Activity
    setContentView(mMainView);

    // Gets an instance of the support library FragmentManager
    FragmentManager localFragmentManager = getSupportFragmentManager();

    /*
     * Adds the back stack change listener defined in this Activity as the listener for the
     * FragmentManager. See the method onBackStackChanged().
     */
    localFragmentManager.addOnBackStackChangedListener(this);

    // If the incoming state of the Activity is null, sets the initial view to be thumbnails
    if (null == stateBundle) {

      // Starts a Fragment transaction to track the stack
      FragmentTransaction localFragmentTransaction = localFragmentManager
          .beginTransaction();


      localFragmentTransaction.add(R.id.fragment_frame,
          new CardStreamFragment(), Constants.CARD_STREAM_FRAGMENT);

      localFragmentTransaction.add(R.id.fragment_frame,
          new AutoCompleteFragment(), Constants.AUTO_COMPLETE_FRAGMENT);


      // Commits this transaction to display the Fragment
      localFragmentTransaction.commit();

      // The incoming state of the Activity isn't null.
    } else {


    }
  }

  @Override
  public void onBackStackChanged() {

  }

  @Override
  public void onSearch(String s) {
    Log.i("Incoming search", s);
  }
}