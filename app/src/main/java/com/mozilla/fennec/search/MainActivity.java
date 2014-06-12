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
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.cardstream.CardStreamStateManager;
import com.mozilla.fennec.search.agents.DuckDuckGoAgent;
import com.mozilla.fennec.search.services.FlickrService;
import com.mozilla.fennec.search.agents.ForecastIoAgent;
import com.mozilla.fennec.search.agents.JsonAgent;
import com.mozilla.fennec.search.agents.Query;
import com.mozilla.fennec.search.agents.WikipediaAgent;
import com.mozilla.fennec.search.agents.YelpAgent;
import com.mozilla.fennec.search.cards.AcceptsCard;
import com.mozilla.fennec.search.cards.IsCard;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements AcceptsCard {

  private static final String STREAM_TAG = "STREAM_WIDGET";
  private CardStreamStateManager mCardManager;
  private Location mCurrentLocation;
  private LocationManager mLocationManager;
  private String mCurrentQuery;
  private ArrayList<IsCard> cards;

  private List<AsyncTask> startedTasks;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    cards = new ArrayList<IsCard>();
    setContentView(R.layout.activity_main);


    FragmentManager fm = getSupportFragmentManager();
    mCardManager = (CardStreamStateManager) fm.findFragmentByTag(STREAM_TAG);

    if (mCardManager == null) {
      FragmentTransaction txn = fm.beginTransaction();
      mCardManager = new CardStreamStateManager();
      txn.add(R.id.container, mCardManager, STREAM_TAG);
      txn.commit();
    }

  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
  }

  @Override
  public void onStart() {
    super.onStart();
    startedTasks = new ArrayList<AsyncTask>();
  }

  @Override
  public void onResume() {
    super.onStart();

    Log.i("MainActivity", "onStart");
    if (mLocationManager == null) {
      mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
    }

    // TODO: Add test for disabled GPS.
    mCurrentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


    if (cards.isEmpty()) {

      if (getIntent().hasExtra(AutoCompleteActivity.QUERY)) {
        String query = getIntent().getStringExtra(AutoCompleteActivity.QUERY);
        Log.i("New search start", query);
        doSearch(query);
        ((TextView) findViewById(R.id.fake_search_box)).setText(query);
        mCurrentQuery = query;
      } else {
        doSearch();
        ((TextView) findViewById(R.id.fake_search_box)).setText("");
      }
    } else {
      for (IsCard card : cards) {
        addCard(card);
      }
    }

  }

  @Override
  public void onStop() {
    super.onStop();
    mLocationManager = null;
    mCurrentLocation = null;

    for (AsyncTask task : startedTasks) {
      task.cancel(true);
    }
    startedTasks = null;
  }

  private void doSearch() {
    mCardManager.deleteAllCards();

  }

  private void doSearch(String queryString) {
    mCardManager.deleteAllCards();

    FlickrService flickr = new FlickrService(this, (ImageView) findViewById(R.id.hero));
    flickr.execute(new Query(queryString, mCurrentLocation));
    startedTasks.add(flickr);

    JsonAgent ddgAgent = new DuckDuckGoAgent(this, this);
    ddgAgent.runAsync(new Query(queryString));
    startedTasks.add(ddgAgent);

    JsonAgent yelpAgent = new YelpAgent(this, this);
    yelpAgent.runAsync(new Query(queryString, mCurrentLocation));
    startedTasks.add(yelpAgent);

  }

  public void addCard(IsCard card) {
    // By setting 'true' here, we're saying that cards
    // are automatically added and vieweable.
    addCard(card, true);
  }

  public void addCard(IsCard card, Boolean canDismiss) {
    if (!cards.contains(card)) {
      cards.add(card);
    }

    showCard(card, canDismiss);
  }

  private void showCard(IsCard card, Boolean canDismiss) {
    mCardManager.addCard(card);
    mCardManager.showCard(card.getCardTag(), canDismiss);
  }

  public void startAutoComplete(String query) {
    Intent intent = new Intent(this, AutoCompleteActivity.class);
    if (query != null)
      intent.putExtra(AutoCompleteActivity.QUERY, query);
    startActivity(intent);
  }

  public void onSearchClick(View view) {
    startAutoComplete(mCurrentQuery);
  }
}
