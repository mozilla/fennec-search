package com.mozilla.fennec.search.models;

import java.util.List;

public interface HasRows<T> extends CardModel {

  public void addRow(T row);
  public void setRows(List<T> rows);
  public void clearRows();
  public List<T> getRows();
}
