package com.mozilla.fennec.search.db;

import android.provider.BaseColumns;

public class SearchHistoryContract {
  public SearchHistoryContract() {}

  public static abstract class SearchHistoryEntry implements BaseColumns {
    public static final String TABLE_NAME = "searchHistory";
    public static final String COLUMN_NAME_QUERY = "query";
    public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    public static final String COLUMN_NAME_NULLABLE = "null";
  }
}
