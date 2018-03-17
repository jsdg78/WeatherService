package com.wh.cache.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A wrapper class for an object being cached which includes the timestamp of when the object was
 * last accessed in the cache.
 */
public class CachedObject<V> {

  private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private V value;

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

  protected void resetLastAccessed() {
    this.lastAccessed = System.currentTimeMillis();
  }

  private static String formatTimeMillis(long lastAccessed) {
    return (sdf.format(new Date(lastAccessed)));
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("value", value)
        .append("lastAccessed", formatTimeMillis(lastAccessed)).toString();
  }
}
