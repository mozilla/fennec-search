/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.search;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import org.mozilla.gecko.Telemetry;
import org.mozilla.gecko.TelemetryContract;
import org.mozilla.gecko.db.BrowserContract;
import org.mozilla.gecko.db.BrowserContract.SearchHistory;
import org.mozilla.search.AcceptsSearchQuery.SuggestionAnimation;

/**
 * This fragment is responsible for managing the card stream.
 */
public class PreSearchFragment extends Fragment {
    private static final String LOG_TAG = "PreSearchFragment";

    private AcceptsSearchQuery searchListener;
    private CursorAdapter cursorAdapter;
    private ContentResolver contentResolver;

    private ListView listView;

    private static final String[] PROJECTION = new String[]{ SearchHistory.QUERY, SearchHistory._ID };

    private int originalHeight;

    // Limit search history query results to 5 items. This value matches the number of search
    // suggestions we return in SearchFragment.
    private static final Uri SEARCH_HISTORY_URI = SearchHistory.CONTENT_URI.buildUpon().
            appendQueryParameter(BrowserContract.PARAM_LIMIT, String.valueOf(Constants.SUGGESTION_MAX)).build();

    private static final int LOADER_ID_SEARCH_HISTORY = 1;

    public PreSearchFragment() {
        // Mandatory empty constructor for Android's Fragment.
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof AcceptsSearchQuery) {
            searchListener = (AcceptsSearchQuery) activity;
            contentResolver = activity.getContentResolver();
        } else {
            throw new ClassCastException(activity.toString() + " must implement AcceptsSearchQuery.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        searchListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID_SEARCH_HISTORY, null, new SearchHistoryLoaderCallbacks());
        cursorAdapter = new SearchHistoryAdapter(getActivity(), null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(LOADER_ID_SEARCH_HISTORY);
        cursorAdapter.swapCursor(null);
        cursorAdapter = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        final View mainView = inflater.inflate(R.layout.search_fragment_pre_search, container, false);

        // Initialize listview.
        listView = (ListView) mainView.findViewById(R.id.list_view);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Cursor c = cursorAdapter.getCursor();
                if (c == null || !c.moveToPosition(position)) {
                    return;
                }
                final String query = c.getString(c.getColumnIndexOrThrow(SearchHistory.QUERY));
                if (!TextUtils.isEmpty(query)) {
                    final Rect startBounds = new Rect();
                    view.getGlobalVisibleRect(startBounds);

                    Telemetry.sendUIEvent(TelemetryContract.Event.SEARCH, TelemetryContract.Method.HOMESCREEN, "history");

                    searchListener.onSearch(query, new SuggestionAnimation() {
                        @Override
                        public Rect getStartBounds() {
                            return startBounds;
                        }
                    });
                }
            }
        });

        final SwipeGestureListener swipeGestureListener = new SwipeGestureListener(listView, new OnSwipeRemoveListener() {
            @Override
            public void onRemove(View v) {
                final String query = ((TextView) v).getText().toString();
                Log.i(LOG_TAG, "*** removing query: " + query);

                final AsyncTask<Void, Void, Boolean> clearHistoryTask = new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        final int numDeleted = contentResolver.delete(
                                BrowserContract.SearchHistory.CONTENT_URI,
                                BrowserContract.SearchHistory.QUERY + " = ? ",
                                new String[] { query });
                        return numDeleted >= 0;
                    }

                    @Override
                    protected void onPostExecute(Boolean success) {
                        if (!success) {
                            Log.e(LOG_TAG, "Error removing query: " + query);
                        }
                    }
                };
                clearHistoryTask.execute();
            }
        });
        listView.setOnScrollListener(swipeGestureListener);
        listView.setOnTouchListener(swipeGestureListener);

        // Apply click handler to settings button.
        mainView.findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchPreferenceActivity.class));
            }
        });
        return mainView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listView.setAdapter(null);
        listView = null;
    }

    private class SearchHistoryLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), SEARCH_HISTORY_URI, PROJECTION, null, null,
                    SearchHistory.DATE_LAST_VISITED + " DESC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (cursorAdapter != null) {
                cursorAdapter.swapCursor(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (cursorAdapter != null) {
                cursorAdapter.swapCursor(null);
            }
        }
    }

    /**
     * Cursor adapter for the list of search history items.
     */
    private class SearchHistoryAdapter extends CursorAdapter {
        public SearchHistoryAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            if (cursor == null) {
                return;
            }

            final String query = cursor.getString(cursor.getColumnIndexOrThrow(SearchHistory.QUERY));
            ((TextView) view).setText(query);

            // Reset properties that might have been changed in animation.
            if (originalHeight != 0) {
                final ViewGroup.LayoutParams lp = view.getLayoutParams();
                lp.height = originalHeight;
                view.setLayoutParams(lp);
            }

            ViewHelper.setTranslationX(view, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.search_card_history, parent, false);
        }
    }

    private interface OnSwipeRemoveListener {
        public void onRemove(View v);
    }

    private class SwipeGestureListener implements AbsListView.OnScrollListener, View.OnTouchListener {
        // same value the stock browser uses for after drag animation velocity in pixels/sec
        // http://androidxref.com/4.0.4/xref/packages/apps/Browser/src/com/android/browser/NavTabScroller.java#61
        private static final float MIN_VELOCITY = 750;

        private final AbsListView absListView;
        private final OnSwipeRemoveListener listener;

        private final int swipeThreshold;
        private final int minFlingVelocity;
        private final int maxFlingVelocity;

        private int listWidth;
        private VelocityTracker velocityTracker;
        private View swipeView;
        private Runnable pendingCheckForTap;

        private float swipeStartX;
        private boolean swiping;

        private boolean enabled = true;

        public SwipeGestureListener(AbsListView absListView, OnSwipeRemoveListener listener) {
            this.absListView = absListView;
            this.listener = listener;

            final ViewConfiguration vc = ViewConfiguration.get(absListView.getContext());
            swipeThreshold = vc.getScaledTouchSlop();
            minFlingVelocity = (int) (absListView.getContext().getResources().getDisplayMetrics().density * MIN_VELOCITY);
            maxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            enabled = (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
        }

        @Override
        public void onScroll(AbsListView view, int i, int i1, int i2) {
        }

        @Override
        public boolean onTouch(View view, MotionEvent e) {
            if (!enabled) {
                return false;
            }

            if (listWidth == 0) {
                listWidth = absListView.getWidth();
            }

            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: {
                    // Check if we should set pressed state on the
                    // touched view after a standard delay.
                    triggerCheckForTap();

                    final float x = e.getRawX();
                    final float y = e.getRawY();

                    // Find out which view is being touched
                    swipeView = findViewAt(x, y);

                    if (swipeView != null) {
                        swipeStartX = e.getRawX();

                        velocityTracker = VelocityTracker.obtain();
                        velocityTracker.addMovement(e);
                    }

                    view.onTouchEvent(e);
                    return true;
                }

                case MotionEvent.ACTION_UP: {
                    if (swipeView == null) {
                        break;
                    }

                    cancelCheckForTap();
                    swipeView.setPressed(false);

                    if (!swiping) {
                        velocityTracker.recycle();
                        velocityTracker = null;
                        break;
                    }

                    velocityTracker.addMovement(e);
                    velocityTracker.computeCurrentVelocity(1000, maxFlingVelocity);

                    final float velocityX = Math.abs(velocityTracker.getXVelocity());
                    final float velocityY = Math.abs(velocityTracker.getYVelocity());

                    boolean dismiss = false;
                    boolean dismissDirection = false;

                    final float deltaX = ViewHelper.getTranslationX(swipeView);
                    if (Math.abs(deltaX) > listWidth / 2) {
                        dismiss = true;
                        dismissDirection = (deltaX > 0);
                    } else if (minFlingVelocity <= velocityX && velocityX <= maxFlingVelocity
                            && velocityY < velocityX) {
                        dismiss = swiping && (deltaX * velocityTracker.getXVelocity() > 0);
                        dismissDirection = (velocityTracker.getXVelocity() > 0);
                    }

                    if (dismiss) {
                        final int dismissTranslation = (dismissDirection ? listWidth : -listWidth);
                        animateRemove(swipeView, dismissTranslation);
                    } else {
                        animateCancel(swipeView);
                    }

                    velocityTracker.recycle();
                    velocityTracker = null;
                    swipeView = null;

                    swipeStartX = 0;
                    swiping = false;

                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    if (swipeView == null || velocityTracker == null) {
                        break;
                    }
                    velocityTracker.addMovement(e);

                    final float delta = e.getRawX() - swipeStartX;
                    final boolean isSwipingToClose = Math.abs(delta) > swipeThreshold;

                    // If we're actually swiping, make sure we don't
                    // set pressed state on the swiped view.
                    if (isSwipingToClose) {
                        cancelCheckForTap();

                        swiping = true;
                        absListView.requestDisallowInterceptTouchEvent(true);

                        // Stops listview from highlighting the touched item
                        // in the list when swiping.
                        MotionEvent cancelEvent = MotionEvent.obtain(e);
                        cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                                (e.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                        absListView.onTouchEvent(cancelEvent);
                        cancelEvent.recycle();

                        ViewHelper.setTranslationX(swipeView, delta);
                        ViewHelper.setAlpha(swipeView, Math.max(0.1f, Math.min(1f,
                                1f - 2f * Math.abs(delta) / listWidth)));
                        return true;
                    }
                    break;
                }
            }
            return false;
        }

        private void animateRemove(final View view, int pos) {
            final AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(view, "alpha", 0),
                    ObjectAnimator.ofFloat(view, "translationX", pos)
            );
            set.setDuration(250);

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    animateFinishRemove(view);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            set.start();
        }

        private void animateFinishRemove(final View view) {
            // This assumes all rows have the same height;
            if (originalHeight == 0) {
                originalHeight = view.getHeight();
            }
            final Animation anim = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    view.getLayoutParams().height = Math.round((originalHeight * (1 - interpolatedTime)));
                    view.requestLayout();
                }
            };
            anim.setDuration(250);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    listener.onRemove(view);

                    final ViewGroup.LayoutParams lp = view.getLayoutParams();
                    lp.height = 0;
                    view.setLayoutParams(lp);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            view.startAnimation(anim);
        }

        private void animateCancel(final View view) {
            final AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(view, "alpha", 1),
                    ObjectAnimator.ofFloat(view, "translationX", 0)
            );
            set.setDuration(250);
            set.start();
        }

        private View findViewAt(float rawX, float rawY) {
            final Rect rect = new Rect();

            final int[] listViewCoords = new int[2];
            absListView.getLocationOnScreen(listViewCoords);

            final int x = (int) rawX - listViewCoords[0];
            final int y = (int) rawY - listViewCoords[1];

            for (int i = 0; i < absListView.getChildCount(); i++) {
                final View child = absListView.getChildAt(i);
                child.getHitRect(rect);

                if (rect.contains(x, y)) {
                    return child;
                }
            }

            return null;
        }

        private void triggerCheckForTap() {
            if (pendingCheckForTap == null) {
                pendingCheckForTap = new CheckForTap();
            }
            absListView.postDelayed(pendingCheckForTap, ViewConfiguration.getTapTimeout());
        }

        private void cancelCheckForTap() {
            if (pendingCheckForTap == null) {
                return;
            }
            absListView.removeCallbacks(pendingCheckForTap);
        }

        private class CheckForTap implements Runnable {
            @Override
            public void run() {
                if (!swiping && swipeView != null && enabled) {
                    swipeView.setPressed(true);
                }
            }
        }
    }
}
