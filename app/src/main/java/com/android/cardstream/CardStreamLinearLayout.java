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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.mozilla.fennec.search.R;

import java.util.ArrayList;

/**
 * A Layout that contains a stream of card views.
 */
public class CardStreamLinearLayout extends LinearLayout {

  public static final int ANIMATION_SPEED_SLOW = 1001;
  public static final int ANIMATION_SPEED_NORMAL = 1002;
  public static final int ANIMATION_SPEED_FAST = 1003;

  private static final String TAG = "CardStreamLinearLayout";
  private final ArrayList<View> mFixedViewList = new ArrayList<View>();
  private final Rect mChildRect = new Rect();
  private CardStreamAnimator mAnimators;
  private OnDissmissListener mDismissListener = null;
  private boolean mLayouted = false;
  private boolean mSwiping = false;
  private String mFirstVisibleCardTag = null;
  private boolean mShowInitialAnimation = false;

  /**
   * Handle touch events to fade/move dragged items as they are swiped out
   */
  private OnTouchListener mTouchListener = new OnTouchListener() {

    private float mDownX;
    private float mDownY;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onTouch(final View v, MotionEvent event) {

      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        return false;
      }

      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          mDownX = event.getX();
          mDownY = event.getY();
          break;
        case MotionEvent.ACTION_CANCEL:
          resetAnimatedView(v);
          mSwiping = false;
          mDownX = 0.f;
          mDownY = 0.f;
          break;
        case MotionEvent.ACTION_MOVE: {

          float x = event.getX() + v.getTranslationX();
          float y = event.getY() + v.getTranslationY();

          mDownX = mDownX == 0.f ? x : mDownX;
          mDownY = mDownY == 0.f ? x : mDownY;

          float deltaX = x - mDownX;
          float deltaY = y - mDownY;

          if (!mSwiping && isSwiping(deltaX, deltaY)) {
            mSwiping = true;
            v.getParent().requestDisallowInterceptTouchEvent(true);
          } else {
            swipeView(v, deltaX, deltaY);
          }
        }
        break;
        case MotionEvent.ACTION_UP: {
          // User let go - figure out whether to animate the view out, or back into place
          if (mSwiping) {
            float x = event.getX() + v.getTranslationX();
            float y = event.getY() + v.getTranslationY();

            float deltaX = x - mDownX;
            float deltaY = y - mDownX;
            float deltaXAbs = Math.abs(deltaX);

            // User let go - figure out whether to animate the view out, or back into place
            boolean remove = deltaXAbs > v.getWidth() / 4 && !isFixedView(v);
            if (remove)
              handleViewSwipingOut(v, deltaX, deltaY);
            else
              handleViewSwipingIn(v, deltaX, deltaY);
          }
          mDownX = 0.f;
          mDownY = 0.f;
          mSwiping = false;
        }
        break;
        default:
          return false;
      }
      return false;
    }
  };
  private int mSwipeSlop = -1;

  private int mLastDownX;

  public CardStreamLinearLayout(Context context) {
    super(context);
    initialize(null, 0);
  }

  public CardStreamLinearLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialize(attrs, 0);
  }

  @SuppressLint("NewApi")
  public CardStreamLinearLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initialize(attrs, defStyle);
  }

  /**
   * add a card view w/ canDismiss flag.
   *
   * @param cardView   a card view
   * @param canDismiss flag to indicate this card is dismissible or not.
   */
  public void addCard(View cardView, boolean canDismiss) {
    if (cardView.getParent() == null) {
      initCard(cardView, canDismiss);

      ViewGroup.LayoutParams param = cardView.getLayoutParams();
      if (param == null)
        param = generateDefaultLayoutParams();

      super.addView(cardView, -1, param);
    }
  }

  @Override
  public void addView(View child, int index, ViewGroup.LayoutParams params) {
    if (child.getParent() == null) {
      initCard(child, true);
      super.addView(child, index, params);
    }
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    Log.d(TAG, "onLayout: " + changed);

    if (changed && !mLayouted)
      mLayouted = true;
  }

  /**
   * Check whether a user moved enough distance to start a swipe action or not.
   *
   * @param deltaX
   * @param deltaY
   * @return true if a user is swiping.
   */
  protected boolean isSwiping(float deltaX, float deltaY) {

    if (mSwipeSlop < 0) {
      //get swipping slop from ViewConfiguration;
      mSwipeSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }


    float absDeltaX = Math.abs(deltaX);

    return absDeltaX > mSwipeSlop;

  }

  /**
   * Swipe a view by moving distance
   *
   * @param child  a target view
   * @param deltaX x moving distance by x-axis.
   * @param deltaY y moving distance by y-axis.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  protected void swipeView(View child, float deltaX, float deltaY) {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      return;
    }

    if (isFixedView(child)) {
      deltaX = deltaX / 4;
    }

    float deltaXAbs = Math.abs(deltaX);
    float fractionCovered = deltaXAbs / (float) child.getWidth();

    child.setTranslationX(deltaX);
    child.setAlpha(1.f - fractionCovered);

    if (deltaX > 0)
      child.setRotationY(-15.f * fractionCovered);
    else
      child.setRotationY(15.f * fractionCovered);
  }

  protected void notifyOnDismissEvent(View child) {
    if (child == null || mDismissListener == null)
      return;

    mDismissListener.onDismiss((String) child.getTag());
  }

  /**
   * get the tag of the first visible child in this layout
   *
   * @return tag of the first visible child or null
   */
  public String getFirstVisibleCardTag() {

    final int count = getChildCount();

    if (count == 0)
      return null;

    for (int index = 0; index < count; ++index) {
      //check the position of each view.
      View child = getChildAt(index);
      if (child.getGlobalVisibleRect(mChildRect))
        return (String) child.getTag();
    }

    return null;
  }

  /**
   * Set the first visible card of this linear layout.
   *
   * @param tag tag of a card which should already added to this layout.
   */
  public void setFirstVisibleCard(String tag) {
    if (tag == null)
      return; //do nothing.

    if (mLayouted) {
      scrollToCard(tag);
    } else {
      //keep the tag for next use.
      mFirstVisibleCardTag = tag;
    }
  }

  /**
   * If this flag is set,
   * after finishing initial onLayout event, an initial animation which is defined in DefaultCardStreamAnimator is launched.
   */
  public void triggerShowInitialAnimation() {
    mShowInitialAnimation = true;
  }

  /**
   * set a OnDismissListener which called when user dismiss a card.
   *
   * @param listener
   */
  public void setOnDismissListener(OnDissmissListener listener) {
    mDismissListener = listener;
  }

  private void initialize(AttributeSet attrs, int defStyle) {

    float speedFactor = 1.f;

    if (attrs != null) {
      TypedArray a = getContext().obtainStyledAttributes(attrs,
          R.styleable.CardStream, defStyle, 0);

      if (a != null) {
        int speedType = a.getInt(R.styleable.CardStream_animationDuration, 1001);
        switch (speedType) {
          case ANIMATION_SPEED_FAST:
            speedFactor = 0.5f;
            break;
          case ANIMATION_SPEED_NORMAL:
            speedFactor = 1.f;
            break;
          case ANIMATION_SPEED_SLOW:
            speedFactor = 2.f;
            break;
        }

        String animatorName = a.getString(R.styleable.CardStream_animators);

        try {
          if (animatorName != null)
            mAnimators = (CardStreamAnimator) getClass().getClassLoader()
                .loadClass(animatorName).newInstance();
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (mAnimators == null)
            mAnimators = new DefaultCardStreamAnimator();
        }
        a.recycle();
      }
    }

    mAnimators.setSpeedFactor(speedFactor);
    mSwipeSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
//    setOnHierarchyChangeListener(mOnHierarchyChangeListener);
  }

  private void initCard(View cardView, boolean canDismiss) {
    resetAnimatedView(cardView);
    cardView.setOnTouchListener(mTouchListener);
    if (!canDismiss)
      mFixedViewList.add(cardView);
  }

  private boolean isFixedView(View v) {
    return mFixedViewList.contains(v);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private void resetAnimatedView(View child) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      return;
    }
    child.setAlpha(1.f);
    child.setTranslationX(0.f);
    child.setTranslationY(0.f);
    child.setRotation(0.f);
    child.setRotationY(0.f);
    child.setRotationX(0.f);
    child.setScaleX(1.f);
    child.setScaleY(1.f);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
  private void runShowActionAreaAnimation(View parent, View area) {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      return;
    }

    area.setPivotY(0.f);
    area.setPivotX(parent.getWidth() / 2.f);

    area.setAlpha(0.5f);
    area.setRotationX(-90.f);
    area.animate().rotationX(0.f).alpha(1.f).setDuration(400);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private void handleViewSwipingOut(final View child, float deltaX, float deltaY) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      return;
    }
    ObjectAnimator animator = mAnimators.getSwipeOutAnimator(child, deltaX, deltaY);
    if (animator != null) {
      animator.addListener(new EndAnimationWrapper() {
        @Override
        public void onAnimationEnd(Animator animation) {
          removeView(child);
          notifyOnDismissEvent(child);
        }
      });
    } else {
      removeView(child);
      notifyOnDismissEvent(child);
    }

    if (animator != null) {
      animator.setTarget(child);
      animator.start();
    }
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
  private void handleViewSwipingIn(final View child, float deltaX, float deltaY) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      return;
    }
    ObjectAnimator animator = mAnimators.getSwipeInAnimator(child, deltaX, deltaY);
    if (animator != null) {
      animator.addListener(new EndAnimationWrapper() {
        @Override
        public void onAnimationEnd(Animator animation) {
          child.setTranslationY(0.f);
          child.setTranslationX(0.f);
        }
      });
    } else {
      child.setTranslationY(0.f);
      child.setTranslationX(0.f);
    }

    if (animator != null) {
      animator.setTarget(child);
      animator.start();
    }
  }

  private void scrollToCard(String tag) {
    final int count = getChildCount();
    for (int index = 0; index < count; ++index) {
      View child = getChildAt(index);

      if (tag.equals(child.getTag())) {

        ViewParent parent = getParent();
        if (parent != null && parent instanceof ScrollView) {
          ((ScrollView) parent).smoothScrollTo(
              0, child.getTop() - getPaddingTop() - child.getPaddingTop());
        }
        return;
      }
    }
  }

  public interface OnDissmissListener {
    public void onDismiss(String tag);
  }

  /**
   * Empty default AnimationListener
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private abstract class EndAnimationWrapper implements Animator.AnimatorListener {

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }
  }//end of inner class
}
