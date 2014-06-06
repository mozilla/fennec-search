package com.mozilla.fennec.search.models;

import java.util.ArrayList;
import java.util.List;

public class RowModel<T> extends TitleCardModel implements HasRows<T> {
  private List<T> mRows;

  public RowModel(String title) {
    super(title);
    mRows = new ArrayList<T>();
  }

  @Override
  public void addRow(T row) {
    mRows.add(row);
  }

  @Override
  public void clearRows() {
    mRows.clear();
  }

  @Override
  public List<T> getRows() {
    return mRows;
  }

  @Override
  public void setRows(List<T> rows) {
    mRows = rows;
  }


}
