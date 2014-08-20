package org.mozilla.search.test;

import android.net.Uri;
import android.test.InstrumentationTestCase;

import org.mozilla.search.providers.SearchEngine;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class SearchEngineTest extends InstrumentationTestCase {

    private SearchEngine engine;

    @Override
    protected void setUp() {
        // This is a copy of /toolkit/components/search/tests/xpcshell/data/engine.xml
        try {
            final InputStream in = getInstrumentation().getContext().getResources().getAssets().open("engine.xml");
            try {
                engine = new SearchEngine("engine", in);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public void testName() {
        assertEquals("Test search engine", engine.getName());
    }

    public void testIsResultsPage() {
        assertFalse(engine.isSearchResultsPage("http://example.com"));
        assertTrue(engine.isSearchResultsPage("http://www.google.com/search"));
    }

    public void testResultsUri() {
        final String query = "test query";
        final Uri uri = Uri.parse(engine.resultsUriForQuery(query));

        assertEquals("www.google.com", uri.getAuthority());
        assertEquals(query, uri.getQueryParameter("q"));
        assertEquals("utf-8", uri.getQueryParameter("ie"));
    }

    public void testSuggestionsTemplate() {
        final String query = "test query";
        final Uri uri = Uri.parse(engine.getSuggestionTemplate(query));

        assertEquals("suggestqueries.google.com", uri.getAuthority());
        assertEquals(query, uri.getQueryParameter("q"));
        assertEquals(Locale.getDefault().toString(), uri.getQueryParameter("hl"));
        assertEquals("firefox", uri.getQueryParameter("client"));
    }
}
