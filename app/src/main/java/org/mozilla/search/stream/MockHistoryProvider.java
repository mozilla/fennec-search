package org.mozilla.search.stream;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.mozilla.gecko.db.BrowserContract;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a mock implementation of the search history provider.
 * Once we land SA in nightly, this class can be removed.
 */
public class MockHistoryProvider extends ContentProvider {

    public static final Uri CONTENT_URI = BrowserContract.SEARCH_HISTORY_AUTHORITY_URI;
    public static final String QUERY_COL = BrowserContract.SearchHistory.QUERY;

    private static final String HISTORY_PREF = "HISTORY";
    private static final String HISTORY_ENT = "ENTRIES";

    private ArrayList<String> queries;

    public MockHistoryProvider() {
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (queries == null) {
            queries = getFromSp();
        }

        // Assume selectionArgs[0] is the query we want to delete
        final int index = queries.indexOf(selectionArgs[0]);
        if (index >= 0) {
            queries.remove(index);
            writeToSp(queries);
            return 1;
        }

        return 0;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (queries == null) {
            queries = getFromSp();
        }
        String query = values.getAsString(QUERY_COL);

        if (queries.contains(query)) {
            return null;
        }
        while (queries.size() >= 10) {
            queries.remove(queries.size() - 1);
        }

        queries.add(0, query);

        writeToSp(queries);
        getContext().getContentResolver().notifyChange(BrowserContract.SearchHistory.CONTENT_URI, null);

        return null;
    }

    private ArrayList<String> getFromSp() {
        String json = getContext().getSharedPreferences(HISTORY_PREF, 0).getString(HISTORY_ENT, null);
        ArrayList<String> res = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray jsArray = new JSONArray(json);
                for (int i = 0; i < jsArray.length(); i++) {
                    res.add(jsArray.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    private void writeToSp(List<String> queries) {
        JSONArray jsArray = new JSONArray(queries);
        SharedPreferences settings = getContext().getSharedPreferences(HISTORY_PREF, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(HISTORY_ENT, jsArray.toString());
        editor.commit();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        if (queries == null) {
            queries = getFromSp();
        }

        MatrixCursor mCursor = new MatrixCursor(new String[]{"_id", QUERY_COL});
        for (String q : queries) {
            mCursor.addRow(new String[]{String.valueOf(q.hashCode()), q});
        }
        mCursor.setNotificationUri(
                getContext().getContentResolver(), BrowserContract.SearchHistory.CONTENT_URI);
        return mCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
