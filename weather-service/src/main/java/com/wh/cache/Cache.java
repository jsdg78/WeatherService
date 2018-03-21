package com.wh.cache;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

/**
 * A simple generic cache.
 */
public interface Cache<K, V> {

  /**
   * Returns true if this cache contains an entry for the specified key.
   */
  boolean containsKey(K key);

  /**
   * Returns the value to which the specified key is mapped, or null if this cache contains no entry
   * for the key.
   */
  V get(K key);

  /**
   * Associates the specified value with the specified key in this cache. If the cache previously
   * contained an entry for the key, the old value is replaced by the specified value.
   */
  void put(K key, V value);

  /**
   * Removes the entry for a key from this cache if present.
   */
  V remove(K key);

  /**
   * Removes all expired entries from this cache.
   */
  void cleanup();

  /**
   * Removes all of the entries from this cache.
   */
  void clear();

  /**
   * Returns the number of entries in this cache.
   */
  int size();

  // In Java 8.0, interfaces have been enhanced to provide method implementations
  // using the default and static keywords (almost like an abstract class).
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
  default void startCleanerThread(final long cleanupIntervalMillis, Logger logger) {
    Thread thread = new Thread(() -> {
      while (true) {
        try {
          Thread.sleep(cleanupIntervalMillis);
        } catch (InterruptedException e) {
          logger.warn("Cleaner thread interrupted.", e);
          // Restore the interrupted state.
          Thread.currentThread().interrupt();
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
