package com.mozilla.fennec.search.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mozilla.fennec.search.db.SearchHistoryContract.SearchHistoryEntry;


public class SearchHistoryDbHelper extends SQLiteOpenHelper {
  public static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "SearchHistory.db";

  // SQLITE timestamp schema motivation from
  // http://androidcookbook.com/Recipe.seam?recipeId=413
  private static final String TEXT_TYPE = " TEXT";
  private static final String TIME_TYPE = " TIMESTAMP";
  private static final String COMMA_SEP = ",";
  private static final String SQL_CREATE_ENTRIES =
      "CREATE TABLE " + SearchHistoryEntry.TABLE_NAME + " (" +
          SearchHistoryEntry._ID + " INTEGER PRIMARY KEY," +
          SearchHistoryEntry.COLUMN_NAME_QUERY + TEXT_TYPE + COMMA_SEP +
          SearchHistoryEntry.COLUMN_NAME_TIMESTAMP + TIME_TYPE + " DEFAULT current_timestamp" +
          " )";

  private static final String SQL_DELETE_ENTRIES =
      "DROP TABLE IF EXISTS " + SearchHistoryEntry.TABLE_NAME;

  public SearchHistoryDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }


  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    // Currently the migration blows away the old db.
    sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
    onCreate(sqLiteDatabase);


  }
}
