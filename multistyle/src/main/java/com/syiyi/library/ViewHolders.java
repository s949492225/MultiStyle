
package com.syiyi.library;

import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import java.util.Iterator;
import java.util.NoSuchElementException;
import com.syiyi.library.MultiStyle.ViewHolder;

/** Helper class for keeping track of {@link ViewHolder}s that are currently bound. */
@SuppressWarnings("WeakerAccess")
public class ViewHolders implements Iterable<ViewHolder> {
  private final LongSparseArray<ViewHolder> holders = new LongSparseArray<>();

  @Nullable
  public ViewHolder get(ViewHolder holder) {
    return holders.get(holder.getItemId());
  }

  public void put(ViewHolder holder) {
    holders.put(holder.getItemId(), holder);
  }

  public void remove(ViewHolder holder) {
    holders.remove(holder.getItemId());
  }

  public int size() {
    return holders.size();
  }

  @Override
  public Iterator<ViewHolder> iterator() {
    return new HolderIterator();
  }

  private class HolderIterator implements Iterator<ViewHolder> {
    private int position = 0;

    @Override
    public boolean hasNext() {
      return position < holders.size();
    }

    @Override
    public ViewHolder next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      return holders.valueAt(position++);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
