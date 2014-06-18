package org.mozilla.search.autocomplete;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WordList {

  public static final String PATH = "dict/en.txt";
  public static String[] mWordList;


  public WordList(Context context) {
    if (null == mWordList) {

      try {
        InputStream json = context.getAssets().open(PATH);
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(json));
        int numWords = new Integer(br.readLine());
        mWordList = new String[numWords];
        while ((line = br.readLine()) != null) {
          for (int i = 0; i < numWords; i++) {
            mWordList[i] = br.readLine();
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public String[] getWordList() {
    return mWordList;
  }
}