package com.mozilla.fennec.search.models.entity;

import android.net.Uri;

import com.mozilla.fennec.search.models.HasTitle;

public class EntityModel implements HasTitle {
  private Uri mThumbnail;
  private String mTitle;
  private String mDescription;
  private Uri mReference;
  private int mIconDrawable;

  public EntityModel(Uri thumbnail, String title, String description, Uri reference, int icon) {
    this.mThumbnail = thumbnail;
    this.mTitle = title;
    this.mDescription = description;
    this.mReference = reference;
    this.mIconDrawable = icon;
  }

  public Uri getThumbnail() {
    return mThumbnail;
  }

  public String getDescription() {
    return mDescription;
  }

  public Uri getReference() {
    return mReference;
  }

  @Override
  public String getTitle() {
    return mTitle;
  }

  @Override
  public void setTitle(String title) {
    mTitle = title;
  }

  @Override
  public int getIcon() {
    return mIconDrawable;
  }

  @Override
  public void setIcon(int icon) {
    mIconDrawable = icon;
  }
}
