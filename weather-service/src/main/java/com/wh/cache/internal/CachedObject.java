package com.wh.cache.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A wrapper class for an object being cached which includes a timestamp of when that object was
 * last accessed from the cache. It provides a setLastAccessed() method that a particular cache
 * implementation can call to update the object timestamp when it gets put into and everytime it
 * gets retrieved from the cache, thus making it stay inside the cache longer.
 */
public class CachedObject<V> {

  private final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /** The underlying object being cached. */
  private V value;

  /** Timestamp of when this object was last accessed from the cache. */
  private long lastAccessed;

  protected CachedObject(V value) {
    this.value = value;
  }

  protected V getValue() {
    return value;
  }

  protected long getLastAccessed() {
    return lastAccessed;
  }

  protected void setLastAccessed() {
    this.lastAccessed = System.currentTimeMillis();
  }

  private String formatLastAccessed() {
    return (sdf.format(new Date(lastAccessed)));
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("value", value)
        .append("lastAccessed", formatLastAccessed()).toString();
  }
}
