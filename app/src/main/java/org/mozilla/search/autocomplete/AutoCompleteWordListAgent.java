package org.mozilla.search.autocomplete;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import org.mozilla.search.Constants;
import org.mozilla.search.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Helper to search a word dictionary.
 * <p/>
 * From: https://developer.android.com/training/search/search.html
 */
public class AutoCompleteWordListAgent {

  //The columns we'll include in the dictionary table
  public static final String COL_WORD = "WORD";
  private static final String TAG = "DictionaryDatabase";
  private static final String DATABASE_NAME = "DICTIONARY";
  private static final String FTS_VIRTUAL_TABLE = "FTS";
  private static final int DATABASE_VERSION = 1;

  private final DatabaseOpenHelper mDatabaseOpenHelper;

  public AutoCompleteWordListAgent(Activity activity) {
    mDatabaseOpenHelper = new DatabaseOpenHelper(activity);
  }

  public Cursor getWordMatches(String query, String[] columns) {
    String selection = COL_WORD + " MATCH ?";
    String[] selectionArgs = new String[]{query + "*"};
    return query(selection, selectionArgs, columns);
  }

  public Boolean dbIsReady() {
    SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
    return true;
  }

  private Cursor query(String selection, String[] selectionArgs, String[] columns) {
    SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
    builder.setTables(FTS_VIRTUAL_TABLE);

    Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
        columns, selection, selectionArgs, null, null, null, Constants.LIMIT);

    if (cursor == null) {
      return null;
    } else if (!cursor.moveToFirst()) {
      cursor.close();
      return null;
    }
    return cursor;
  }

  private static class DatabaseOpenHelper extends SQLiteOpenHelper {

    private final Activity mActivity;;
    private SQLiteDatabase mDatabase;

    DatabaseOpenHelper(Activity activity) {
      super(activity, DATABASE_NAME, null, DATABASE_VERSION);
      mActivity = activity;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      mDatabase = db;
      mDatabase.execSQL(FTS_TABLE_CREATE);

      loadDictionary();
    }

    private static final String FTS_TABLE_CREATE =
        "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
            " USING fts3 (" +
            COL_WORD + ")";

    private void loadDictionary() {
      new Thread(new Runnable() {
        public void run() {
          try {
            mActivity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(mActivity, "Starting post-install indexing",
                    Toast.LENGTH_SHORT).show();
                Toast.makeText(mActivity, "Don't worry; Mark & Ian we'll figure out a way around " +
                        "this :)",
                    Toast.LENGTH_SHORT).show();
              }
            });
            loadWords();
            mActivity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(mActivity, "All done!", Toast.LENGTH_SHORT).show();
              }
            });
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }).start();
    }

    private void loadWords() throws IOException {
      final Resources resources = mActivity.getResources();
      InputStream inputStream = resources.openRawResource(R.raw.en_us);
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


      String sql = "INSERT INTO "+ FTS_VIRTUAL_TABLE +" VALUES (?);";
      SQLiteStatement statement = mDatabase.compileStatement(sql);
      mDatabase.beginTransaction();

      int i = 0;
      try {
        String line;
        while (null != (line = reader.readLine())) {
          i += 1;
          if (i % 10000 == 0) {
            Log.i("Prog", String.valueOf(i));
          }
          statement.clearBindings();
          statement.bindString(1, line.trim());
          statement.execute();
        }
      } finally {
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
      }
    }

    public long addWord(String word) {
      Log.i("Add", word);
      ContentValues initialValues = new ContentValues();
      initialValues.put(COL_WORD, word);

      return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
          + newVersion + ", which will destroy all old data");
      db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
      onCreate(db);
    }

  }
}