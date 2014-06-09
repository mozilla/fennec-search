package com.mozilla.fennec.search.models.types;

import android.net.Uri;

import java.io.Serializable;

public class URI implements Serializable {
  private String uri;

  public URI(String uri) {
    setUri(uri);
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = Uri.parse(uri).toString();
  }

  public String toString() {
    return uri;
  }
}
