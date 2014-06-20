package org.mozilla.search;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.mozilla.gecko.GeckoView;
import org.mozilla.gecko.GeckoViewChrome;
import org.mozilla.gecko.GeckoViewContent;
import org.mozilla.gecko.PrefsHelper;
import org.mozilla.gecko.util.EventCallback;
import org.mozilla.gecko.util.NativeEventListener;
import org.mozilla.gecko.util.NativeJSObject;

public class DetailActivity extends Activity implements NativeEventListener {


  public final static String URL_MESSAGE = "org.mozilla.search.DetailActivity.URL";

  private static final String LOGTAG = "GeckoBrowser";
  GeckoView mGeckoView;
  TextView mPageTitle;
  private String mUrl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);

    mGeckoView = (GeckoView) findViewById(R.id.gecko_view);

    mGeckoView.setChromeDelegate(new MyGeckoViewChrome());
    mGeckoView.setContentDelegate(new MyGeckoViewContent());


    Intent intent = getIntent();
    if (null != intent) {
      String query = intent.getStringExtra(URL_MESSAGE);
      if (null != query) {
        mUrl = "https://search.yahoo.com/search?p=" + Uri.encode(query);


      }
    }

    if (null == mGeckoView.getCurrentBrowser()) {
      mGeckoView.addBrowser(mUrl);
    } else {
      mGeckoView.getCurrentBrowser().loadUrl(mUrl);
    }

//    EventDispatcher.getInstance().registerGeckoThreadListener(this, "Content:StateChange");

//    mPageTitle = (TextView) findViewById(R.id.page_title);
  }


  @Override
  public void handleMessage(String s, NativeJSObject nativeJSObject, EventCallback eventCallback) {
    Log.i("Message", s);

  }


//  @Override
//  public void onBackPressed() {
//    GeckoView.Browser selected = mGeckoView.getCurrentBrowser();
//    if (selected != null && selected.canGoBack()) {
//      selected.goBack();
//    } else {
//      moveTaskToBack(true);
//    }
//  }

  private class MyGeckoViewChrome extends GeckoViewChrome {
    @Override
    public void onReady(GeckoView view) {
      Log.i(LOGTAG, "Gecko is ready");

      PrefsHelper.setPref("devtools.debugger.remote-enabled", true);

      // The Gecko libraries have finished loading and we can use the rendering engine.
      // Let's add a browser (required) and load a page into it.
    }

  }


  private class MyGeckoViewContent extends GeckoViewContent {

    @Override
    public void onPageStart(GeckoView geckoView, GeckoView.Browser browser, String s) {
      Log.i("OnPageStart", s);
      // Only load this page if it's the Yahoo search page that we're using.
      // TODO: Make this check more robust, and allow for other search providers.
      if (s.contains("search.yahoo.com/search")) {
        super.onPageStart(geckoView, browser, s);
      } else {
        browser.stop();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(s));
        startActivity(i);
      }
    }
  }


}
