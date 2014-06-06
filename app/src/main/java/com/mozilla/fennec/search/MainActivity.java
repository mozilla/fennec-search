/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.mozilla.fennec.search;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.cardstream.CardStreamStateManager;
import com.mozilla.fennec.search.agents.DuckDuckGoAgent;
import com.mozilla.fennec.search.agents.ForecastIoAgent;
import com.mozilla.fennec.search.agents.JsonAgent;
import com.mozilla.fennec.search.agents.Query;
import com.mozilla.fennec.search.agents.WikipediaAgent;
import com.mozilla.fennec.search.agents.YelpAgent;
import com.mozilla.fennec.search.cards.IsCard;
import com.mozilla.fennec.search.events.StartAutoCompleteEvent;
import com.mozilla.fennec.search.events.UserSubmitQueryEvent;
import com.mozilla.fennec.search.events.UserTypeQueryEvent;
import com.mozilla.fennec.search.services.AutoCompleteService;
import com.mozilla.fennec.search.services.HistoryService;
import com.mozilla.fennec.search.widgets.AutoCompleteFragment;
import com.mozilla.fennec.search.widgets.SearchWidget;

import de.greenrobot.event.EventBus;

public class MainActivity extends Activity {

  private SearchWidget mSearchWidget;
  private CardStreamStateManager mCardManager;
  private AutoCompleteFragment mAutoCompleteFragment;

  private Location mCurrentLocation;
  private LocationManager mLocationManager;

  private static final String SEARCH_TAG = "SEARCH_WIDGET";
  private static final String AUTOCOMPLETE_TAG = "AUTOCOMPLETE_WIDGET";
  private static final String STREAM_TAG = "STREAM_WIDGET";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i("MainActivity", "onCreate");
    setContentView(R.layout.activity_main);

    EventBus.getDefault().register(this);

    FragmentManager fm = getFragmentManager();

    mSearchWidget = (SearchWidget) fm.findFragmentByTag(SEARCH_TAG);

    if (mSearchWidget == null) {
      FragmentTransaction txn = fm.beginTransaction();
      mSearchWidget = new SearchWidget();
      txn.add(R.id.container, mSearchWidget, SEARCH_TAG);
      txn.commit();
    }

    mAutoCompleteFragment = (AutoCompleteFragment) fm.findFragmentByTag(AUTOCOMPLETE_TAG);

    if (mAutoCompleteFragment == null) {
      FragmentTransaction txn = fm.beginTransaction();
      mAutoCompleteFragment = new AutoCompleteFragment();
      txn.add(R.id.container, mAutoCompleteFragment, AUTOCOMPLETE_TAG);
      txn.commit();
    }

    mCardManager = (CardStreamStateManager) fm.findFragmentByTag(STREAM_TAG);

    if (mCardManager == null) {
      FragmentTransaction txn = fm.beginTransaction();
      mCardManager = new CardStreamStateManager();
      txn.add(R.id.container, mCardManager, STREAM_TAG);
      txn.commit();
    }

  }

  @Override
  public void onStart() {
    super.onStart();

    mAutoCompleteFragment.getView().setVisibility(View.GONE);

    Log.i("MainActivity", "onStart");
    if (mLocationManager == null) {
      mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
    }

    // TODO: Add test for disabled GPS.
    mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
  }

  @Override
  protected void onRestart() {
    Log.i("MainActivity", "onRestart");
    super.onRestart();
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.i("MainActivity", "onResume");

    HistoryService.startActionQueryHistory(this, 3);

    if (mCardManager.isEmpty()) {

      if (mCurrentLocation != null) {
        JsonAgent weatherAgent = new ForecastIoAgent(this);
        weatherAgent.runAsync(new Query(mCurrentLocation));
      }
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Log.i("MainActivity", "onNewIntent");
  }

  @Override
  protected void onPause() {
    Log.i("MainActivity", "onPause");
    super.onPause();
  }

  @Override
  public void onStop() {
    Log.i("MainActivity", "onStop");
    super.onStop();
    mLocationManager = null;
  }

  @Override
  protected void onDestroy() {
    Log.i("MainActivity", "onDestroy");
    super.onDestroy();

    EventBus.getDefault().unregister(this);

    Fragment fragment = getFragmentManager().findFragmentByTag(STREAM_TAG);
  }

  // Start searching
  public void onEventMainThread(StartAutoCompleteEvent event) {
    mCardManager.deleteAllCards();
    mAutoCompleteFragment.getView().setVisibility(View.VISIBLE);
  }

  // Done searching
  public void onEvent(UserSubmitQueryEvent event) {
    Log.i("EventHandler", "UserSubmitQueryEvent");
    mAutoCompleteFragment.getView().setVisibility(View.GONE);
    HistoryService.startActionRecordQuery(this, event.getQuery());
    doSearch(event.getQuery());
  }

  public void onEventMainThread(IsCard card) {
    Log.i("SearchActivity::Event", "add card");
    addCard(card, false);
  }

  public void onEventMainThread(UserTypeQueryEvent event) {
    Log.i("EventHandler", "UserTypeQueryEvent");
    AutoCompleteService.startSearch(this, event.getQuery());
  }



  private void doSearch(String queryString) {
    LocationManager locationManager =
        (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    mCardManager.deleteAllCards();

    JsonAgent ddgAgent = new DuckDuckGoAgent(this);
    ddgAgent.runAsync(new Query(queryString));

    JsonAgent yelpAgent = new YelpAgent(this);
    yelpAgent.runAsync(new Query(queryString, location));

    JsonAgent wikipediaAgent = new WikipediaAgent(this);
    wikipediaAgent.runAsync(new Query(queryString, location));
  }

  public void addCard(IsCard card) {
    // By setting 'true' here, we're saying that cards
    // are automatically added and vieweable.
    mCardManager.addCard(card, true);
  }

  public void addCard(IsCard card, Boolean canDismiss) {
    mCardManager.addCard(card);
    mCardManager.showCard(card.getCardTag(), canDismiss);
  }

}
