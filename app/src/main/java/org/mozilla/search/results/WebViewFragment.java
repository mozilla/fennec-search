/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.search.results;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.mozilla.search.R;

public class WebViewFragment extends Fragment {

    private WebView searchResultsWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.search_activity_detail, container, false);

        searchResultsWebView = (WebView) mainView.findViewById(R.id.web_view);

        return mainView;
    }


    public void setUrl(String url) {
        searchResultsWebView.loadUrl(url);
    }
}
