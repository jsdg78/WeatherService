package com.wh.cache.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wh.cache.Cache;

/**
 * A simple generic cache backed by a Map.
 * 
 * This cache can be initialized using any java.util.Map but the default implementation uses a
 * ConcurrentHashMap to achieve thread-safety while not adding too much synchronization overhead. As
 * opposed to Collections.synchronizedMap(Map<K,V>) which locks the entire collection at the object
 * level (i.e. get() and put() methods acquire a lock), a ConcurrentHashMap does the locking at a
 * much finer granularity (i.e. HashMap bucket level). This approach makes it possible to have
 * concurrent readers and writers resulting in higher throughput and scalability.
 */
public class MapBackedCache<K, V> implements Cache<K, V> {

  // Give each thread the ability to control the log level
  // for this class as the logging can be extremely verbose.
  private static final Logger LOG = (new ThreadLocal<Logger>() {
    @Override
    protected Logger initialValue() {
      return LoggerFactory.getLogger(MapBackedCache.class);
    }
  }).get();

  private final Map<K, CachedObject<V>> cache;

  private long timeToLiveMillis;

  private long cleanupIntervalMillis;

  public MapBackedCache() {
    this(new ConcurrentHashMap<K, CachedObject<V>>());
  }

  public MapBackedCache(Map<K, CachedObject<V>> cache) {
    this(cache, Cache.DEFAULT_TTL_SECS, Cache.DEFAULT_CLEANUP_INTERVAL_SECS);
  }

  public MapBackedCache(int timeToLiveSecs, int cleanupIntervalSecs) {
    this(new ConcurrentHashMap<K, CachedObject<V>>(), timeToLiveSecs, cleanupIntervalSecs);
  }

  public MapBackedCache(Map<K, CachedObject<V>> cache, int timeToLiveSecs,
      int cleanupIntervalSecs) {
    this.cache = cache;
    this.timeToLiveMillis = timeToLiveSecs * 1000;
    this.cleanupIntervalMillis = cleanupIntervalSecs * 1000;

    startCleanerThread(cleanupIntervalMillis);
    LOG.info("Cleaner thread started.");
  }

  @Override
  public boolean containsKey(K key) {
    validateKey(key);
    return cache.containsKey(key);
  }

  @Override
  public V get(K key) {
    validateKey(key);
    CachedObject<V> cachedObj = cache.get(key);
    if (cachedObj == null) {
      return null;
    }
    cachedObj.resetLastAccessed();
    LOG.info("Get: {}", cachedObj);
    return cachedObj.getValue();
  }

  @Override
  public void put(K key, V value) {
    validateKey(key);
    validateValue(value);
    CachedObject<V> cachedObj = new CachedObject<V>(value);
    cachedObj.resetLastAccessed();
    cache.put(key, cachedObj);
    LOG.info("Put: {}", cachedObj);
  }

  @Override
  public V remove(K key) {
    validateKey(key);
    CachedObject<V> cachedObj = cache.remove(key);
    LOG.info("Remove: {}", cachedObj);
    return cachedObj.getValue();
  }

  @Override
  public void cleanup() {
    List<K> keysToDelete = new ArrayList<K>();
    long now = System.currentTimeMillis();
    // Collect all keys whose values have expired...
    for (Entry<K, CachedObject<V>> entry : cache.entrySet()) {
      CachedObject<V> cachedObj = entry.getValue();
      if (cachedObj != null && (now > (cachedObj.getLastAccessed() + timeToLiveMillis))) {
        keysToDelete.add(entry.getKey());
      }
    }
    // and delete each one.
    for (K key : keysToDelete) {
      remove(key);
    }
    LOG.info("Cache has been cleaned.");
  }

  @Override
  public void clear() {
    cache.clear();
    LOG.info("Cache has been cleared.");
  }

  @Override
  public int size() {
    return cache.size();
  }
}
