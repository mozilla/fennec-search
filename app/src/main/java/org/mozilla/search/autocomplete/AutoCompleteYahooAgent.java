/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.search.autocomplete;

import android.net.Uri;
import android.os.Looper;

import org.mozilla.search.Constants;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Get autocomplete suggestions from Yahoo.
 * <p/>
 * This implementation uses an XML pull parser.
 * is defined at https://mxr.mozilla.org/mozilla-central/source/mobile/locales/en-US/searchplugins/yahoo.xml.
 */
public class AutoCompleteYahooAgent extends AutoCompleteBaseAgent {

    private YahooXmlParser xmlParser;

    public AutoCompleteYahooAgent(Looper foregroundLooper, AcceptsSearchResults callback) throws AgentException {
        this(foregroundLooper, null, callback);
    }

    public AutoCompleteYahooAgent(Looper foregroundLooper, Looper backgroundLooper, AcceptsSearchResults callback) throws AgentException {
        super(foregroundLooper, backgroundLooper, callback);
        this.xmlParser = new YahooXmlParser();
    }

    private URL urlForQuery(String query, int numResults) {
        try {
            return new URL(String.format(Constants.YAHOO_URL_PATTERN, numResults, Uri.encode(query)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    List<AutoCompleteModel> processQueryInBackground(String queryString) {
        final URL url = urlForQuery(queryString, Constants.AUTOCOMPLETE_MAX_REQUESTS);
        if (url == null) {
            return null;
        }

        try {
            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            return xmlParser.buildResultList(urlConnection.getInputStream(), urlConnection.getContentEncoding(), Constants.AUTOCOMPLETE_MAX_REQUESTS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class YahooXmlParser {
        private final String SUGGESTION_ATTRIBUTE = "k";
        private final String SUGGESTION_TAG = "s";
        private final String YAHOO_XML_NAMESPACE = null;

        private XmlPullParser xpp;

        public YahooXmlParser() throws AgentException {
            try {
                xpp = XmlPullParserFactory.newInstance().newPullParser();
            } catch (XmlPullParserException e) {
                xpp = null;
                e.printStackTrace();
                throw new AgentException();
            }
        }

        public List<AutoCompleteModel> buildResultList(InputStream inputStream, String encoding, int numSuggestions) {
            List<AutoCompleteModel> destination = new ArrayList<AutoCompleteModel>();
            try {
                // Bind the pull parser to the autocomplete url.
                xpp.setInput(inputStream, encoding);

                // See XmlPullParser.TYPES for the supported event types. Right now,
                // we use `END_DOCUMENT` to detect when we're finished and `START_TAG` to
                // get the actual suggestion.
                int eventType = xpp.getEventType();
                int numAdded = 0;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    // Ignore nodes other than <s>.
                    if (eventType == XmlPullParser.START_TAG && xpp.getName().equals(SUGGESTION_TAG)) {
                        destination.add(new AutoCompleteModel(xpp.getAttributeValue(YAHOO_XML_NAMESPACE, SUGGESTION_ATTRIBUTE)));
                        // Stop parsing when we have filled the destination array with results.
                        if (++numAdded == numSuggestions) {
                            break;
                        }
                    }
                    eventType = xpp.next();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                resetPullParser();
            }
            return destination;
        }

        private void resetPullParser() {
            try {
                xpp.setInput(null);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
    }
}
