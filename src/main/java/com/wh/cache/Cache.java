package com.wh.cache;

import org.apache.commons.lang3.Validate;

/**
 * A simple generic cache.
 */
public interface Cache<K, V> {

  /** The default time-to-live for elements in the cache (15 minutes). */
  static final int DEFAULT_TTL_SECS = 900;

  /** The default time interval for removing expired elements (every minute). */
  static final int DEFAULT_CLEANUP_INTERVAL_SECS = 60;

  /**
   * Returns true if this cache contains a mapping for the specified key.
   */
  boolean containsKey(K key);

  /**
   * Returns the value to which the specified key is mapped, or null if this cache contains no
   * mapping for the key.
   */
  V get(K key);

  /**
   * Associates the specified value with the specified key in this cache. If the cache previously
   * contained a mapping for the key, the old value is replaced by the specified value.
   */
  void put(K key, V value);

  /**
   * Removes the mapping for a key from this cache if it is present.
   */
  V remove(K key);

  /**
   * Removes all expired mappings from this cache.
   */
  void cleanup();

  /**
   * Removes all of the mappings from this cache.
   */
  void clear();

  /**
   * Returns the number of key-value mappings in this cache.
   */
  int size();

  // In Java 8.0, interfaces have been enhanced to provide method implementations
  // using the default and static keywords (almost like an abstract class but with
  // the advantage of multiple inheritance).
  // The following are default methods for the Cache which the implementing class
  // can override if necessary (since declared as default and not static).
  /**
   * Validate the given key.
   * 
   * If key is a String, then treat null and empty the same way.
   */
  default void validateKey(K key) {
    if (key instanceof String) {
      Validate.notBlank((String) key, "key is blank");
    } else {
      Validate.notNull(key, "key is null");
    }
  }

  /**
   * Validate the given value.
   * 
   * If value is a String, then treat null and empty the same way.
   */
  default void validateValue(V value) {
    if (value instanceof String) {
      Validate.notBlank((String) value, "value is blank");
    } else {
      Validate.notNull(value, "value is null");
    }
  }

  /**
   * Starts the thread that cleans up the cache at regular intervals.
   */
  default void startCleanupTask(final long cleanupIntervalMillis) {
    // Another new feature of Java 8.0 are functional interfaces.
    // Runnable is a functional interface and therefore can be used
    // as a target for this lambda expression.
    Thread thread = new Thread(() -> {
      while (true) {
        try {
          Thread.sleep(cleanupIntervalMillis);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        cleanup();
      }
    });
    // Unlike user threads, daemon threads will not prevent the JVM from exiting
    // once all user threads are finished executing. They are low-priority threads
    // whose purpose is to provide services to user threads (e.g. garbage collection,
    // releasing resources of unused objects, etc).
    thread.setDaemon(true);
    thread.start();
  }
}
