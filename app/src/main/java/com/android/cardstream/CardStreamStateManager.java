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


package com.android.cardstream;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ScrollView;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import com.mozilla.fennec.search.R;
import com.mozilla.fennec.search.cards.IsCard;

/**
 * A Fragment that handles a stream of cards.
 * Cards can be shown or hidden. When a card is shown it can also be marked as not-dismissible, see
 * {@link CardStreamLinearLayout#addCard(android.view.View, boolean)}.
 */
public class CardStreamStateManager extends Fragment {

  private static final int INITIAL_SIZE = 15;
  private CardStreamLinearLayout mLayout = null;
  private LinkedHashMap<String, IsCard> mVisibleCards = new LinkedHashMap<String, IsCard>(INITIAL_SIZE);
  private HashMap<String, IsCard> mHiddenCards = new HashMap<String, IsCard>(INITIAL_SIZE);
  private HashSet<String> mDismissibleCards = new HashSet<String>(INITIAL_SIZE);

  // Set the listener to handle dismissed cards by moving them to the hidden cards map.
  private CardStreamLinearLayout.OnDissmissListener mCardDismissListener =
      new CardStreamLinearLayout.OnDissmissListener() {
        @Override
        public void onDismiss(String tag) {
          dismissCard(tag);
        }
      };


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    Log.i("CardStreamStateManager::Event", "onCreateView");
    View view = inflater.inflate(R.layout.card_stream, container, false);
    mLayout = (CardStreamLinearLayout) view.findViewById(R.id.card_stream);
    mLayout.setOnDismissListener(mCardDismissListener);

    return view;
  }

  @Override
  public void onStart() {
    Log.i("CardStreamStateManager::Event", "onStart");
    super.onStart();
  }

  @Override
  public void onResume() {
    Log.i("CardStreamStateManager::Event", "onResume");
    super.onResume();
  }

  @Override
  public void onLowMemory() {
    Log.i("CardStreamStateManager::Event", "onLowMemory");
    super.onLowMemory();
  }


  @Override
  public void onDetach() {
    Log.i("CardStreamStateManager::Event", "onDetach");
    super.onDetach();
  }

  @Override
  public void onDestroy() {
    Log.i("CardStreamStateManager::Event", "onDestroy");
    super.onDestroy();
  }

  @Override
  public void onDestroyView() {
    Log.i("CardStreamStateManager::Event", "onDestroyView");
    super.onDestroyView();
    mLayout = null;
  }

  @Override
  public void onStop() {
    Log.i("CardStreamStateManager::Event", "onStop");
    super.onStop();
  }

  @Override
  public void onPause() {
    Log.i("CardStreamStateManager::Event", "onPause");
    super.onPause();
  }

  /**
   * Add a visible, dismissible card to the card stream.
   *
   * @param card
   */
  public void addCard(IsCard card) {
    final String tag = card.getCardTag();
    Log.i("Adding card", card.getCardTag());

    if (!mVisibleCards.containsKey(tag) && !mHiddenCards.containsKey(tag)) {
      final View view = card.getView();
      view.setTag(tag);
      mHiddenCards.put(tag, card);
    }
  }

  /**
   * Add and show a card.
   *
   * @param card
   * @param show
   */
  public void addCard(IsCard card, boolean show) {

    addCard(card);
    if (show) {
      showCard(card.getCardTag());
    }
  }

  public Boolean isEmpty() {
    return mVisibleCards.isEmpty() && mHiddenCards.isEmpty();
  }

  /**
   * Remove a card and return true if it has been successfully removed.
   *
   * @param tag
   * @return
   */
  public boolean removeCard(String tag) {
    // Attempt to remove a visible card first
    IsCard card = mVisibleCards.get(tag);
    if (card != null && mLayout != null) {
      // Card is visible, also remove from layout
      mVisibleCards.remove(tag);
      mLayout.removeView(card.getView());
      return true;
    } else {
      // Card is hidden, no need to remove from layout
      card = mHiddenCards.remove(tag);
      return card != null;
    }
  }

  public boolean deleteAllCards() {
    mLayout.removeAllViews();
    mVisibleCards.clear();
    mHiddenCards.clear();
    mDismissibleCards.clear();
    ViewParent parent = mLayout.getParent();

    if (parent != null && parent instanceof ScrollView) {
      ((ScrollView) parent).scrollTo(0,0);
    }
    return true;
  }

  /**
   * Show a dismissible card, returns false if the card could not be shown.
   *
   * @param tag
   * @return
   */
  public boolean showCard(String tag) {
    return showCard(tag, true);
  }

  /**
   * Show a card, returns false if the card could not be shown.
   *
   * @param tag
   * @param dismissible
   * @return
   */
  public boolean showCard(String tag, boolean dismissible) {
    final IsCard card = mHiddenCards.get(tag);
    // ensure the card is hidden and not already visible
    if (card != null && mLayout != null && !mVisibleCards.containsValue(tag)) {
      mHiddenCards.remove(tag);
      mVisibleCards.put(tag, card);
      mLayout.addCard(card.getView(), dismissible);
      if (dismissible) {
        mDismissibleCards.add(tag);
      }
      return true;
    }
    return false;
  }

  /**
   * Hides the card, returns false if the card could not be hidden.
   *
   * @param tag
   * @return
   */
  public boolean hideCard(String tag) {
    final IsCard card = mVisibleCards.get(tag);
    if (card != null) {
      mVisibleCards.remove(tag);
      mDismissibleCards.remove(tag);
      mHiddenCards.put(tag, card);

      mLayout.removeView(card.getView());
      return true;
    }
    return mHiddenCards.containsValue(tag);
  }


  private void dismissCard(String tag) {
    final IsCard card = mVisibleCards.get(tag);
    if (card != null) {
      mDismissibleCards.remove(tag);
      mVisibleCards.remove(tag);
      mHiddenCards.put(tag, card);
    }
  }


  public boolean isCardVisible(String tag) {
    return mVisibleCards.containsValue(tag);
  }

  /**
   * Returns true if the card is shown and is dismissible.
   *
   * @param tag
   * @return
   */
  public boolean isCardDismissible(String tag) {
    return mDismissibleCards.contains(tag);
  }

  /**
   * Returns the Card for this tag.
   *
   * @param tag
   * @return
   */
  public IsCard getCard(String tag) {
    final IsCard card = mVisibleCards.get(tag);
    if (card != null) {
      return card;
    } else {
      return mHiddenCards.get(tag);
    }
  }

  /**
   * Moves the view port to show the card with this tag.
   *
   * @param tag
   * @see CardStreamLinearLayout#setFirstVisibleCard(String)
   */
  public void setFirstVisibleCard(String tag) {
    final IsCard card = mVisibleCards.get(tag);
    if (card != null) {
      mLayout.setFirstVisibleCard(tag);
    }
  }

  public int getVisibleCardCount() {
    return mVisibleCards.size();
  }

  public Collection<IsCard> getVisibleCards() {
    return mVisibleCards.values();
  }


}
