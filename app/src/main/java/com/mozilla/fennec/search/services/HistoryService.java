package com.mozilla.fennec.search.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mozilla.fennec.search.db.SearchHistoryContract;
import com.mozilla.fennec.search.db.SearchHistoryDbHelper;
import com.mozilla.fennec.search.events.AutoCompleteChangedEvent;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;


public class HistoryService extends IntentService {
  private static final String ACTION_RECORD_QUERY = "com.mozilla.fennec.search.services.action.ACTION_RECORD_QUERY";
  private static final String ACTION_HISTORY_QUERY = "com.mozilla.fennec.search.services.action.ACTION_HISTORY_QUERY";

  private static final String TEXT = "com.mozilla.fennec.search.services.extra.TEXT";

  public HistoryService() {
    super("HistoryService");
  }

  /**
   * Record a user's query so that it can be shown back to the user later.
   * @param context
   * @param query
   */
  public static void startActionRecordQuery(Context context, String query) {
    Intent intent = new Intent(context, HistoryService.class);
    intent.setAction(ACTION_RECORD_QUERY);
    intent.putExtra(TEXT, query);
    context.startService(intent);
  }

  /**
   * Retrieve the most recent queries; up to numResults.
   * @param context
   * @param numResults
   */
  public static void startActionQueryHistory(Context context, int numResults) {
    Intent intent = new Intent(context, HistoryService.class);
    intent.setAction(ACTION_HISTORY_QUERY);
    intent.putExtra(TEXT, String.valueOf(numResults));
    context.startService(intent);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      if (ACTION_RECORD_QUERY.equals(action)) {
        final String query = intent.getStringExtra(TEXT);
        handleRecordQuery(query);
      } else if (ACTION_HISTORY_QUERY.equals(action)) {
        final String numResults = intent.getStringExtra(TEXT);
        handleHistoryQuery(numResults);
      }
    }
  }

  private void handleRecordQuery(String query) {
    SearchHistoryDbHelper mDbHelper = new SearchHistoryDbHelper(getApplicationContext());
    SQLiteDatabase db = mDbHelper.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put(SearchHistoryContract.SearchHistoryEntry.COLUMN_NAME_QUERY, query);
    db.insert(
        SearchHistoryContract.SearchHistoryEntry.TABLE_NAME,
        SearchHistoryContract.SearchHistoryEntry.COLUMN_NAME_NULLABLE,
        values);


    Log.i("HistoryService", "handleRecordQuery: " + query);

    db.close();

  }

  private void handleHistoryQuery(String numResults) {
    SearchHistoryDbHelper mDbHelper = new SearchHistoryDbHelper(getApplicationContext());
    SQLiteDatabase db = mDbHelper.getReadableDatabase();

    String[] projection = {
        SearchHistoryContract.SearchHistoryEntry.COLUMN_NAME_QUERY,
    };

    String sortOrder =
        SearchHistoryContract.SearchHistoryEntry.COLUMN_NAME_TIMESTAMP + " DESC";

    Cursor c = db.query(
        // Distinct
        true,

        // Table name
        SearchHistoryContract.SearchHistoryEntry.TABLE_NAME,

        // Which columns to use
        projection,

        // WHERE clause
        null,

        // WHERE clause args
        null,

        // GROUP BY
        null,

        // HAVING
        null,

        // ORDER BY
        sortOrder,

        // LIMIT
        "3");

    Log.i("HistoryService", String.format("Results %d", c.getCount()));


    ArrayList<String> historyResults = new ArrayList<String>();

    c.moveToFirst();
    for(int i = 0; i < c.getCount(); i++) {
      historyResults.add(
          c.getString(c.getColumnIndex(SearchHistoryContract.SearchHistoryEntry.COLUMN_NAME_QUERY)));
      c.moveToNext();
    }

    EventBus.getDefault().post(new AutoCompleteChangedEvent(historyResults));
    c.close();
    db.close();
  }

}
