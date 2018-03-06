package com.wf.gts.remoting.protocol;

import java.util.concurrent.atomic.AtomicLong;

public class DataVersion extends RemotingSerializable {
  private long timestamp = System.currentTimeMillis();
  private AtomicLong counter = new AtomicLong(0);

  public void assignNewOne(final DataVersion dataVersion) {
      this.timestamp = dataVersion.timestamp;
      this.counter.set(dataVersion.counter.get());
  }

  public void nextVersion() {
      this.timestamp = System.currentTimeMillis();
      this.counter.incrementAndGet();
  }

  public long getTimestamp() {
      return timestamp;
  }

  public void setTimestamp(long timestamp) {
      this.timestamp = timestamp;
  }

  public AtomicLong getCounter() {
      return counter;
  }

  public void setCounter(AtomicLong counter) {
      this.counter = counter;
  }

  @Override
  public boolean equals(final Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
          return false;

      final DataVersion that = (DataVersion) o;

      if (timestamp != that.timestamp) {
          return false;
      }

      if (counter != null && that.counter != null) {
          return counter.longValue() == that.counter.longValue();
      }

      return (null == counter) && (null == that.counter);
  }

  @Override
  public int hashCode() {
      int result = (int) (timestamp ^ (timestamp >>> 32));
      if (null != counter) {
          long l = counter.get();
          result = 31 * result + (int) (l ^ (l >>> 32));
      }
      return result;
  }
}