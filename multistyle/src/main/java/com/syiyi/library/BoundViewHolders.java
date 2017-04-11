
package com.syiyi.library;

import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** Helper class for keeping track of {@link MultiStyleHolder}s that are currently bound. */
@SuppressWarnings("WeakerAccess")
public class BoundViewHolders implements Iterable<MultiStyleHolder> {
  private final LongSparseArray<MultiStyleHolder> holders = new LongSparseArray<>();

  @Nullable
  public MultiStyleHolder get(MultiStyleHolder holder) {
    return holders.get(holder.getItemId());
  }

  public void put(MultiStyleHolder holder) {
    holders.put(holder.getItemId(), holder);
  }

  public void remove(MultiStyleHolder holder) {
    holders.remove(holder.getItemId());
  }

  public int size() {
    return holders.size();
  }

  @Override
  public Iterator<MultiStyleHolder> iterator() {
    return new HolderIterator();
  }

  private class HolderIterator implements Iterator<MultiStyleHolder> {
    private int position = 0;

    @Override
    public boolean hasNext() {
      return position < holders.size();
    }

    @Override
    public MultiStyleHolder next() {
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
