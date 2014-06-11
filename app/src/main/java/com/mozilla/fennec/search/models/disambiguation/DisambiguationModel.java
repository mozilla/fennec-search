package com.mozilla.fennec.search.models.disambiguation;

import com.mozilla.fennec.search.models.RowListModel;
import com.mozilla.fennec.search.models.types.URI;

public class DisambiguationModel extends RowListModel<DisambiguationModel.DisambiguationEntry> {


  public DisambiguationModel(String title, int iconDrawable) {
    super(title, iconDrawable);
  }

  public static class DisambiguationEntry {
    private String title;
    private String subtitle;
    private URI thumbnail;

    public DisambiguationEntry(String title, String subtitle, URI thumbnail) {
      this.title = title;
      this.subtitle = subtitle;
      this.thumbnail = thumbnail;
    }

    public String getTitle() {
      return title;
    }

    public String getSubtitle() {
      return subtitle;
    }

    public URI getThumbnail() {
      return thumbnail;
    }
  }
}
