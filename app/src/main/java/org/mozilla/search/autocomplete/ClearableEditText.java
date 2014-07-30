/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.search.autocomplete;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

import org.mozilla.search.R;

public class ClearableEditText extends FrameLayout {

    private static final int ANIMATION_DURATION = 200;
    private static final Interpolator BOUNCE_INTERPOLATOR = new BounceInterpolator();

    private EditText editText;
    private ImageButton clearButton;
    private InputMethodManager inputMethodManager;

    private TextListener listener;

    private boolean active;

    private boolean clearButtonVisible;

    public interface TextListener {
        public void onChange(String text);
        public void onSubmit(String text);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.clearable_edit_text, this);

        editText = (EditText) findViewById(R.id.edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) {
                    listener.onChange(s.toString());
                }

                animateClearButton(s.length() > 0);
            }
        });

        // Attach a listener for the "search" key on the keyboard.
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (listener != null && actionId == EditorInfo.IME_ACTION_SEARCH) {
                    listener.onSubmit(v.getText().toString());
                    return true;
                }
                return false;
            }
        });

        clearButton = (ImageButton) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        // Initialize clear button in invisible state.
        clearButtonVisible = false;
        ViewHelper.setScaleX(clearButton, 0);
        ViewHelper.setScaleY(clearButton, 0);

        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void setText(String text) {
        editText.setText(text);

        // Move cursor to end of search input.
        editText.setSelection(text.length());
    }

    public void setActive(boolean active) {
        if (this.active == active) {
            return;
        }
        this.active = active;

        animateClearButton(active);

        editText.setFocusable(active);
        editText.setFocusableInTouchMode(active);

        final int leftDrawable = active ? R.drawable.search_icon_active : R.drawable.search_icon_inactive;
        editText.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, 0, 0, 0);

        if (active) {
            editText.requestFocus();
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            editText.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    /**
     * Animates clear button between visible/invisible states.
     * @param visible
     */
    private void animateClearButton(boolean visible) {
        if (visible == clearButtonVisible) {
            return;
        }
        clearButtonVisible = visible;

        final float startScale = visible ? 0 : 1;
        final float endScale = visible ? 1 : 0;

        final AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(clearButton, "scaleX", startScale, endScale),
                ObjectAnimator.ofFloat(clearButton, "scaleY", startScale, endScale)
        );
        set.setDuration(ANIMATION_DURATION);
        if (visible) {
            set.setInterpolator(BOUNCE_INTERPOLATOR);
        }
        set.start();
    }

    public void setTextListener(TextListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        // When the view is active, pass touch events to child views.
        // Otherwise, intercept touch events to allow click listeners on the view to
        // fire no matter where the user clicks.
        return !active;
    }
}
