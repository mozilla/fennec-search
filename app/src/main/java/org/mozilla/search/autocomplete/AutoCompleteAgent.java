package org.mozilla.search.autocomplete;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import org.mozilla.search.Constants;

import java.util.Arrays;
import java.util.Comparator;

public class AutoCompleteAgent {

  HandlerThread mThread;
  Handler mMainUiHandler;
  Handler mHandler;
  WordList wordList;

  public AutoCompleteAgent(Context context, Handler mainUiHandler) {
    mThread = new HandlerThread("org.mozilla.search.autocomplete.SuggestionAgent");
    // TODO: Where to kill this thread?
    mThread.start();
    mMainUiHandler = mainUiHandler;
    mHandler = new SuggestionMessageHandler(mThread.getLooper());
    wordList = new WordList(context);
  }

  /**
   * Process the next incoming query.
   */
  public void search(String queryString) {
    // TODO check if there's a pending search.. not sure how to handle that.
    mHandler.sendMessage(mHandler.obtainMessage(0, queryString));
  }

  /**
   * This background thread runs the queries; the results get sent back through mMainUiHandler
   * <p/>
   * TODO: Refactor this wordlist search and add other search providers (eg: Yahoo)
   */
  private class SuggestionMessageHandler extends Handler {

    private SuggestionMessageHandler(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (null == msg.obj)
        return;

      String s = ((String) msg.obj).toLowerCase();

      int firstMatch = Arrays.binarySearch(wordList.getWordList(), s, new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
          if (lhs.startsWith(rhs)) {
            return 0;
          }
          return lhs.compareTo(rhs);
        }
      });

      // If there's a match, return the results to the main thread.
      if (firstMatch >= 0 && firstMatch < WordList.mWordList.length) {
        String[] res = new String[Constants.NUM_AUTO_COMPLETE_RESULTS];
        for (int i = 0; i < res.length; i++) {
          res[i] = wordList.getWordList()[i + firstMatch];
        }
        mMainUiHandler.sendMessage(Message.obtain(mMainUiHandler, 0,
            res));
      }


    }

  }

}
